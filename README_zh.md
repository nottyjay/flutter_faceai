# flutter_faceai

flutter_faceai 是一个 Flutter 插件，用于在 Flutter 应用中集成 FaceAI 人脸检测和识别功能。安卓端采用[FaceAISDK_Android](https://github.com/FaceAISDK/FaceAISDK_Android)提供支持。目前仅支持安卓设备使用

## 上手教程

### 安装
在```main.dart```中引入插件
```dart
const flutterFaceaiPlugin = FlutterFaceai();
flutterFaceaiPlugin.init("人脸照片存放路径");
```
现在，插件就已经安装完成！可以开始使用了。
### 类说明
#### FlutterFaceai
| 方法                    | 参数                 | 说明                                 |
| ----------------------- | -------------------- | ------------------------------------ |
| init                    | cacheDir             | 初始化插件，需要传入人脸照片存放路径 |
| delete                  | facePath             | 删除指定人脸照片                     |
| clear                   |                      | 清除所有人脸照片                     |
| saveFaceImageByFilePath | imageFilePath,faceId | 保存人脸图片到指定路径               |
| saveFaceImageBytes      | imageBytes,faceId    | 保存人脸图片到指定路径               |

#### FlutterFaceaiConfig
| 方法        | 参数 | 说明                 |
| ----------- | ---- | -------------------- |
| getCacheDir |      | 获取人脸照片存放路径 |


#### FlutterUVCCameraWidget
构造参数

| 参数             | 说明                          |
| ---------------- | ----------------------------- |
| name             | 相机名称                      |
| camerakey        | 相机名称关键字                |
| horizontalMirror | 是否水平镜像                  |
| degree           | 旋转角度，支持0, 90, 180, 270 |

#### FlutterUVCCameraAddFaceWidget
构造参数

| 参数             | 说明                                    |
| ---------------- | --------------------------------------- |
| name             | 相机名称                                |
| camerakey        | 相机名称关键字                          |
| horizontalMirror | 是否水平镜像                            |
| degree           | 旋转角度，支持0, 90, 180, 270           |
| onAddFace        | 扫描到人脸时调用                        |
| scanFaceTips     | 人脸扫描提示                            |
| controller       | FlutterUVCCameraAddFaceController控制器 |
#### FlutterUVCCameraAddFaceController
| 方法     | 参数   | 说明                                       |
| -------- | ------ | ------------------------------------------ |
| saveFace | faceId | 保存刚刚扫描到的人脸，faceId为人脸唯一标识 |

#### FlutterUVCCameraSearchFaceWidget
构造参数

| 参数             | 说明                                        |
| ---------------- | ------------------------------------------- |
| masterName       | 主相机名称                                  |
| slaveName        | 从相机名称（一般为IR摄像头）                |
| masterCamerakey  | 主相机名称关键字                            |
| slaveCamerakey   | 从相机名称关键字                            |
| horizontalMirror | 是否水平镜像                                |
| degree           | 旋转角度，支持0, 90, 180, 270               |
| threshold        | 相似度阈值，默认0.88。取值范围[0.85 , 0.95] |
| scanFaceTips     | 人脸搜索提示                                |
| controller       | FlutterUVCCameraSearchFaceController控制器  |
| onSearchFace     | 搜索到人脸时调用                            |

#### FlutterUVCCameraSearchFaceController
| 方法     | 参数 | 说明     |
| -------- | ---- | -------- |
| reSearch |      | 重新搜索 |


