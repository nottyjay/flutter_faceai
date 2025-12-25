package com.alphay.flutter.plugin.faceai.flutter_faceai.config;

public class FaceImageConfig {
    private String cacheSearchFaceDir;

    private static volatile FaceImageConfig instance;

    private FaceImageConfig() {}

    public static FaceImageConfig getInstance() {
        if (instance == null) {
            synchronized (FaceImageConfig.class) {
                if (instance == null) {
                    instance = new FaceImageConfig();
                }
            }
        }
        return instance;
    }

    public String getCacheSearchFaceDir() {
        return cacheSearchFaceDir;
    }

    public void setCacheSearchFaceDir(String cacheSearchFaceDir) {
        this.cacheSearchFaceDir = cacheSearchFaceDir;
    }
}
