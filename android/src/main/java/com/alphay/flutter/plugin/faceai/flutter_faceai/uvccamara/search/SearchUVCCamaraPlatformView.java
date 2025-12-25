package com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.search;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


import com.alphay.flutter.plugin.faceai.flutter_faceai.R;

import java.util.Map;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class SearchUVCCamaraPlatformView implements PlatformView {

    private FragmentActivity activity;
    private ViewGroup containerView;
    private FragmentManager fragmentManager;
    private SearchUVCCamaraFragment fragment;
    private MethodChannel channel;

    private final Map<String, Object> initParams;

    public SearchUVCCamaraPlatformView(Context context, BinaryMessenger messenger, ActivityPluginBinding activityBinding, Object args) {
        this.activity = (FragmentActivity) activityBinding.getActivity();
        if (activity == null) {
            throw new IllegalStateException("宿主 Activity 必须继承 FragmentActivity 或其子类 (如 FlutterFragmentActivity) 以支持 AndroidX Fragment");
        }
        this.fragmentManager = activity.getSupportFragmentManager();
        this.containerView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.fragment_search_uvc_camara, null);
        this.channel = new MethodChannel(messenger, "com.alphay.flutter.faceai__flutter_search_uvc_camera_view");
        // 直接使用创建参数初始化 Fragment
        this.initParams = args instanceof Map ? (Map<String, Object>) args : null;
        if (this.initParams != null) {
            this.fragment = new SearchUVCCamaraFragment(this.initParams, activity, channel);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    @Nullable
    @Override
    public View getView() {
        return containerView;
    }

    @Override
    public void dispose() {
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
            fragment.onDestroyView();
        }
        containerView.removeAllViews();
        if (containerView.getParent() != null) {
            ((ViewGroup) containerView.getParent()).removeView(containerView);
        }
        // 清理 channel 回调
        if (channel != null) {
            channel.setMethodCallHandler(null);
        }
    }
}
