import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_faceai_platform_interface.dart';

/// An implementation of [FlutterFaceaiPlatform] that uses method channels.
class MethodChannelFlutterFaceai extends FlutterFaceaiPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_faceai');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }

  @override
  Future<void> init(String cacheDir) async {
    return methodChannel.invokeMethod<void>('init', {'cacheDir': cacheDir});
  }

  @override
  Future<void> delete(String facePath) async {
    return methodChannel.invokeMethod<void>('delete', {'path': facePath});
  }

  @override
  Future<void> clear() async {
    return methodChannel.invokeMethod<void>('clear');
  }

  @override
  Future<void> saveFaceImageByFilePath(String imageFilePath, String faceId) {
    return saveFaceImageBytes(File(imageFilePath).readAsBytesSync(), faceId);
  }

  @override
  Future<void> saveFaceImageBytes(Uint8List imageBytes, String faceId) async {
    return methodChannel.invokeMethod<void>('saveFaceImage', {
      'imageData': imageBytes,
      'faceId': faceId,
    });
  }
}
