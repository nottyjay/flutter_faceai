import 'package:flutter/material.dart';
import 'package:flutter_faceai/flutter_faceai.dart';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class AddFaceViewPage extends StatefulWidget {
  const AddFaceViewPage({super.key});

  @override
  State<AddFaceViewPage> createState() => _AddFaceViewPageState();
}

class _AddFaceViewPageState extends State<AddFaceViewPage> {
  Uint8List? _imageBytes; // 用于存储接收的图片字节数组
  FlutterUVCCameraAddFaceController _controller =
      FlutterUVCCameraAddFaceController();
  final TextEditingController _faceIdController = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    _faceIdController.dispose();
    super.dispose();
  }

  void _showSaveFaceDialog(Uint8List imageBytes) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          contentPadding: const EdgeInsets.all(16),
          content: SizedBox(
            width: 300,
            height: 400,
            child: Column(
              children: [
                // 上部分：显示图片
                Expanded(
                  flex: 2,
                  child: Container(
                    width: double.infinity,
                    decoration: BoxDecoration(
                      border: Border.all(color: Colors.grey),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: Image.memory(imageBytes, fit: BoxFit.cover),
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                // 中部分：输入框
                Expanded(
                  flex: 1,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        '请输入人脸ID:',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 8),
                      TextField(
                        controller: _faceIdController,
                        decoration: const InputDecoration(
                          hintText: '只允许输入文字、数字、下划线和@符号',
                          border: OutlineInputBorder(),
                        ),
                        inputFormatters: [
                          FilteringTextInputFormatter.allow(
                            RegExp(r'[a-zA-Z0-9_@\u4e00-\u9fa5]'),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 16),
                // 下部分：确定按钮
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () => _saveFace(),
                    child: const Text('确定'),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Future<void> _saveFace() async {
    final faceId = _faceIdController.text.trim();

    // 检查是否已输入
    if (faceId.isEmpty) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('请输入人脸ID')));
      return;
    }

    // 检查格式要求（文字、数字、下划线、@符号）
    final RegExp validFormat = RegExp(r'^[a-zA-Z0-9_@\u4e00-\u9fa5]+$');
    if (!validFormat.hasMatch(faceId)) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('人脸ID格式不正确，只允许输入文字、数字、下划线和@符号')),
      );
      return;
    }

    try {
      await _controller.saveFace(faceId);
      Navigator.of(context).pop(); // 关闭弹窗
      _faceIdController.clear(); // 清空输入框
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
      body: Center(
        child: Container(
          width: 300,
          height: 400,
          child: FlutterUVCCameraAddFaceWidget(
            name: '普通RGB摄像头',
            camerakey: 'RGB',
            horizontalMirror: false,
            degree: 0,
            controller: _controller,
            onAddFace: (imageBytes) {
              setState(() {
                _imageBytes = imageBytes;
              });
              // 当接收到图片数据时，弹出保存对话框
              if (imageBytes != null) {
                _showSaveFaceDialog(imageBytes);
              }
            },
            scanFaceTips: (code, status) {
              print('code: $code, status: $status');
            },
          ),
        ),
      ),
    );
  }
}
