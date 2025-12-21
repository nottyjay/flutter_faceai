import 'package:flutter/services.dart';

/// 类似TextEditingController的控制器，用于控制FlutterUVCCameraAddFaceWidget
class FlutterUVCCameraAddFaceController {
  MethodChannel? _channel;
  bool _isDisposed = false;

  /// 内部方法：设置MethodChannel，由Widget调用
  void setChannel(MethodChannel channel) {
    if (_isDisposed) return;
    _channel = channel;
  }

  /// 保存人脸数据
  /// [faceID] 人脸ID
  Future<void> saveFace(String faceID) async {
    if (_isDisposed) {
      throw StateError('Controller has been disposed.');
    }
    if (_channel == null) {
      throw StateError('Controller is not attached to a widget. Make sure to pass this controller to FlutterUVCCameraAddFaceWidget.');
    }

    try {
      await _channel!.invokeMethod('saveFace', {'faceID': faceID});
    } on PlatformException catch (e) {
      throw PlatformException(
        code: e.code,
        message: 'Failed to save face: ${e.message}',
        details: e.details,
      );
    }
  }

  /// 释放资源
  void dispose() {
    if (_isDisposed) return;
    _isDisposed = true;
    _channel = null;
  }
}
