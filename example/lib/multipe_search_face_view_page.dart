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
  bool _isShowDialog = false;
  Function? _updateDialog;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _showSaveFaceDialog() {
    if (results == null || results!.isEmpty) return;
    
    // 如果对话框已经显示，不重复弹出
    if (_isShowDialog) return;
    
    _isShowDialog = true;
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            // 保存对话框的更新函数
            _updateDialog = setDialogState;
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
                                      result.faceId,
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
                    Navigator.pop(context);
                    _isShowDialog = false;
                    _updateDialog = null;
                  },
                  child: const Text('关闭'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  // 利用Map实现合并：将resultsTmp中未包含的元素添加到results
  List<MultipeSearchResult> mergeResultsWithMap(
    List<MultipeSearchResult>? results,
    List<MultipeSearchResult> resultsTmp,
  ) {
    // 1. 处理results为空的情况（初始化为空列表）
    final targetList = results ?? [];

    // 2. 构建Map：key=faceId，value=MultipeSearchResult（利用键唯一性去重）
    final resultMap = <String, MultipeSearchResult>{};

    // 2.1 先将原results的元素存入Map
    for (final item in targetList) {
      resultMap[item.faceId] = item; // 若有重复faceId，后存入的会覆盖前一个（按需调整）
    }

    // 2.2 遍历resultsTmp，仅添加Map中不存在的元素
    for (final tmpItem in resultsTmp) {
      if (!resultMap.containsKey(tmpItem.faceId)) {
        resultMap[tmpItem.faceId] = tmpItem; // 新元素存入Map
      }
    }

    // 3. 将Map的值转回列表（覆盖原results，保持顺序与存入顺序一致）
    // 注意：Dart 2.17+中Map的values顺序与插入顺序一致
    targetList.clear();
    targetList.addAll(resultMap.values);
    return targetList;
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
                    results = mergeResultsWithMap(this.results, results);
                    setState(() {
                      this.results = results;
                    });
                    if (this.results != null) {
                      _showSaveFaceDialog();
                      // 如果对话框已经打开，更新对话框内容
                      if (_updateDialog != null) {
                        _updateDialog!(() {});
                      }
                    }
                  },
                  scanFaceTips: (code, status) {
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
