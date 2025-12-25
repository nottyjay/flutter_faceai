import 'dart:typed_data';

import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_faceai/flutter_faceai.dart';
import 'package:flutter_faceai/src/flutter_faceai_platform_interface.dart';
import 'package:flutter_faceai/src/flutter_faceai_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterFaceaiPlatform
    with MockPlatformInterfaceMixin
    implements FlutterFaceaiPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<void> clear() {
    // TODO: implement clear
    throw UnimplementedError();
  }

  @override
  Future<void> delete(String facePath) {
    // TODO: implement delete
    throw UnimplementedError();
  }

  @override
  Future<void> init(String cacheDir) {
    // TODO: implement init
    throw UnimplementedError();
  }

  @override
  Future<void> saveFaceImageByFilePath(String imageFilePath, String faceId) {
    // TODO: implement saveFaceImageByFilePath
    throw UnimplementedError();
  }

  @override
  Future<void> saveFaceImageBytes(Uint8List imageBytes, String faceId) {
    // TODO: implement saveFaceImageBytes
    throw UnimplementedError();
  }

  @override
  Future<void> stopSearchProcess() {
    // TODO: implement stopSearchProcess
    throw UnimplementedError();
  }
}

void main() {
  final FlutterFaceaiPlatform initialPlatform = FlutterFaceaiPlatform.instance;

  test('$MethodChannelFlutterFaceai is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterFaceai>());
  });

  test('getPlatformVersion', () async {
    FlutterFaceai flutterFaceaiPlugin = FlutterFaceai();
    MockFlutterFaceaiPlatform fakePlatform = MockFlutterFaceaiPlatform();
    FlutterFaceaiPlatform.instance = fakePlatform;

    expect(await flutterFaceaiPlugin.getPlatformVersion(), '42');
  });
}
