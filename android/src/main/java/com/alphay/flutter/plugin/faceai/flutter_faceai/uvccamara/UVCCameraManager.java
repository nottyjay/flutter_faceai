package com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ai.face.base.utils.DataConvertUtils;
import com.herohan.uvcapp.CameraHelper;
import com.herohan.uvcapp.ICameraHelper;
import com.serenegiant.opengl.renderer.MirrorMode;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.UVCParam;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;

public class UVCCameraManager {

    private static final String TAG = "FlutterUVCCameraManager";

    // 配置UVC 协议摄像头默认的分辨率
    public static final int UVC_CAMERA_WIDTH = 640;
    public static final int UVC_CAMERA_HEIGHT = 480;

    private CameraBuilder cameraBuilder;
    private Context context;

    private ICameraHelper mCameraHelper;
    private boolean autoAspectRatio = true;
    private int previewHeight = UVC_CAMERA_HEIGHT;
    private OnFaceAIAnalysisCallBack faceAIAnalysisCallBack;
    private OnCameraStatusCallBack onCameraStatuesCallBack;
    private OnPreviewSizeCallBack onPreviewSizeCallBack;

    private int width = UVC_CAMERA_WIDTH;
    private int height = UVC_CAMERA_HEIGHT;
    private Bitmap reuseBitmap;

    public interface OnCameraStatusCallBack {
        void onAttach(UsbDevice device);

        void onDeviceOpen(UsbDevice device, boolean isFirstOpen);
    }

    public interface OnFaceAIAnalysisCallBack {
        void onBitmapFrame(Bitmap bitmap);
    }

    public interface OnPreviewSizeCallBack {
        void onPreviewSize(int width, int height);
    }

    public UVCCameraManager(CameraBuilder cameraBuilder) {
        this.cameraBuilder = cameraBuilder;
        this.context = cameraBuilder.getContext().getApplicationContext();

        initCameraHelper();
        initUVCCamera();
    }

    private void initCameraHelper() {
        if (mCameraHelper == null) {
            mCameraHelper = new CameraHelper();
            mCameraHelper.setStateCallback(mStateListener);
        }
    }

    public void setOnCameraStatuesCallBack(OnCameraStatusCallBack callBack) {
        this.onCameraStatuesCallBack = callBack;
    }

    public void setOnPreviewSizeCallBack(OnPreviewSizeCallBack callBack) {
        this.onPreviewSizeCallBack = callBack;
    }

    public void setAutoAspectRatio(boolean autoAspectRatio) {
        this.autoAspectRatio = autoAspectRatio;
    }

    public void releaseCameraHelper() {
        if (mCameraHelper != null) {
            mCameraHelper.setStateCallback(null);
            mCameraHelper.release();
            mCameraHelper = null;
        }

        faceAIAnalysisCallBack = null;
        onCameraStatuesCallBack = null; // 添加这行
        onPreviewSizeCallBack = null;
        cameraBuilder = null;
    }

