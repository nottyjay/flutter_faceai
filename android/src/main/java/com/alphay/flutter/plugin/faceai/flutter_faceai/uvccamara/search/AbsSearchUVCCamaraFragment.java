package com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.search;

import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.ai.face.faceVerify.verify.FaceVerifyUtils;
import com.alphay.flutter.plugin.faceai.flutter_faceai.R;
import com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.CameraBuilder;
import com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.UVCCameraManager;
import com.serenegiant.widget.AspectRatioSurfaceView;

import android.util.Log;

import java.util.Map;

public abstract class AbsSearchUVCCamaraFragment extends DialogFragment {

    private static final String TAG = "AbsFlutterSearchUVCCameraFragment";

    private View nativeView;
    private AspectRatioSurfaceView rgbCameraView;
    private UVCCameraManager rgbCameraManager;
    private UVCCameraManager irCameraManager;
    private Map<String, Object> param;

    abstract void initFaceSearchParam();

    abstract void faceSearchSetBitmap(Bitmap bitmap, FaceVerifyUtils.BitmapType type);

    public View getRgbCameraView() {
        return rgbCameraView;
    }

    public AbsSearchUVCCamaraFragment(Map<String, Object> param) {
        this.param = param;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        nativeView = inflater.inflate(R.layout.view_camera, container, false);
        return nativeView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initRGBCamara();
    }

    public void initViews() {
    }

    public Map<String, Object> getParam() {
        return param;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rgbCameraManager != null) {
            rgbCameraManager.releaseCameraHelper();
        }
        if (irCameraManager != null) {
            irCameraManager.releaseCameraHelper();
        }
    }

    public void initRGBCamara() {
        rgbCameraView = nativeView.findViewById(R.id.rgb_camera_view);
        String name = (String) param.get("rgb-name");
        String key = (String) param.get("rgb-key");
        Boolean horizontalMirror = (Boolean) param.get("horizontalMirror");
        Integer degree = (Integer) param.get("degree");

        CameraBuilder cameraBuilder = new CameraBuilder.Builder()
                .setCameraName(name)
                .setCameraKey(key)
                .setHorizontalMirror(horizontalMirror != null ? horizontalMirror : false)
                .setDegree(degree != null ? degree : 0)
                .setCameraView(rgbCameraView)
                .setContext(requireContext())
                .build();
        rgbCameraManager = new UVCCameraManager(cameraBuilder);

        rgbCameraManager.setOnCameraStatuesCallBack(new UVCCameraManager.OnCameraStatusCallBack() {
            @Override
            public void onAttach(UsbDevice device) {

            }

            @Override
            public void onDeviceOpen(UsbDevice device, boolean isFirstOpen) {
                initFaceSearchParam();

                //RGB 打开了就继续去打开IR
                initIRCamara();
            }
        });

        rgbCameraManager.setFaceAIAnalysis(new UVCCameraManager.OnFaceAIAnalysisCallBack() {
            @Override
            public void onBitmapFrame(Bitmap bitmap) {
                faceSearchSetBitmap(bitmap, FaceVerifyUtils.BitmapType.RGB);
            }
        });
    }

    private void initIRCamara() {
        AspectRatioSurfaceView view = nativeView.findViewById(R.id.ir_camera_view);
        String name = (String) param.get("ir-name");
        String key = (String) param.get("ir-key");
        Boolean horizontalMirror = (Boolean) param.get("horizontalMirror");

        CameraBuilder cameraBuilder = new CameraBuilder.Builder()
                .setCameraName(name)
                .setCameraKey(key)
                .setHorizontalMirror(horizontalMirror != null ? horizontalMirror : false)
                .setDegree(0)
                .setCameraView(view)
                .setContext(requireContext())
                .build();
        irCameraManager = new UVCCameraManager(cameraBuilder);

        irCameraManager.setOnCameraStatuesCallBack(new UVCCameraManager.OnCameraStatusCallBack() {
            @Override
            public void onAttach(UsbDevice device) {

            }

            @Override
            public void onDeviceOpen(UsbDevice device, boolean isFirstOpen) {

            }
        });

        irCameraManager.setFaceAIAnalysis(new UVCCameraManager.OnFaceAIAnalysisCallBack() {
            @Override
            public void onBitmapFrame(Bitmap bitmap) {
                faceSearchSetBitmap(bitmap, FaceVerifyUtils.BitmapType.IR);
            }
        });
        view.setVisibility(View.GONE);
    }
}
