# flutter_faceai

flutter_faceai is a Flutter plugin for integrating FaceAI face detection and recognition functionality into Flutter applications. The Android side is supported by [FaceAISDK_Android](https://github.com/FaceAISDK/FaceAISDK_Android). Currently only supports Android devices.

## Getting Started

### Installation
Import the plugin in ```main.dart```
```dart
const flutterFaceaiPlugin = FlutterFaceai();
flutterFaceaiPlugin.init("Face photo storage path");
```
Now the plugin is installed! You can start using it.

### Class Documentation
#### FlutterFaceai
| Method                  | Parameters           | Description                                         |
| ----------------------- | -------------------- | --------------------------------------------------- |
| init                    | cacheDir             | Initialize plugin, requires face photo storage path |
| delete                  | facePath             | Delete specified face photo                         |
| clear                   |                      | Clear all face photos                               |
| saveFaceImageByFilePath | imageFilePath,faceId | Save face image to specified path                   |
| saveFaceImageBytes      | imageBytes,faceId    | Save face image to specified path                   |

#### FlutterFaceaiConfig
| Method      | Parameters | Description                 |
| ----------- | ---------- | --------------------------- |
| getCacheDir |            | Get face photo storage path |


#### FlutterUVCCameraWidget
Constructor Parameters

| Parameter        | Description                              |
| ---------------- | ---------------------------------------- |
| name             | Camera name                              |
| camerakey        | Camera name keyword                      |
| horizontalMirror | Whether to mirror horizontally           |
| degree           | Rotation angle, supports 0, 90, 180, 270 |

#### FlutterUVCCameraAddFaceWidget
Constructor Parameters

| Parameter        | Description                                  |
| ---------------- | -------------------------------------------- |
| name             | Camera name                                  |
| camerakey        | Camera name keyword                          |
| horizontalMirror | Whether to mirror horizontally               |
| degree           | Rotation angle, supports 0, 90, 180, 270     |
| onAddFace        | Called when face is detected                 |
| scanFaceTips     | Face scanning tips                           |
| controller       | FlutterUVCCameraAddFaceController controller |

#### FlutterUVCCameraAddFaceController
| Method   | Parameters | Description                                             |
| -------- | ---------- | ------------------------------------------------------- |
| saveFace | faceId     | Save the just scanned face, faceId is unique identifier |

#### FlutterUVCCameraSearchFaceWidget
Constructor Parameters

| Parameter        | Description                                            |
| ---------------- | ------------------------------------------------------ |
| masterName       | Master camera name                                     |
| slaveName        | Slave camera name (usually IR camera)                  |
| masterCamerakey  | Master camera name keyword                             |
| slaveCamerakey   | Slave camera name keyword                              |
| horizontalMirror | Whether to mirror horizontally                         |
| degree           | Rotation angle, supports 0, 90, 180, 270               |
| threshold        | Similarity threshold, default 0.88. Range [0.85, 0.95] |
| scanFaceTips     | Face search tips                                       |
| controller       | FlutterUVCCameraSearchFaceController controller        |
| onSearchFace     | Called when face is found                              |

#### FlutterUVCCameraSearchFaceController
| Method   | Parameters | Description |
| -------- | ---------- | ----------- |
| reSearch |            | Re-search   |

