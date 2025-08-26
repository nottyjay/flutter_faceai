import 'package:flutter/material.dart';
import 'package:flutter_faceai/flutter_faceai.dart';
import 'dart:io';
import 'add_face_view_page.dart';
import 'dart:typed_data';

class FaceLibraryPage extends StatefulWidget {
  const FaceLibraryPage({super.key});
  @override
  State<FaceLibraryPage> createState() => _FaceLibraryPageState();
}

class _FaceLibraryPageState extends State<FaceLibraryPage> {
  List<String>? _faceImagesList;

  @override
  void initState() {
    super.initState();
  }

  // 获取缓存目录中的所有jpg文件
  Future<List<String>> _loadFaceImages() async {
    final cacheDir = FlutterFaceaiConfig.instance.cacheDir;
    if (cacheDir == null || cacheDir.isEmpty) {
      return [];
    }
    
    try {
      final directory = Directory(cacheDir);
      if (!await directory.exists()) {
        return [];
      }
      
      final files = await directory.list().toList();
      final jpgFiles = files
          .where((file) => file is File && file.path.toLowerCase().endsWith('.jpg'))
          .map((file) => file.path)
          .toList();
      
      return jpgFiles;
    } catch (e) {
      print('Error loading face images: $e');
      return [];
    }
  }

  // 显示图片弹窗
  void _showImageDialog(String imagePath) {
    showDialog(
      context: context,
      barrierDismissible: true,
      builder: (BuildContext context) {
        return Dialog(
          backgroundColor: Colors.transparent,
          child: Stack(
            children: [
              Center(
                child: Container(
                  constraints: BoxConstraints(
                    maxWidth: MediaQuery.of(context).size.width * 0.9,
                    maxHeight: MediaQuery.of(context).size.height * 0.8,
                  ),
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Image.file(
                      File(imagePath),
                      fit: BoxFit.contain,
                    ),
                  ),
                ),
              ),
              Positioned(
                top: 40,
                right: 20,
                child: GestureDetector(
                  onTap: () => Navigator.of(context).pop(),
                  child: Container(
                    padding: const EdgeInsets.all(8),
                    decoration: const BoxDecoration(
                      color: Colors.black54,
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.close,
                      color: Colors.white,
                      size: 24,
                    ),
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  // 获取文件名（不包含路径和扩展名）
  String _getFileName(String filePath) {
    return filePath.split(Platform.pathSeparator).last.replaceAll('.jpg', '');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('人脸库'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const AddFaceViewPage(),
                ),
              );
            },
            child: const Text(
              '添加头像',
              style: TextStyle(color: Colors.white),
            ),
          ),
        ],
      ),
      body: FutureBuilder<List<String>>(
        future: _loadFaceImages(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }
          
          if (snapshot.hasError) {
            return Center(
              child: Text('加载失败: ${snapshot.error}'),
            );
          }
          
          final imagesList = snapshot.data ?? [];
          _faceImagesList = imagesList;
          
          if (imagesList.isEmpty) {
            return const Center(
              child: Text(
                '暂无人脸数据\n点击右上角"添加头像"按钮添加人脸',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
            );
          }
          
          return Padding(
            padding: const EdgeInsets.all(8.0),
            child: GridView.builder(
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                crossAxisSpacing: 8,
                mainAxisSpacing: 8,
                childAspectRatio: 100 / 140, // 100宽 : 140高 (100图片 + 40文字)
              ),
              itemCount: imagesList.length,
              itemBuilder: (context, index) {
                final imagePath = imagesList[index];
                final fileName = _getFileName(imagePath);
                
                return GestureDetector(
                  onTap: () => _showImageDialog(imagePath),
                  child: Container(
                    decoration: BoxDecoration(
                      border: Border.all(color: Colors.grey.shade300),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Column(
                      children: [
                        // 图片部分 100x100
                        Expanded(
                          flex: 100,
                          child: Container(
                            width: double.infinity,
                            decoration: const BoxDecoration(
                              borderRadius: BorderRadius.only(
                                topLeft: Radius.circular(8),
                                topRight: Radius.circular(8),
                              ),
                            ),
                            child: ClipRRect(
                              borderRadius: const BorderRadius.only(
                                topLeft: Radius.circular(8),
                                topRight: Radius.circular(8),
                              ),
                              child: Image.file(
                                File(imagePath),
                                fit: BoxFit.cover,
                                errorBuilder: (context, error, stackTrace) {
                                  return Container(
                                    color: Colors.grey.shade200,
                                    child: const Icon(
                                      Icons.error,
                                      color: Colors.grey,
                                    ),
                                  );
                                },
                              ),
                            ),
                          ),
                        ),
                        // 文字部分 40高度
                        Container(
                          height: 40,
                          width: double.infinity,
                          padding: const EdgeInsets.symmetric(
                            horizontal: 4,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.grey.shade50,
                            borderRadius: const BorderRadius.only(
                              bottomLeft: Radius.circular(8),
                              bottomRight: Radius.circular(8),
                            ),
                          ),
                          child: Center(
                            child: Text(
                              fileName,
                              style: const TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w500,
                              ),
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                              textAlign: TextAlign.center,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }
}
