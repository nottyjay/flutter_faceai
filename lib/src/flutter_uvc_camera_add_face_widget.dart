import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'flutter_faceai_constants.dart';
import 'flutter_uvc_camera_add_face_controller.dart';

class FlutterUVCCameraAddFaceWidget extends StatelessWidget {
  const FlutterUVCCameraAddFaceWidget({
    super.key,
    required this.name,
    required this.camerakey,
    required this.horizontalMirror,
    required this.degree,
    required this.onAddFace,
    required this.scanFaceTips,
    this.controller,
  });

  final String name;
  final String camerakey;
  final bool horizontalMirror;
  final int degree;

  final Function(Uint8List?) onAddFace;
  final Function(int, String) scanFaceTips;
  final FlutterUVCCameraAddFaceController? controller;

  Future<void> _addFaceInit_onCompleted(
    MethodCall call,
    MethodChannel channel,
  ) async {
    if (call.method == 'addFaceInit_onCompleted') {
      final result = (call.arguments as Map).cast<String, dynamic>();
      onAddFace(result['images'] as Uint8List);
    } else if (call.method == 'addFaceInit_onProcessTips') {
      String status = '';
      final code = call.arguments as int;
      switch (code) {
        //整理返回提示，2025.0815
        case no_face_repeatedly:
          status = '未识别到人脸';
          break;
        case face_too_many:
          status = '多个人脸';
          break;
        case face_too_small:
          status = "请靠近摄像头一点";
          break;
        case face_too_large:
          status = "请远离屏幕一点";
          break;
        case close_eye:
          status = "请不要闭眼";
          break;
        case head_center:
          status = "请平视正对摄像头"; //2秒后确认图像
          break;
        case tilt_head:
          status = "请勿歪头";
          break;
        case head_left:
          status = "脸偏左";
          break;
        case head_right:
          status = "脸偏右";
          break;
        case head_up:
          status = "请勿抬头";
          break;
        case head_down:
          status = "请勿低头";
          break;
      }
      scanFaceTips(code, status);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AndroidView(
      viewType: 'flutter_uvc_camera_view',
      onPlatformViewCreated: (viewId) {
        final channel = MethodChannel('flutter_uvc_camera_view_$viewId');
        channel.setMethodCallHandler(
          (call) => _addFaceInit_onCompleted(call, channel),
        );
        // 如果提供了控制器，将MethodChannel设置到控制器中
        controller?.setChannel(channel);
        _initCamera(channel);
      },
    );
  }

  Future<void> _startAddFace(MethodChannel channel) async {
    await channel.invokeMethod('addFaceInit');
  }

  // 提取初始化方法，便于复用和异常处理
  Future<void> _initCamera(MethodChannel channel) async {
    try {
      await channel.invokeMethod('init', {
        "name": name,
        "key": camerakey,
        "horizontalMirror": horizontalMirror,
        "degree": degree,
      });
      _startAddFace(channel);
    } on PlatformException catch (e) {
      print('相机初始化失败: ${e.message}');
    }
  }
}
