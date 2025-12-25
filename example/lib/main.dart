import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_faceai/flutter_faceai.dart';
import 'package:flutter_faceai_example/test_page.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _flutterFaceaiPlugin = FlutterFaceai();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // 获取外部存储路径（存储卡）
    Directory? externalDir;
    try {
      // 优先使用外部存储目录
      externalDir = await getDownloadsDirectory();
    } catch (e) {
      debugPrint("获取外部存储失败，使用应用文档目录: $e");
    }

    // 如果外部存储不可用，则使用应用文档目录作为备选
    Directory baseDir = externalDir ?? await getApplicationDocumentsDirectory();

    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      debugPrint('头像文件缓存至' + baseDir.path);
      _flutterFaceaiPlugin.init(baseDir.path + "/");
      _flutterFaceaiPlugin.saveFaceImageByFilePath("/storage/emulated/0/1.jpg", "jll.jpg");
    } on PlatformException {
      debugPrint('Failed to get platform version.');
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Plugin example app')),
        body: TestPage(),
      ),
    );
  }
}
