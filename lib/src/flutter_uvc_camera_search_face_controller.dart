import 'package:flutter/services.dart';

/// 类似TextEditingController的控制器，用于控制FlutterUVCCameraAddFaceWidget
class FlutterUVCCameraSearchFaceController {
  MethodChannel? _channel;
  bool _isDisposed = false;

  /// 内部方法：设置MethodChannel，由Widget调用
  void setChannel(MethodChannel channel) {
    if (_isDisposed) return;
    _channel = channel;
  }

}
