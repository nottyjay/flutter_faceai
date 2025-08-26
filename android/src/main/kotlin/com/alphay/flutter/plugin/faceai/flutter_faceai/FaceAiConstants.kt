package com.alphay.flutter.plugin.faceai.flutter_faceai

class FaceAiConstants {

    companion object {
        //系统摄像头相关
        const val FRONT_BACK_CAMERA_FLAG = "cameraFlag"
        const val SYSTEM_CAMERA_DEGREE = "cameraDegree"

        //UVC 相机旋转 镜像管理。神奇，竟然有相机两个不同步，那分开管理
        const val RGB_UVC_CAMERA_DEGREE = "RGB_UVCCameraDegree"
        const val RGB_UVC_CAMERA_MIRROR_H = "RGB_UVCCameraHorizontalMirror"
        const val IR_UVC_CAMERA_DEGREE = "IR_UVCCameraDegree"
        const val IR_UVC_CAMERA_MIRROR_H = "IR_UVCCameraHorizontalMirror"

        //手动选择指定摄像头
        const val RGB_UVC_CAMERA_SELECT = "RGB_UVC_CAMERA_SELECT"
        const val IR_UVC_CAMERA_SELECT = "IR_UVC_CAMERA_SELECT"

        //默认匹配的摄像头关键字，但并不是所有的摄像头命名都规范会带有这种关键字样
        const val  RGB_KEY_DEFAULT ="RGB"
        const val  IR_KEY_DEFAULT="IR"
    }
}