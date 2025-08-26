package com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamera

import android.content.Context
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.Nullable
import com.ai.face.base.utils.DataConvertUtils
import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.ICameraHelper
import com.serenegiant.opengl.renderer.MirrorMode
import com.serenegiant.usb.IFrameCallback
import com.serenegiant.usb.Size
import com.serenegiant.usb.UVCCamera
import com.serenegiant.usb.UVCParam
import java.nio.ByteBuffer
import java.util.Locale

class FlutterUVCCameraManager(private val cameraBuilder: FlutterCameraBuilder) {

    // 配置UVC 协议摄像头默认的分辨率，请参考你的摄像头能支持的分辨率，分辨率不用那么高关键在成像能力
    // 分辨率太高需要高性能的硬件配置。强烈建议摄像头的宽动态值 > 105DB
    companion object {
        const val UVC_CAMERA_WIDTH = 640
        const val UVC_CAMERA_HEIGHT = 480
    }

    private var mCameraHelper: ICameraHelper? = null
    private var autoAspectRatio = true   //摄像头画面自行管理，源码完全开放
    private var previewHeight = UVC_CAMERA_HEIGHT
    private var faceAIAnalysisCallBack: OnFaceAIAnalysisCallBack? = null
    private var onCameraStatuesCallBack: OnCameraStatusCallBack? = null

    private val mStateListener: ICameraHelper.StateCallback

    private val context: Context = cameraBuilder.context.applicationContext

    private var width = UVC_CAMERA_WIDTH
    private var height = UVC_CAMERA_HEIGHT
    private var reuseBitmap: Bitmap? = null

    interface OnCameraStatusCallBack {
        fun onAttach(device: UsbDevice)
        fun onDeviceOpen(device: UsbDevice, isFirstOpen: Boolean)
    }

    /**
     * 对每帧bitmap 进行分析，如果SDK上一帧还在处理就可以丢弃掉
     */
    interface OnFaceAIAnalysisCallBack {
        fun onBitmapFrame(bitmap: Bitmap)
    }


    init {
        mStateListener = object : ICameraHelper.StateCallback {
            override fun onAttach(device: UsbDevice) {
                onCameraStatuesCallBack?.onAttach(device)
            }

            override fun onDeviceOpen(device: UsbDevice, isFirstOpen: Boolean) {
                //参考https://github.com/shiyinghan/UVCAndroid demo的MultiCameraNewActivity,
                val param = UVCParam()
                param.quirks = UVCCamera.UVC_QUIRK_FIX_BANDWIDTH
                mCameraHelper?.openCamera(param)
                onCameraStatuesCallBack?.onDeviceOpen(device, isFirstOpen)
            }


            override fun onCameraOpen(device: UsbDevice) {
                var previewSize: Size? = null
                if (previewHeight > 0) {
                    // 摄像头支持的Size列表，选择一个合适分辨率和FPS。
                    val supportedSizeList = mCameraHelper?.supportedSizeList
                    if (supportedSizeList != null) {
                        for (size in supportedSizeList) {
                            if (size.height == previewHeight && size.type == 7) {
                                previewSize = size
                                break
                            }
                        }
                        if (previewSize != null) {
                            mCameraHelper?.setPreviewSize(previewSize)
                            width = previewSize.width
                            height = previewSize.height
                            if (autoAspectRatio) {
                                cameraBuilder.cameraView.setAspectRatio(width, height)
                            }
                        } else {
                            //无匹配的分辨率
                            Toast.makeText(context, "无对应的分辨率，请调试修正", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                mCameraHelper?.startPreview()

                if (cameraBuilder.cameraView != null) {
                    mCameraHelper?.addSurface(cameraBuilder.cameraView.holder.surface, true)

                    mCameraHelper?.setFrameCallback(object : IFrameCallback {
                        override fun onFrame(byteBuffer: ByteBuffer) {
                            //转为bitmap 后
                            faceAIAnalysisCallBack?.let { callback ->
//                            val t1 = System.currentTimeMillis()
                                reuseBitmap = DataConvertUtils.NV21Data2Bitmap(
                                    byteBuffer,
                                    width,
                                    height,
                                    cameraBuilder.degree,
                                    cameraBuilder.isHorizontalMirror
                                )
//                            Log.e("DataConvertUtils", "${width}转化用时：${System.currentTimeMillis() - t1}")

                                callback.onBitmapFrame(reuseBitmap!!)
                            }
                        }
                    }, UVCCamera.PIXEL_FORMAT_NV21)
                }
            }

            override fun onCameraClose(device: UsbDevice) {
                if (cameraBuilder.cameraView != null) {
//                initCameraHelper();
                    mCameraHelper?.removeSurface(cameraBuilder.cameraView.holder.surface)
                }
            }

            override fun onDeviceClose(device: UsbDevice) {
                // 空实现，保持原逻辑
            }

            override fun onDetach(device: UsbDevice) {
                // 空实现，保持原逻辑
            }

            override fun onCancel(device: UsbDevice) {
                // 空实现，保持原逻辑
            }
        }
        initCameraHelper()
        initUVCCamera()
    }


    private fun initCameraHelper() {
        if (mCameraHelper == null) {
            mCameraHelper = CameraHelper()
            mCameraHelper?.setStateCallback(mStateListener)
        }
    }

    fun setOnCameraStatuesCallBack(callBack: OnCameraStatusCallBack?) {
        onCameraStatuesCallBack = callBack
    }

    /**
     * 使用结束后, 释放 camera
     *
     */
    fun releaseCameraHelper() {
        mCameraHelper?.apply {
            setStateCallback(null)
            release()
        }
        mCameraHelper = null

        faceAIAnalysisCallBack = null
        onCameraStatuesCallBack = null
        // cameraBuilder设为null在Kotlin中如果是val则无法修改，这里保持原逻辑不做修改
    }


    /**
     * 根据摄像头的名字来选择使用哪个摄像头
     */
    private fun initUVCCamera() {
        //不同厂家生产的摄像头有点差异，请开发者自己实现匹配逻辑
        val list = mCameraHelper?.deviceList ?: return
        var isMatched = false
        for (device in list) {
            val name = device.productName
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(context, "摄像头ProductName为空", Toast.LENGTH_LONG).show()
            } else if (name!!.lowercase(Locale.getDefault()).contains(cameraBuilder.cameraKey.lowercase(Locale.getDefault()))) { //忽略大小写
                isMatched = true //匹配成功了
                mCameraHelper?.selectDevice(device)
                //角度旋转，范围为 0 90 180 270
                mCameraHelper?.previewConfig = mCameraHelper?.previewConfig?.setRotation(cameraBuilder.degree)
                //是否水平左右翻转
                if (cameraBuilder.isHorizontalMirror) {
                    mCameraHelper?.previewConfig = mCameraHelper?.previewConfig?.setMirror(
                        MirrorMode.MIRROR_HORIZONTAL)
                }
                break
            }
        }
        if (!isMatched) {
            //Demo 需要允许用户手动去选择设置，傻瓜式操作
            Toast.makeText(context, "${cameraBuilder.cameraName}匹配失败,请手动匹配", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 设置回调,给人脸识别SDK分析每帧数据，帧率15～30
     */
    fun setFaceAIAnalysis(callBack: OnFaceAIAnalysisCallBack?) {
        faceAIAnalysisCallBack = callBack
    }

    @Nullable
    fun getCurrentPreviewSize(): Size? {
        return mCameraHelper?.previewSize
    }
}