package com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.search;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ai.face.base.utils.BrightnessUtil;
import com.ai.face.faceSearch.search.FaceSearchEngine;
import com.ai.face.faceSearch.search.SearchProcessBuilder;
import com.ai.face.faceSearch.search.SearchProcessCallBack;
import com.ai.face.faceSearch.utils.FaceSearchResult;
import com.ai.face.faceVerify.verify.FaceVerifyUtils;
import com.alphay.flutter.plugin.faceai.flutter_faceai.config.FaceImageConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public class SearchUVCCamaraFragment extends AbsSearchUVCCamaraFragment {

    private static final String TAG = "SearchUVCCamaraFragment";

    private FragmentActivity activity;

    private Bitmap irBitmap;
    private Bitmap rgbBitmap;
    private boolean irReady = false;
    private boolean rgbReady = false;
    private MethodChannel channel;

    public SearchUVCCamaraFragment(Map<String, Object> param, FragmentActivity activity, MethodChannel channel) {
        super(param);
        this.activity = activity;
        this.channel = channel;
        Log.d(TAG, "创建一个 View，FlutterSearchUVCCamaraFragment");
    }

    @Override
    public void initViews() {
        super.initViews();
        BrightnessUtil.setBrightness(requireActivity(), 1.0f);  //高亮白色背景屏幕光可以当补光灯
    }

    @Override
    void initFaceSearchParam() {
        Map<String, Object> param = getParam();
        Double thresholdDouble = null;
        if (param.get("threshold") instanceof Double) {
            thresholdDouble = (Double) param.get("threshold");
        }
        float threshold = thresholdDouble != null ? thresholdDouble.floatValue() : 0.88f;

        SearchProcessBuilder faceProcessBuilder = new SearchProcessBuilder.Builder(activity)
                .setLifecycleOwner(this)
                .setThreshold(threshold)
                .setCallBackAllMatch(true)
                .setFaceLibFolder(FaceImageConfig.getInstance().getCacheSearchFaceDir())
                .setCameraType(SearchProcessBuilder.CameraType.UVC_CAMERA)
                .setProcessCallBack(new SearchProcessCallBack() {
                    @Override
                    public void onMostSimilar(String faceId, float score, Bitmap bitmap) {
                        FaceSearchEngine.Companion.getInstance().stopSearchProcess();
                        Map<String, Object> result = new HashMap<>();
                        result.put("faceId", faceId);
                        if (channel != null) { // Added: Null check for channel
                            channel.invokeMethod("createSearchProcess_onMostSimilar", result);
                        }
                    }

                    @Override
                    public void onFaceMatched(List<FaceSearchResult> p0, Bitmap p1) {
                        super.onFaceMatched(p0, p1);
                        Log.d(TAG, "onFaceMatched: " + p0);
                    }

                    @Override
                    public void onFaceDetected(List<FaceSearchResult> result) {
                        if (result != null && !result.isEmpty()) {
                            List<Map<String, Object>> newList = new ArrayList<>();
                            for (FaceSearchResult item : result) {
                                if (item != null && item.getFaceScore() > 0) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("faceId", item.getFaceName());
                                    map.put("score", item.getFaceScore());
                                    newList.add(map);
                                }
                            }
                            FaceSearchEngine.Companion.getInstance().stopSearchProcess();
                            if (!newList.isEmpty()) {
                                Log.d(TAG, "onFaceDetected: " + newList);
                                if (channel != null) { // Added: Null check for channel
                                    channel.invokeMethod("createSearchProcess_onFaceDetected", newList);
                                }
                            }
                        }
                    }

                    @Override
                    public void onProcessTips(int code) {
                        if (activity != null) { // Added: Null check for activityBinding and its activity
                            activity.runOnUiThread(() -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("code", code);
                                if (channel != null) { // Added: Null check for channel
                                    channel.invokeMethod("createSearchProcess_onProcessTips", map);
                                }
                            });
                        }
                    }
                }).create();
        FaceSearchEngine.Companion.getInstance().initSearchParams(faceProcessBuilder);
    }

    @Override
    void faceSearchSetBitmap(Bitmap bitmap, FaceVerifyUtils.BitmapType type) {
        if (type.equals(FaceVerifyUtils.BitmapType.IR)) {
            irBitmap = bitmap;
            irReady = true;
        } else if (type.equals(FaceVerifyUtils.BitmapType.RGB)) {
            rgbBitmap = bitmap;
            rgbReady = true;
        }

        if (irReady && rgbReady) {
            //送数据进入SDK
            FaceSearchEngine.Companion.getInstance().runSearchWithIR(irBitmap, rgbBitmap);
            irReady = false;
            rgbReady = false;
        }
    }


//    @Override
//    public void dispose() {
//        isDisposed = true; // Added: Set disposed flag at the beginning
//        Log.d(TAG, "回收一个 View，FlutterUVCCameraView: " + this.viewId);
//        if (channel != null) { // Added: Null check for channel before detaching
//            channel.setMethodCallHandler(null); // Detach handler to prevent further calls
//            channel = null; // Nullify channel after detaching handler
//        }
//        activityBinding = null; // Nullify activityBinding
//        if (rgbBitmap != null) {
//            rgbBitmap.recycle();
//            rgbBitmap = null;
//        }
//        if (irBitmap != null) {
//            irBitmap.recycle();
//            irBitmap = null;
//        }
//        Log.d(TAG, "当前视图 nativeView：" + (getNativeView() != null ? "未释放" : "已释放"));
//        FaceSearchEngine.Companion.getInstance().stopSearchProcess();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FaceSearchEngine.Companion.getInstance().stopSearchProcess();
    }
}
