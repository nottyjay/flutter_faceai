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

  /// 重新搜索
  Future<void> reSearch() async {
    await _channel?.invokeMethod("reSearchFace");
  }

  /// 释放资源
  void dispose() {
    if (_isDisposed) return;
    _isDisposed = true;
    _channel = null;
  }
}
