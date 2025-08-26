import 'package:flutter/material.dart';
import 'package:flutter_faceai/flutter_faceai.dart';
import 'package:flutter_faceai_example/add_face_view_page.dart';
import 'package:flutter_faceai_example/camera_view_page.dart';
import 'package:flutter_faceai_example/face_library_page.dart';
import 'package:flutter_faceai_example/multipe_search_face_view_page.dart';
import 'package:flutter_faceai_example/search_face_view_page.dart';

class TestPage extends StatelessWidget {
  const TestPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          // 嵌入原生Fragment（占满剩余空间）
          ElevatedButton(
            // 按钮点击事件
            onPressed: () {
              // 导航到FaceAiSearchUacCameraPage
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const FaceLibraryPage(),
                ),
              );
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
            child: const Text('打开人脸库页面'),
          ),
          // 嵌入原生Fragment（占满剩余空间）
          ElevatedButton(
            // 按钮点击事件
            onPressed: () {
              // 导航到FaceAiSearchUacCameraPage
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const CameraViewPage()),
              );
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
            child: const Text('打开人脸识别页面'),
          ),
          ElevatedButton(
            // 按钮点击事件
            onPressed: () {
              // 导航到FaceAiSearchUacCameraPage
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const AddFaceViewPage(),
                ),
              );
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
            child: const Text('打开人脸添加页面'),
          ),
          ElevatedButton(
            // 按钮点击事件
            onPressed: () {
              // 导航到FaceAiSearchUacCameraPage
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const SearchFaceViewPage(),
                ),
              );
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
            child: const Text('打开人脸搜索页面'),
          ),
          ElevatedButton(
            // 按钮点击事件
            onPressed: () {
              // 导航到FaceAiSearchUacCameraPage
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const MultipeSearchFaceViewPage(),
                ),
              );
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
          ElevatedButton(
            // 按钮点击事件
            onPressed: () {
              // 导航到FaceAiSearchUacCameraPage
              FlutterFaceai().clear();
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
            child: const Text('删除所有人脸'),
          ),
        ],
      ),
    );
  }
}
