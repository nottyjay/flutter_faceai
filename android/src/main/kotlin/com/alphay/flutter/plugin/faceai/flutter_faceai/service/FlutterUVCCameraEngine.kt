package com.alphay.flutter.plugin.faceai.flutter_faceai.service

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ai.face.faceSearch.search.FaceSearchEngine
import com.ai.face.faceSearch.search.SearchProcessBuilder
import com.ai.face.faceSearch.search.SearchProcessCallBack
import com.ai.face.faceSearch.utils.FaceSearchResult
import com.alphay.flutter.plugin.faceai.flutter_faceai.config.FaceImageConfig
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel

object FlutterUVCCameraEngine {

    private const val TAG = "FlutterUVCCameraEngine"
    var irBitmap: Bitmap? = null
    var rgbBitmap: Bitmap? = null
    var irReady: Boolean = false
    var rgbReady: Boolean = false

    var faceProcessBuilder: SearchProcessBuilder? = null

    fun createSearchProcess(
        channel: MethodChannel,
        binding: ActivityPluginBinding,
        params: Map<String, Any>
    ) {
        val thresholdDouble: Double? = params.get("threshold") as? Double
        val multipe: Boolean? = params.get("multipe") as? Boolean
        val threshold = thresholdDouble?.toFloat() ?: 0.88f
        var activity = binding.activity as FragmentActivity
        var fragment = getCurrentFragment(activity)
        var faceProcessBuilderTemp = SearchProcessBuilder.Builder(activity)
            .setLifecycleOwner(fragment)
            .setThreshold(threshold)
            .setCallBackAllMatch(true)
            .setFaceLibFolder(FaceImageConfig.getInstance().cacheSearchFaceDir)
            .setCameraType(SearchProcessBuilder.CameraType.UVC_CAMERA)
        if (multipe == true) {
            faceProcessBuilderTemp.setSearchType(SearchProcessBuilder.SearchType.N_SEARCH_M)
        }

        faceProcessBuilderTemp.setProcessCallBack(object : SearchProcessCallBack() {
            override fun onMostSimilar(faceId: String?, score: Float, bitmap: Bitmap?) {
                FaceSearchEngine.Companion.getInstance().stopSearchProcess()
                channel.invokeMethod(
                    "createSearchProcess_onMostSimilar", mapOf(
                        "faceId" to faceId
                    )
                )
            }

            override fun onFaceMatched(p0: List<FaceSearchResult?>?, p1: Bitmap?) {
                super.onFaceMatched(p0, p1)
                Log.d(TAG, "onFaceMatched: $p0")
            }

            override fun onFaceDetected(result: List<FaceSearchResult?>?) {
//                FaceSearchEngine.Companion.getInstance().stopSearchProcess()
                if (result?.isNotEmpty() ?: false) {
                    val newList = result.mapNotNull { item ->
                        // 安全处理item为空的情况，并过滤faceScore <= 0的项
                        item?.takeIf { it.faceScore > 0 }?.let { nonNullItem ->
                            mapOf(
                                "faceId" to nonNullItem.faceName,  // 映射 faceName 到 faceId
                                "score" to nonNullItem.faceScore   // 映射 faceScore 到 score
                            )
                        }
                    }
                    if (newList.isNotEmpty() && newList.size > 0) {
                        Log.d(TAG, "onFaceDetected: $newList")
                        channel.invokeMethod(
                            "createSearchProcess_onFaceDetected", newList
                        )
                    }
                } else {
//                    FaceSearchEngine.Companion.getInstance().initSearchParams(faceProcessBuilder!!);
                }

            }

            override fun onProcessTips(code: Int) {
                binding.activity.runOnUiThread {
                    channel.invokeMethod(
                        "createSearchProcess_onProcessTips", mapOf(
                            "code" to code
                        )
                    )
                }

            }
        })
        faceProcessBuilder = faceProcessBuilderTemp.create()
        FaceSearchEngine.Companion.getInstance().initSearchParams(faceProcessBuilder!!);
    }

    fun faceSearchSetBitmap(bitmap: Bitmap, type: String) {
        if (type == "IR") {
            irBitmap = bitmap
            irReady = true
        } else if (type == "RGB") {
            rgbBitmap = bitmap
            rgbReady = true
        }

        if (irReady && rgbReady) {
            //送数据进入SDK
            FaceSearchEngine.Companion.getInstance().runSearchWithIR(irBitmap!!, rgbBitmap!!);
            irReady = false;
            rgbReady = false;
        }
    }

    fun stopSearchProcess() {
        if (faceProcessBuilder == null) {
            return
        }
        irReady = false;
        rgbReady = false;
        irBitmap = null
        rgbBitmap = null
        FaceSearchEngine.Companion.getInstance().stopSearchProcess();
    }

    fun retrySearchProcess() {
        if (faceProcessBuilder == null) {
            return
        }
        irReady = false;
        rgbReady = false;
        irBitmap = null
        rgbBitmap = null
        FaceSearchEngine.Companion.getInstance().initSearchParams(faceProcessBuilder!!);
    }

    // 核心方法：从 Activity 中获取当前生效的 Fragment
    private fun getCurrentFragment(activity: Activity): Fragment? {
        val currentActivity = activity ?: return null

        // 确保 Activity 是 FragmentActivity（支持 Fragment）
        if (currentActivity !is FragmentActivity) {
            return null
        }

        val fragmentManager = currentActivity.supportFragmentManager

        // 方式1：遍历所有 Fragment，找到可见且活跃的
        for (fragment in fragmentManager.fragments) {
            if (fragment.isVisible && fragment.isResumed) {
                return fragment
            }
        }

        // 方式2：直接查找 Flutter 主 Fragment（适用于纯 Flutter 场景）
        return fragmentManager.findFragmentByTag("flutter_fragment")
    }
}