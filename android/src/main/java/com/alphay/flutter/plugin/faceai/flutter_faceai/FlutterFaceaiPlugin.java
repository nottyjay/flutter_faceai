package com.alphay.flutter.plugin.faceai.flutter_faceai;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ai.face.faceSearch.search.FaceSearchImagesManger;
import com.alphay.flutter.plugin.faceai.flutter_faceai.config.FaceImageConfig;
import com.alphay.flutter.plugin.faceai.flutter_faceai.factory.SearchUVCCameraFactory;
import com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.search.SearchUVCCamaraFragment;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterFaceaiPlugin
 */
public class FlutterFaceaiPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static final String TAG = FlutterFaceaiPlugin.class.getSimpleName();

    // 保存Flutter引擎绑定实例
    private FlutterPluginBinding flutterPluginBinding;
    private ActivityPluginBinding activityBinding;

    // 标记平台视图是否已注册
    private boolean isViewFactoryRegistered = false;

    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.alphay.flutter.plugin/flutter_uvc_faceai");
        channel.setMethodCallHandler(this);
        this.flutterPluginBinding = flutterPluginBinding;
    }

    // 注册平台视图工厂的方法
    private void registerViewFactoryIfNeeded() {
        if (!isViewFactoryRegistered && flutterPluginBinding != null && activityBinding != null) {
//            try {
//                // 注册原生视图工厂
//                flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(
//                        "com.alphay.flutter.faceai/flutter_search_uvc_camera_view",
//                        new SearchUVCCameraFactory(
//                                flutterPluginBinding.getBinaryMessenger(),
//                                activityBinding
//                        )
//                );
//                Log.i(TAG, "UI注册成功");
//                isViewFactoryRegistered = true;
//            } catch (Exception e) {
//                // 如果注册失败，记录错误但不崩溃
//                Log.e("FlutterFaceaiPlugin", "Failed to register view factory", e);
//            }
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if ("init".equals(call.method)) {
            // 解析参数（cacheDir 是 Flutter 传递的路径）
            String cacheDir = call.argument("cacheDir");
            if (cacheDir == null || cacheDir.isEmpty()) {
                // 参数错误时返回失败
                result.error("INVALID_PARAM", "cacheDir 不能为空", null);
                return;
            }
            FaceImageConfig.getInstance().setCacheSearchFaceDir(cacheDir);
            result.success("Android " + android.os.Build.VERSION.RELEASE);
            registerViewFactoryIfNeeded();
        } else if ("delete".equals(call.method)) {
            try {
                String facePathName = call.argument("path");
                FaceSearchImagesManger.Companion.getInstance(activityBinding.getActivity().getApplication())
                        .deleteFaceImage(facePathName);
                result.success(true);
            } catch (RuntimeException e) {
                result.error("RuntimeException", e.getMessage(), null);
            }
        } else if ("clear".equals(call.method)) {
            try {
                FaceSearchImagesManger.Companion.getInstance(activityBinding.getActivity().getApplication())
                        .clearFaceImages(
                                FaceImageConfig.getInstance().getCacheSearchFaceDir()
                        );
                result.success(true);
            } catch (RuntimeException e) {
                result.error("RuntimeException", e.getMessage(), null);
            }
        } else if ("addFaceImage".equals(call.method)) {
            try {
                byte[] imageData = call.argument("imageData");
                String faceID = call.argument("faceId");
                if (imageData == null) {
                    result.error("NULL_DATA", "图片数据为空", null);
                    return;
                }
                Bitmap faceBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                String faceName = faceID + ".jpg";
                String facePathName = FaceImageConfig.getInstance().getCacheSearchFaceDir() + faceName;
                FaceSearchImagesManger.Companion.getInstance(activityBinding.getActivity().getApplication())
                        .insertOrUpdateFaceImage(
                                faceBitmap,
                                facePathName,
                                new FaceSearchImagesManger.Callback() {
                                    @Override
                                    public void onFailed(String msg) {
                                        result.error("9999", "Save face image failed", null);
                                    }

                                    @Override
                                    public void onSuccess(Bitmap bitmap, float[] faceEmbedding) {
                                        result.success(true);
                                    }
                                });
                // result.success(true);
            } catch (RuntimeException e) {
                result.error("RuntimeException", e.getMessage(), null);
            }
        } else if ("startSearch".equals(call.method)) {
            startSearch((Map<String, Object>) call.arguments);
        } else {
            result.notImplemented();
        }
    }

    /**
     * 显示内嵌Fragment的Dialog
     */
    private void startSearch(Map<String, Object> params) {
        FragmentActivity activity = (FragmentActivity) activityBinding.getActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        SearchUVCCamaraFragment fragment = new SearchUVCCamaraFragment(params, activity, channel);
        fragment.show(fragmentManager, "search_uvc");
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activityBinding = binding;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.activityBinding = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activityBinding = binding;

    }

    @Override
    public void onDetachedFromActivity() {
        this.activityBinding = null;
    }
}