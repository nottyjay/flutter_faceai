import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_faceai/flutter_faceai.dart';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class SearchFaceViewPage extends StatefulWidget {
  const SearchFaceViewPage({super.key});

  @override
  State<SearchFaceViewPage> createState() => _SearchFaceViewPageState();
}

class _SearchFaceViewPageState extends State<SearchFaceViewPage> {
  String? faceID;
  String? status;
  FlutterUVCCameraSearchFaceController _controller =
      FlutterUVCCameraSearchFaceController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _showSaveFaceDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          contentPadding: const EdgeInsets.all(16),
          content: SizedBox(
            width: 300,
            height: 400,
            child: Container(
              width: double.infinity,
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey),
                borderRadius: BorderRadius.circular(8),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(8),
                child: Image.file(
                  File(FlutterFaceaiConfig.instance.cacheDir! + faceID!),
                  fit: BoxFit.cover,
                ),
              ),
            ),
          ),
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
                  onSearchFace: (faceID) {
                    setState(() {
                      this.faceID = faceID;
                    });
                    if (this.faceID != null) {
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
