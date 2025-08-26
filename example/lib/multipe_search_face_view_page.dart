import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_faceai/flutter_faceai.dart';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class MultipeSearchFaceViewPage extends StatefulWidget {
  const MultipeSearchFaceViewPage({super.key});

  @override
  State<MultipeSearchFaceViewPage> createState() =>
      _MultipeSearchFaceViewPageState();
}

class _MultipeSearchFaceViewPageState extends State<MultipeSearchFaceViewPage> {
  List<MultipeSearchResult>? results;
  String? status;
  FlutterUVCCameraSearchFaceController _controller =
      FlutterUVCCameraSearchFaceController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _showSaveFaceDialog() {
    if (results == null || results!.isEmpty) return;

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('识别结果'),
          contentPadding: const EdgeInsets.all(16),
          content: SizedBox(
            width: 400,
            height: 500,
            child: ListView.builder(
              itemCount: results!.length,
              itemBuilder: (context, index) {
                final result = results![index];
                return Container(
                  margin: const EdgeInsets.only(bottom: 16),
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    border: Border.all(color: Colors.grey),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Column(
                    children: [
                      Container(
                        width: 150,
                        height: 150,
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(8),
                          child: Image.file(
                            File(
                              FlutterFaceaiConfig.instance.cacheDir! +
                                  result.faceId +
                                  '.jpg',
                            ),
                            fit: BoxFit.cover,
                            errorBuilder: (context, error, stackTrace) {
                              return Container(
                                color: Colors.grey[300],
                                child: Icon(
                                  Icons.person,
                                  size: 50,
                                  color: Colors.grey[600],
                                ),
                              );
                            },
                          ),
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'ID: ${result.faceId}',
                        style: const TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '得分: ${result.score.toStringAsFixed(3)}',
                        style: const TextStyle(
                          fontSize: 12,
                          color: Colors.blue,
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: const Text('关闭'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Container(
          width: 300,
          height: 400,
          child: Column(
            children: [
              if (this.status != null)
                Container(width: 300, height: 100, child: Text(this.status!))
              else
                SizedBox(height: 100),
              Container(
                width: 300,
                height: 300,
                child: FlutterUVCCameraSearchFaceWidget(
                  masterName: '普通RGB摄像头',
                  masterCamerakey: 'RGB',
                  slaveName: '普通IR摄像头',
                  slaveCamerakey: 'IR',
                  horizontalMirror: false,
                  degree: 0,
                  threshold: 0.88,
                  controller: _controller,
                  multipe: true,
                  onMultipeSearchFace: (results) {
                    setState(() {
                      this.results = results;
                    });
                    if (this.results != null) {
                      _showSaveFaceDialog();
                    }
                  },
                  scanFaceTips: (code, status) {
                    print('code: $code, status: $status');
                    setState(() {
                      this.status = status;
                    });
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
