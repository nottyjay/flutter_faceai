import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_faceai/flutter_faceai.dart';

class TestPage extends StatefulWidget {
  const TestPage({super.key});

  @override
  State<TestPage> createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> {
  final FlutterFaceai _channel = FlutterFaceai();
  static const MethodChannel _methodChannel =
      MethodChannel('com.alphay.flutter.plugin/flutter_uvc_faceai');

  @override
  void initState() {
    super.initState();
    _setupMethodCallHandler();
  }

  void _setupMethodCallHandler() {
    _methodChannel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'createSearchProcess_onFaceMatched':
          _onFaceMatched(call.arguments);
          break;
        case 'createSearchProcess_onMostSimilar':
          _onMostSimilar(call.arguments);
          break;
      }
    });
  }

  void _onFaceMatched(dynamic arguments) {
    // arguments 包含 results (List<Map>) 和 picture (Base64 String)
    final Map<dynamic, dynamic> data = arguments as Map<dynamic, dynamic>;
    final List<dynamic>? results = data['results'] as List<dynamic>?;
    final String? picture = data['picture'] as String?;

    debugPrint('onFaceMatched: results=$results, picture=${picture?.substring(0, 50)}...');

    // TODO: 在这里处理人脸匹配结果
  }

  void _onMostSimilar(dynamic arguments) {
    final Map<dynamic, dynamic> data = arguments as Map<dynamic, dynamic>;
    final String? faceId = data['faceId'] as String?;

    debugPrint('onMostSimilar: faceId=$faceId');

    // TODO: 在这里处理最相似人脸结果
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          ElevatedButton(
            // 按钮点击事件
            onPressed: () {
              final creationParams = {
                "rgb-name": '普通RGB摄像头',
                "rgb-key": 'CAM1',
                "ir-name": '普通IR摄像头',
                "ir-key": 'CAM2',
                "horizontalMirror": false,
                "degree": 0,
                "threshold": 0.88,
                "multipe": true,
              };
              _channel.startSearch(creationParams);
            },
            // 按钮样式
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              textStyle: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.w500,
              ),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8),
              ),
            ),
            // 按钮文本
            child: const Text('打开人脸搜索M:N页面'),
          ),
        ],
      ),
    );
  }
}
