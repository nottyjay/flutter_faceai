import 'package:flutter/foundation.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_faceai_method_channel.dart';

abstract class FlutterFaceaiPlatform extends PlatformInterface {
  /// Constructs a FlutterFaceaiPlatform.
  FlutterFaceaiPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterFaceaiPlatform _instance = MethodChannelFlutterFaceai();

  /// The default instance of [FlutterFaceaiPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterFaceai].
  static FlutterFaceaiPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterFaceaiPlatform] when
  /// they register themselves.
  static set instance(FlutterFaceaiPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> init(String cacheDir) {
    throw UnimplementedError('init(cacheDir) has not been implemented.');
  }

  Future<void> delete(String facePath) {
    throw UnimplementedError('delete(facePath) has not been implemented.');
  }

  Future<void> clear() {
    throw UnimplementedError('clear() has not been implemented.');
  }

  Future<void> saveFaceImageByFilePath(String imageFilePath, String faceId) {
    throw UnimplementedError(
      'saveFaceImageByFilePath(imageFilePath, faceId) has not been implemented.',
    );
  }

  Future<void> saveFaceImageBytes(Uint8List imageBytes, String faceId) {
    throw UnimplementedError(
      'saveFaceImageBytes(imageBytes, faceId) has not been implemented.',
    );
  }
}

class FlutterFaceai {
  Future<void> init(String cacheDir) async {
    FlutterFaceaiConfig.instance.setCacheDir(cacheDir);
    return FlutterFaceaiPlatform.instance.init(cacheDir);
  }

  Future<String?> getPlatformVersion() {
    return FlutterFaceaiPlatform.instance.getPlatformVersion();
  }

  Future<void> delete(String facePath) {
    return FlutterFaceaiPlatform.instance.delete(facePath);
  }

  Future<void> clear() {
    return FlutterFaceaiPlatform.instance.clear();
  }

  Future<void> saveFaceImageByFilePath(String imageFilePath, String faceId) {
    return FlutterFaceaiPlatform.instance.saveFaceImageByFilePath(
      imageFilePath,
      faceId,
    );
  }

  Future<void> saveFaceImageBytes(Uint8List imageBytes, String faceId) {
    return FlutterFaceaiPlatform.instance.saveFaceImageBytes(
      imageBytes,
      faceId,
    );
  }
}

class FlutterFaceaiConfig {
  // 存储单例实例
  static final FlutterFaceaiConfig _instance = FlutterFaceaiConfig._internal();

  // 缓存目录
  String? _cacheDir;

  // 私有构造函数，防止外部实例化
  FlutterFaceaiConfig._internal();

  // 提供全局访问点
  static FlutterFaceaiConfig get instance => _instance;

  // 获取缓存目录
  String? get cacheDir => _cacheDir;

  // 设置缓存目录（支持链式调用）
  FlutterFaceaiConfig setCacheDir(String path) {
    _cacheDir = path;
    return this;
  }
}
