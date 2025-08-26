package com.alphay.flutter.plugin.faceai.flutter_faceai.config

class FaceImageConfig private constructor() {

    var cacheSearchFaceDir: String? = null

    companion object {
        @Volatile
        private var instance: FaceImageConfig? = null

        fun getInstance(): FaceImageConfig {
            return instance ?: synchronized(this) {
                instance ?: FaceImageConfig().also { instance = it }
            }
        }
    }
}
