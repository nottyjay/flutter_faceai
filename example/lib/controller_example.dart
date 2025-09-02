import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_faceai/flutter_faceai.dart';

class ControllerExamplePage extends StatefulWidget {
  const ControllerExamplePage({super.key});

  @override
  State<ControllerExamplePage> createState() => _ControllerExamplePageState();
}

class _ControllerExamplePageState extends State<ControllerExamplePage> {
  // 创建控制器实例
  final FlutterUVCCameraAddFaceController _controller =
      FlutterUVCCameraAddFaceController();
  final TextEditingController _faceIdController = TextEditingController();

  @override
  void dispose() {
    // StatelessWidget版本的FlutterUVCCameraAddFaceWidget不再自动调用controller.dispose()
    // 需要在页面dispose时手动调用
    _controller.dispose();
    _faceIdController.dispose();
    super.dispose();
  }

  void _onAddFace(Uint8List? imageData) {
    if (imageData != null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('人脸添加成功！')));
    }
  }

  void _onScanFaceTips(int code, String message) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text('扫描提示: $message')));
  }

  Future<void> _saveFace() async {
    final faceId = _faceIdController.text.trim();
    if (faceId.isEmpty) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('请输入人脸ID')));
      return;
    }

    try {
      await _controller.saveFace(faceId);
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('人脸保存成功: $faceId')));
    } catch (e) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('人脸保存失败: $e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('控制器示例')),
      body: Column(
        children: [
          // 摄像头视图
          Expanded(
            flex: 3,
            child: FlutterUVCCameraAddFaceWidget(
              name: 'example_camera',
              camerakey: 'camera_key_001',
              horizontalMirror: false,
              degree: 0,
              onAddFace: _onAddFace,
              scanFaceTips: _onScanFaceTips,
              controller: _controller, // 注入控制器
            ),
          ),
          // 控制面板
          Expanded(
            flex: 1,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: [
                  TextField(
                    controller: _faceIdController,
                    decoration: const InputDecoration(
                      labelText: '人脸ID',
                      hintText: '请输入人脸ID',
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: _saveFace,
                    child: const Text('保存人脸'),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
