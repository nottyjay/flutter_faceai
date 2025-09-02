package com.alphay.flutter.plugin.faceai.flutter_faceai.views

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import com.ai.face.base.baseImage.BaseImageCallBack
import com.ai.face.base.baseImage.BaseImageDispose
import com.ai.face.faceSearch.search.FaceSearchEngine
import com.ai.face.faceSearch.search.FaceSearchImagesManger
import com.alphay.flutter.plugin.faceai.flutter_faceai.R
import com.alphay.flutter.plugin.faceai.flutter_faceai.config.FaceImageConfig
import com.alphay.flutter.plugin.faceai.flutter_faceai.service.FlutterUVCCameraEngine
import com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamera.FlutterCameraBuilder
import com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamera.FlutterUVCCameraManager
import com.serenegiant.widget.AspectRatioSurfaceView
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import java.io.ByteArrayOutputStream

class FlutterUVCCameraView(
    private val context: Context,
    private val viewId: Int,
    private val messenger: BinaryMessenger,
    private val activityBinding: ActivityPluginBinding
) : PlatformView, MethodChannel.MethodCallHandler {

    private var nativeView: View
    private var cameraManager: FlutterUVCCameraManager? = null
    private var channel: MethodChannel
    private var baseImageDispose: BaseImageDispose? = null
    private var TAG: String = "FlutterUVCCameraView"
    private var faceBitmap: Bitmap? = null

    init {
        nativeView = LayoutInflater.from(context).inflate(R.layout.view_camera, null, false)
        channel = MethodChannel(messenger, "flutter_uvc_camera_view_" + viewId)
        channel.setMethodCallHandler(this)
    }

    private fun initCamera(param: Map<String, Any>) {
        var view: AspectRatioSurfaceView = nativeView.findViewById(R.id.surface_view)
        var cameraBuilder = FlutterCameraBuilder.Builder()
            .setCameraName(param.get("name") as String)
            .setCameraKey(param.get("key") as String)
            .setHorizontalMirror(param.get("horizontalMirror") as Boolean)
            .setDegree(param.get("degree") as Int)
            .setCameraView(view)
            .setContext(context)
            .build()
        cameraManager = FlutterUVCCameraManager(cameraBuilder)
        if (param.contains("type")) {
            if (param.get("type") == "master") {
                cameraManager!!.setFaceAIAnalysis(object :
                    FlutterUVCCameraManager.OnFaceAIAnalysisCallBack {
                    override fun onBitmapFrame(bitmap: Bitmap) {
                        FlutterUVCCameraEngine.faceSearchSetBitmap(bitmap, "RGB");
                    }
                });
                FlutterUVCCameraEngine.createSearchProcess(channel, activityBinding, param);
            } else if (param.get("type") == "slave") {
                cameraManager!!.setFaceAIAnalysis(object :
                    FlutterUVCCameraManager.OnFaceAIAnalysisCallBack {
                    override fun onBitmapFrame(bitmap: Bitmap) {
                        FlutterUVCCameraEngine.faceSearchSetBitmap(bitmap, "IR");
                    }
                });
            }
        }
    }

    private fun addFaceInit() {
        if (cameraManager == null) {
            throw RuntimeException("Camera has not init")
        }
        cameraManager!!.setFaceAIAnalysis(object :
            FlutterUVCCameraManager.OnFaceAIAnalysisCallBack {
            override fun onBitmapFrame(bitmap: Bitmap) {
                baseImageDispose!!.dispose(bitmap)
            }
        })
        baseImageDispose =
            BaseImageDispose(activityBinding!!.activity, 1, object : BaseImageCallBack() {
                override fun onCompleted(bitmap: Bitmap?, silentLiveValue: Float) {
                    var imageBytes = bitmapToByteArray(bitmap!!)
                    faceBitmap = bitmap
                    channel.invokeMethod(
                        "addFaceInit_onCompleted", mapOf(
                            "images" to imageBytes,
                            "silentLiveValue" to silentLiveValue
                        )
                    )
                }

                override fun onProcessTips(actionCode: Int) {
                    // 必须使用此方式，否则只会返回一次结果，无法做人脸识别
                    activityBinding!!.activity.runOnUiThread {
                        channel.invokeMethod("addFaceInit_onProcessTips", actionCode)
                    }
                }
            })
    }

    private fun retrySaveFace() {
        if (baseImageDispose == null)
            return
        baseImageDispose!!.retry()
    }

    private fun saveFaceImg(faceID: String?, result: MethodChannel.Result) {
        if (faceBitmap == null) {
            result.error("9999", "Face image cannot found", null)
        }
        if (faceID == null) {
            result.error("9999", "Must set faceID value", null)
        }
        var faceName = faceID + ".jpg"
        var facePathName = FaceImageConfig.getInstance().cacheSearchFaceDir + faceName
        FaceSearchImagesManger.Companion.getInstance(activityBinding.activity.application)
            .insertOrUpdateFaceImage(
                faceBitmap!!,
                facePathName,
                object : FaceSearchImagesManger.Callback {
                    override fun onFailed(msg: String) {
                        result.error("9999", "Save face image failed", null)
                    }

                    override fun onSuccess(
                        bitmap: Bitmap,
                        faceEmbedding: FloatArray
                    ) {
                        result.success(true)
                        baseImageDispose!!.retry()
                    }
                })
    }

    override fun onMethodCall(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        if (call.method.equals("init")) {
            var param: Map<String, Any> = call.arguments as Map<String, Any>
            initCamera(param)
            result.success(true)
        } else if (call.method.equals("addFaceInit")) {
            try {
                if (baseImageDispose == null) {
                    addFaceInit()
                } else {
                    retrySaveFace()
                }
                result.success(true)
            } catch (e: RuntimeException) {
                result.error("RuntimeException", e.message, null)
            }

        } else if (call.method.equals("saveFace")) {
            try {
                val faceId: String? = call.argument("faceID")
                saveFaceImg(faceId, result)
            } catch (e: RuntimeException) {
                result.error("RuntimeException", e.message, null)
            }

        } else if (call.method.equals("reSearchFace")) {
            try {
                FlutterUVCCameraEngine.retrySearchProcess()
            } catch (e: RuntimeException) {
                result.error("RuntimeException", e.message, null)
            }

        } else if (call.method.equals("stopSearchProcess")) {
            try {
                FlutterUVCCameraEngine.stopSearchProcess()
            } catch (e: RuntimeException) {
                result.error("RuntimeException", e.message, null)
            }

        }
    }


    override fun getView(): View? {
        return nativeView
    }

    override fun dispose() {
        baseImageDispose = null
        FaceSearchEngine.Companion.getInstance().stopSearchProcess()
        cameraManager?.releaseCameraHelper()
    }

    // 关键：将 Bitmap 转为字节数组（PNG 格式，无损压缩）
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        // 压缩为 PNG 格式（质量参数无效，始终无损），也可改为 JPEG 并设置质量（0-100）
        val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        if (success) {
            return outputStream.toByteArray()
        }
        return null
    }

}