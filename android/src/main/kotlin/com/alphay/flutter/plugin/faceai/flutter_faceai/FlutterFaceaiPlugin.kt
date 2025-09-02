package com.alphay.flutter.plugin.faceai.flutter_faceai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.ai.face.faceSearch.search.FaceSearchImagesManger
import com.alphay.flutter.plugin.faceai.flutter_faceai.config.FaceImageConfig
import com.alphay.flutter.plugin.faceai.flutter_faceai.factory.FlutterUVCCameraFactory
import com.alphay.flutter.plugin.faceai.flutter_faceai.service.FlutterUVCCameraEngine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterFaceaiPlugin */
class FlutterFaceaiPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    companion object {
        val TAG = FlutterFaceaiPlugin::class.simpleName
    }

    // 保存Flutter引擎绑定实例
    private var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null
    private var activityBinding: ActivityPluginBinding? = null

    // 标记平台视图是否已注册
    private var isViewFactoryRegistered = false

    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_faceai")
        channel.setMethodCallHandler(this)
        this.flutterPluginBinding = flutterPluginBinding
    }


    // 注册平台视图工厂的方法
    private fun registerViewFactoryIfNeeded() {
        if (!isViewFactoryRegistered && flutterPluginBinding != null && activityBinding != null) {
            try {
                // 注册原生视图工厂
                flutterPluginBinding!!.platformViewRegistry.registerViewFactory(
                    "flutter_uvc_camera_view",
                    FlutterUVCCameraFactory(
                        flutterPluginBinding!!.binaryMessenger,
                        activityBinding!!
                    )
                )
                Log.i(TAG, "UI注册成功")
                isViewFactoryRegistered = true
            } catch (e: Exception) {
                // 如果注册失败，记录错误但不崩溃
                Log.e("FlutterFaceaiPlugin", "Failed to register view factory", e)
            }
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "init") {
            // 解析参数（cacheDir 是 Flutter 传递的路径）
            val cacheDir = call.argument<String>("cacheDir")
            if (cacheDir.isNullOrEmpty()) {
                // 参数错误时返回失败
                result.error("INVALID_PARAM", "cacheDir 不能为空", null)
                return
            }
            FaceImageConfig.getInstance().cacheSearchFaceDir = cacheDir
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
            registerViewFactoryIfNeeded()
        } else if (call.method == "createSearchProcess") {
            FlutterUVCCameraEngine.createSearchProcess(
                channel,
                activityBinding!!,
                call.arguments as Map<String, Any>
            )
            result.success(true)
        } else if (call.method == "stopSearchProcess") {
            FlutterUVCCameraEngine.stopSearchProcess()
            result.success(true)
        } else if (call.method.equals("delete")) {
            try {
                val facePathName: String? = call.argument("path")
                FaceSearchImagesManger.Companion.getInstance(activityBinding!!.activity.application)
                    .deleteFaceImage(facePathName!!)
                result.success(true)
            } catch (e: RuntimeException) {
                result.error("RuntimeException", e.message, null)
            }
        } else if (call.method.equals("clear")) {
            try {
                FaceSearchImagesManger.Companion.getInstance(activityBinding!!.activity.application)
                    .clearFaceImages(
                        FaceImageConfig.getInstance().cacheSearchFaceDir!!
                    )
                result.success(true)
            } catch (e: RuntimeException) {
                result.error("RuntimeException", e.message, null)
            }
        } else if (call.method.equals("addFaceImage")) {
            try {
                var imageData = call.argument<ByteArray>("imageData")
                var faceID = call.argument<String>("faceId")
                if (imageData == null) {
                    result.error("NULL_DATA", "图片数据为空", null)
                    return
                }
                val faceBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
                var faceName = faceID + ".jpg"
                var facePathName = FaceImageConfig.getInstance().cacheSearchFaceDir + faceName
                FaceSearchImagesManger.Companion.getInstance(activityBinding!!.activity.application)
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
                            }
                        })
                result.success(true)
            } catch (e: RuntimeException) {
                result.error("RuntimeException", e.message, null)
            }

        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activityBinding = binding
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.activityBinding = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.activityBinding = binding

    }

    override fun onDetachedFromActivity() {
        this.activityBinding = null
    }
}