    private void initUVCCamera() {
        if (mCameraHelper == null) return;
        List<UsbDevice> list = mCameraHelper.getDeviceList();
        if (list == null) return;

        boolean isMatched = false;
        for (UsbDevice device : list) {
            String name = device.getProductName();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(context, "摄像头ProductName为空", Toast.LENGTH_LONG).show();
            } else if (name != null && name.toLowerCase(Locale.getDefault()).contains(cameraBuilder.getCameraKey().toLowerCase(Locale.getDefault()))) {
                isMatched = true;
                mCameraHelper.selectDevice(device);
                if (mCameraHelper.getPreviewConfig() != null) {
                    mCameraHelper.getPreviewConfig().setRotation(cameraBuilder.getDegree());
                    if (cameraBuilder.isHorizontalMirror()) {
                        mCameraHelper.getPreviewConfig().setMirror(MirrorMode.MIRROR_HORIZONTAL);
                    }
                }
                break;
            }
        }
        if (!isMatched) {
            Toast.makeText(context, cameraBuilder.getCameraName() + "匹配失败,请手动匹配", Toast.LENGTH_LONG).show();
        }
    }

    public void setFaceAIAnalysis(OnFaceAIAnalysisCallBack callBack) {
        this.faceAIAnalysisCallBack = callBack;
    }

    private final ICameraHelper.StateCallback mStateListener = new ICameraHelper.StateCallback() {

        @Override
        public void onAttach(UsbDevice device) {
            if (onCameraStatuesCallBack != null) {
                onCameraStatuesCallBack.onAttach(device);
            }
        }

        @Override
        public void onDeviceOpen(UsbDevice device, boolean isFirstOpen) {
            UVCParam param = new UVCParam();
            param.setQuirks(UVCCamera.UVC_QUIRK_FIX_BANDWIDTH);
            if (mCameraHelper != null) {
                mCameraHelper.openCamera(param);
            }
            if (onCameraStatuesCallBack != null) {
                onCameraStatuesCallBack.onDeviceOpen(device, isFirstOpen);
            }
        }

        @Override
        public void onCameraOpen(UsbDevice device) {
            Size previewSize = null;
            if (previewHeight > 0 && mCameraHelper != null) {
                List<Size> supportedSizeList = mCameraHelper.getSupportedSizeList();
                if (supportedSizeList != null) {
                    for (Size size : supportedSizeList) {
                        if (size.height == previewHeight && size.type == 7) {
                            previewSize = size;
                            break;
                        }
                    }
                    if (previewSize != null) {
                        mCameraHelper.setPreviewSize(previewSize);
                        width = previewSize.width;
                        height = previewSize.height;
                        if (autoAspectRatio && cameraBuilder.getCameraView() != null) {
                            cameraBuilder.getCameraView().setAspectRatio(width, height);
                        }
                        if (onPreviewSizeCallBack != null) {
                            int rotateDegree = cameraBuilder.getDegree() % 360;
                            if (rotateDegree < 0) rotateDegree += 360;
                            boolean swap = rotateDegree == 90 || rotateDegree == 270;
                            int displayWidth = swap ? height : width;
                            int displayHeight = swap ? width : height;
                            onPreviewSizeCallBack.onPreviewSize(displayWidth, displayHeight);
                        }
                    } else {
                        Toast.makeText(context, "无对应的分辨率，请调试修正", Toast.LENGTH_LONG).show();
                    }
                }
            }

            if (mCameraHelper != null) {
                mCameraHelper.startPreview();
                if (cameraBuilder.getCameraView() != null) {
                    mCameraHelper.addSurface(cameraBuilder.getCameraView().getHolder().getSurface(), true);

                    mCameraHelper.setFrameCallback(new IFrameCallback() {
                        @Override
                        public void onFrame(ByteBuffer byteBuffer) {
                            if (faceAIAnalysisCallBack != null) {
                                reuseBitmap = DataConvertUtils.NV21Data2Bitmap(
                                        byteBuffer,
                                        width,
                                        height,
                                        cameraBuilder.getDegree(),
                                        cameraBuilder.isHorizontalMirror()
                                );
                                faceAIAnalysisCallBack.onBitmapFrame(reuseBitmap);
                            }
                        }
                    }, UVCCamera.PIXEL_FORMAT_NV21);
                }
            }
        }

        @Override
        public void onCameraClose(UsbDevice device) {
            Log.d(TAG, "摄像头onCameraClose中");
            if (cameraBuilder.getCameraView() != null && mCameraHelper != null) {
                mCameraHelper.removeSurface(cameraBuilder.getCameraView().getHolder().getSurface());
            }
        }

        @Override
        public void onDeviceClose(UsbDevice device) {
            Log.d(TAG, "摄像头设备onDeviceClose中");
        }

        @Override
        public void onDetach(UsbDevice device) {
            Log.d(TAG, "摄像头设备onDetach中");

        }

        @Override
        public void onCancel(UsbDevice device) {
            Log.d(TAG, "摄像头设备onCancel中");

        }
    };
}
