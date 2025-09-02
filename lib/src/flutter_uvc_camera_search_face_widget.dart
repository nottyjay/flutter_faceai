import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'flutter_uvc_camera_search_face_controller.dart';

class FlutterUVCCameraSearchFaceWidget extends StatelessWidget {
  const FlutterUVCCameraSearchFaceWidget({
    super.key,
    required this.masterName,
    required this.masterCamerakey,
    required this.slaveName,
    required this.slaveCamerakey,
    required this.horizontalMirror,
    required this.degree,
    required this.scanFaceTips,
    required this.threshold,

    this.controller,
    this.onSearchFace,
    this.multipe = false,
    this.onMultipeSearchFace,
  });

  final String masterName;
  final String masterCamerakey;
  final String slaveName;
  final String slaveCamerakey;
  final bool horizontalMirror;
  final int degree;
  final double threshold;
  final bool multipe;

  final Function(String)? onSearchFace;
  final Function(List<MultipeSearchResult>)? onMultipeSearchFace;
  final Function(int, String) scanFaceTips;
  final FlutterUVCCameraSearchFaceController? controller;

  static const int face_size_fit = 10;
  static const int face_too_large = 9;
  static const int ir_live_error = 8;
  static const int too_much_face = 7;
  static const int face_too_small = 6;
  static const int threshold_error = 5;
  static const int mask_detection = 4;
  static const int no_live_face = 3;
  static const int searching = 2;
  static const int search_prepared = 1;
  static const int emgine_initing = 0;
  static const int face_dir_empty = -1;
  static const int no_matched = -2;
  static const int un_support_camera = -3;

  Future<void> _addFaceInitOnCompleted(
    MethodCall call,
    MethodChannel channel,
  ) async {
    if (call.method == 'createSearchProcess_onMostSimilar') {
      final result = (call.arguments as Map).cast<String, dynamic>();
      if (result.containsKey("faceId")) {
        String faceID = result["faceId"] as String;
        onSearchFace!(faceID);
      }
    } else if (call.method == 'createSearchProcess_onFaceDetected') {
      final result = call.arguments as List;
      if (result.isNotEmpty && multipe) {
        onMultipeSearchFace!(
          result
              .map(
                (e) => MultipeSearchResult(
                  e['faceId'] as String,
                  e['score'] as double,
                ),
              )
              .toList(),
        );
        // channel.invokeMethod("stopSearchProcess");
      }
    } else if (call.method == 'createSearchProcess_onProcessTips') {
      String status = '';
      final code =
          (call.arguments as Map).cast<String, dynamic>()['code'] as int;
      switch (code) {
        //整理返回提示，2025.0815
        case no_matched:
          status = '人脸搜索中';
          break;
        case face_dir_empty:
          status = '人脸库为空';
          break;
        case emgine_initing:
          status = "SDK Initing";
          break;
        case search_prepared:
        case searching:
          status = "平视正对摄像头";
          break;
        case no_live_face:
          status = "未检测到人脸"; //2秒后确认图像
          break;
        case face_too_small:
          status = "请靠近一点";
          break;
        case face_too_large:
          status = "请远离屏幕";
          break;
        case face_size_fit:
          status = "";
          break;
        case too_much_face:
          status = "多张人脸出现";
          break;
        case threshold_error:
          status = "阈值Threshold范围为[0.85,0.95]";
          break;
        case mask_detection:
          status = "请不要佩戴口罩";
          break;
      }
      scanFaceTips(code, status);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      alignment: Alignment.center,
      children: [
        AndroidView(
          viewType: 'flutter_uvc_camera_view',
          onPlatformViewCreated: (viewId) {
            final channel = MethodChannel('flutter_uvc_camera_view_$viewId');
            _initSlaveCamera(channel);
          },
        ),
        AndroidView(
          viewType: 'flutter_uvc_camera_view',
          onPlatformViewCreated: (viewId) {
            final channel = MethodChannel('flutter_uvc_camera_view_$viewId');
            channel.setMethodCallHandler(
              (call) => _addFaceInitOnCompleted(call, channel),
            );
            // 如果提供了控制器，将MethodChannel设置到控制器中
            controller?.setChannel(channel);
            _initMasterCamera(channel);
          },
        ),
      ],
    );
  }

  // 提取初始化方法，便于复用和异常处理
  Future<void> _initMasterCamera(MethodChannel channel) async {
    try {
      await channel.invokeMethod('init', {
        "name": masterName,
        "key": masterCamerakey,
        "horizontalMirror": horizontalMirror,
        "degree": degree,
        "threshold": threshold,
        "multipe": multipe,
        "type": "master",
      });
    } on PlatformException catch (e) {
      debugPrint('相机初始化失败: ${e.message}');
    }
  }

  // 提取初始化方法，便于复用和异常处理
  Future<void> _initSlaveCamera(MethodChannel channel) async {
    try {
      await channel.invokeMethod('init', {
        "name": slaveName,
        "key": slaveCamerakey,
        "horizontalMirror": horizontalMirror,
        "degree": degree,
        "type": "slave",
      });
    } on PlatformException catch (e) {
      debugPrint('相机初始化失败: ${e.message}');
    }
  }
}

class MultipeSearchResult {
  final String faceId;
  final double score;
  MultipeSearchResult(this.faceId, this.score);
}
