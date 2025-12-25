package com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara;

import android.content.Context;

import com.serenegiant.widget.AspectRatioSurfaceView;

/**
 * 构建管理USB摄像头（UVC协议）的参数集合Builder
 */
public class CameraBuilder {
    private final String cameraName;
    private final String cameraKey;
    private final Context context;
    private final AspectRatioSurfaceView cameraView;
    private final int degree;
    private final boolean isHorizontalMirror;

    private CameraBuilder(String cameraName, String cameraKey, Context context, AspectRatioSurfaceView cameraView, int degree, boolean isHorizontalMirror) {
        this.cameraName = cameraName;
        this.cameraKey = cameraKey;
        this.context = context;
        this.cameraView = cameraView;
        this.degree = degree;
        this.isHorizontalMirror = isHorizontalMirror;
    }

    public String getCameraName() {
        return cameraName;
    }

    public String getCameraKey() {
        return cameraKey;
    }

    public Context getContext() {
        return context;
    }

    public AspectRatioSurfaceView getCameraView() {
        return cameraView;
    }

    public int getDegree() {
        return degree;
    }

    public boolean isHorizontalMirror() {
        return isHorizontalMirror;
    }

    public static class Builder {
        private String cameraName = "";
        private String cameraKey = "";
        private Context context;
        private AspectRatioSurfaceView cameraView;
        private int degree = 0;
        private boolean horizontalMirror = false;

        public Builder setCameraName(String cameraName) {
            this.cameraName = cameraName;
            return this;
        }

        // RGB，IR 红外摄像头设备device.getProductName() 一般会有RGB/IR 字样关键字，也有不规范命名
        public Builder setCameraKey(String cameraKey) {
            this.cameraKey = cameraKey;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setCameraView(AspectRatioSurfaceView cameraView) {
            this.cameraView = cameraView;
            return this;
        }

        public Builder setDegree(int degree) {
            this.degree = degree;
            return this;
        }

        public Builder setHorizontalMirror(boolean horizontalMirror) {
            this.horizontalMirror = horizontalMirror;
            return this;
        }

        public CameraBuilder build() {
            if (context == null) throw new IllegalArgumentException("Context must not be null");
            if (cameraView == null)
                throw new IllegalArgumentException("CameraView must not be null");
            return new CameraBuilder(
                    cameraName,
                    cameraKey,
                    context,
                    cameraView,
                    degree,
                    horizontalMirror
            );
        }
    }
}
