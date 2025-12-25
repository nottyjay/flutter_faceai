// TODO Implement this library.import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

export 'flutter_uvc_camera_add_face_widget.dart';

class FlutterUVCCameraWidget extends StatefulWidget {
  const FlutterUVCCameraWidget({
    super.key,
    required this.name,
    required this.camerakey,
    required this.horizontalMirror,
    required this.degree,
  });

  final String name;
  final String camerakey;
  final bool horizontalMirror;
  final int degree;

  @override
  State<FlutterUVCCameraWidget> createState() =>
      _FlutterUVCCameraWidgetState();
}

class _FlutterUVCCameraWidgetState extends State<FlutterUVCCameraWidget> {
  MethodChannel? _channel;

  @override
  Widget build(BuildContext context) {
    return AndroidView(
      viewType: 'flutter_uvc_camera_view',
      onPlatformViewCreated: (viewId) {
        _channel = MethodChannel('flutter_uvc_camera_view_$viewId');
        _channel!.invokeMethod('init', {
          "name": widget.name,
          "key": widget.camerakey,
          "horizontalMirror": widget.horizontalMirror,
          "degree": widget.degree,
        });
      },
    );
  }

  @override
  void dispose() {
    // 释放视图资源
    _channel?.invokeMethod('releaseView').catchError((e) {
      debugPrint('释放摄像头资源失败: $e');
    });

    // 清空引用
    _channel = null;

    super.dispose();
  }

}