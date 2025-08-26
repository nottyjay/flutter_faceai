import 'package:flutter/material.dart';
import 'package:flutter_faceai/flutter_faceai.dart';

class CameraViewPage extends StatelessWidget {
  const CameraViewPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 300,
              height: 300,
              child: FlutterUVCCameraWidget(
                name: '普通RGB摄像头',
                camerakey: 'RGB',
                horizontalMirror: false,
                degree: 0,
                type: 'RGB',
              ),
            ),
            Container(
              width: 300,
              height: 300,
              child: FlutterUVCCameraWidget(
                name: '普通IR摄像头',
                camerakey: 'IR',
                horizontalMirror: false,
                degree: 0,
                type: 'IR',
              ),
            ),
          ],
        ),
      ),
    );
  }
}
