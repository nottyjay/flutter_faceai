package com.alphay.flutter.plugin.faceai.flutter_faceai.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtils {

    /**
     * Bitmap转字节数组工具方法
     *
     * @param bitmap  目标Bitmap
     * @param format  压缩格式（JPEG/PNG）
     * @param quality 压缩质量（0-100，JPEG有效）
     * @return 字节数组，失败返回null
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 压缩Bitmap到输出流
            bitmap.compress(format, quality, outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扩展：Bitmap转Base64字符串（适合跨端传输，如网络转发）
     */
    public static String bitmapToBase64(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        byte[] bytes = bitmapToByteArray(bitmap, format, quality);
        if (bytes == null) {
            return null;
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP); // 无换行符，避免解析问题
    }
}
