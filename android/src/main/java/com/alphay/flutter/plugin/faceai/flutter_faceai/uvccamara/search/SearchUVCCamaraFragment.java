package com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.search;

import static com.ai.face.faceSearch.search.SearchProcessTipsCode.EMGINE_INITING;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.FACE_DIR_EMPTY;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.FACE_SIZE_FIT;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.FACE_TOO_LARGE;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.FACE_TOO_SMALL;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.IR_LIVE_ERROR;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.MASK_DETECTION;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.NO_LIVE_FACE;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.NO_MATCHED;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.SEARCHING;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.SEARCH_PREPARED;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.THRESHOLD_ERROR;
import static com.ai.face.faceSearch.search.SearchProcessTipsCode.TOO_MUCH_FACE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ai.face.base.utils.BrightnessUtil;
import com.ai.face.faceSearch.search.FaceSearchEngine;
import com.ai.face.faceSearch.search.SearchProcessBuilder;
import com.ai.face.faceSearch.search.SearchProcessCallBack;
import com.ai.face.faceSearch.utils.FaceSearchResult;
import com.ai.face.faceVerify.verify.FaceVerifyUtils;
import com.alphay.flutter.plugin.faceai.flutter_faceai.R;
import com.alphay.flutter.plugin.faceai.flutter_faceai.config.FaceImageConfig;
import com.alphay.flutter.plugin.faceai.flutter_faceai.utils.BitmapUtils;

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
    private TextView searchTipsTextView;
    private TextView secondSearchTipsTextView;


    public SearchUVCCamaraFragment(Map<String, Object> param, FragmentActivity activity, MethodChannel channel) {
        super(param);
        this.activity = activity;
        this.channel = channel;
        Log.d(TAG, "创建一个 View，FlutterSearchUVCCamaraFragment");
    }

    @Override
    public void initViews() {
        super.initViews();
        searchTipsTextView = nativeView.findViewById(R.id.search_tips);
        secondSearchTipsTextView = nativeView.findViewById(R.id.second_search_tips);
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
//                .setSearchType(SearchProcessBuilder.SearchType.N_SEARCH_M)
                .setFaceLibFolder(FaceImageConfig.getInstance().getCacheSearchFaceDir())
                .setCameraType(SearchProcessBuilder.CameraType.UVC_CAMERA)
                .setProcessCallBack(new SearchProcessCallBack() {
                    @Override
                    public void onMostSimilar(String faceId, float score, Bitmap bitmap) {
                        FaceSearchEngine.Companion.getInstance().stopSearchProcess();
                        Map<String, Object> result = new HashMap<>();
                        result.put("faceId", faceId);
                        activity.runOnUiThread(() -> {
                            if (channel != null) {
                                channel.invokeMethod("createSearchProcess_onMostSimilar", result);
                            }
                            dismissAllowingStateLoss();
                        });
                    }

                    @Override
                    public void onFaceMatched(List<FaceSearchResult> p0, Bitmap p1) {
                        super.onFaceMatched(p0, p1);
                        if (p0 != null && !p0.isEmpty()) {
                            FaceSearchEngine.Companion.getInstance().stopSearchProcess();
                            List<Map<String, Object>> newList = new ArrayList<>();
                            for (FaceSearchResult item : p0) {
                                if (item != null && item.getFaceScore() > 0) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("faceId", item.getFaceName());
                                    map.put("score", item.getFaceScore());
                                    newList.add(map);
                                }
                            }
                            Map<String, Object> results = new HashMap<>();
                            results.put("results", newList);
                            results.put("picture", BitmapUtils.bitmapToBase64(p1, Bitmap.CompressFormat.JPEG, 100));
                            Log.d(TAG, "onFaceMatched: " + newList);
                            activity.runOnUiThread(() -> {
                                if (channel != null) { // Added: Null check for channel
                                    channel.invokeMethod("createSearchProcess_onFaceMatched", results);
                                }
                                dismissAllowingStateLoss();
                            });
                        }
                    }

                    @Override
                    public void onFaceDetected(List<FaceSearchResult> result) {
                        super.onFaceDetected(result);
                    }

                    @Override
                    public void onProcessTips(int code) {
                        showFaceSearchPrecessTips(code);
                    }
                }).create();
        FaceSearchEngine.Companion.getInstance().initSearchParams(faceProcessBuilder);
    }

    @Override
    void showFaceSearchPrecessTips(int code) {
        switch (code) {
            case NO_MATCHED:
                //没有搜索匹配识别到任何人
                searchTipsTextView.setText(R.string.no_matched_face);
                break;

            case FACE_DIR_EMPTY:
                //人脸库没有人脸照片，没有使用SDK 插入人脸？
                searchTipsTextView.setText(R.string.face_dir_empty);
                break;


            case EMGINE_INITING:
                searchTipsTextView.setText(R.string.sdk_init);
                break;

            case SEARCH_PREPARED, SEARCHING:
                searchTipsTextView.setText(R.string.keep_face_tips);
                break;

            case IR_LIVE_ERROR:
//                binding.searchTips.setText(R.string.ir_live_error); //偶尔失败可以忽略
                break;

            case NO_LIVE_FACE:
                searchTipsTextView.setText(R.string.no_face_detected_tips);
                break;

            case FACE_TOO_SMALL:
                secondSearchTipsTextView.setText(R.string.come_closer_tips);
                break;

            // 单独使用一个textview 提示，防止上一个提示被覆盖。
            // 也可以自行记住上个状态，FACE_SIZE_FIT 中恢复上一个提示
            case FACE_TOO_LARGE:
                secondSearchTipsTextView.setText(R.string.far_away_tips);
                break;

            //检测到正常的人脸，尺寸大小OK
            case FACE_SIZE_FIT:
                secondSearchTipsTextView.setText("");
                break;

            case TOO_MUCH_FACE:
                secondSearchTipsTextView.setText(R.string.multiple_faces_tips);
                break;

            case THRESHOLD_ERROR:
                searchTipsTextView.setText(R.string.search_threshold_scope_tips);
                break;

            case MASK_DETECTION:
                searchTipsTextView.setText(R.string.no_mask_please);
                break;


            default:
                searchTipsTextView.setText("回调提示：" + code);
                break;

        }
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FaceSearchEngine.Companion.getInstance().stopSearchProcess();
    }
}
