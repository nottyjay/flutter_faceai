package com.alphay.flutter.plugin.faceai.flutter_faceai.factory;

import android.content.Context;

import com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.search.SearchUVCCamaraFragment;
import com.alphay.flutter.plugin.faceai.flutter_faceai.uvccamara.search.SearchUVCCamaraPlatformView;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class SearchUVCCameraFactory extends PlatformViewFactory {
    private final BinaryMessenger messenger;
    private final ActivityPluginBinding activityBinding;

    public SearchUVCCameraFactory(BinaryMessenger messenger, ActivityPluginBinding activityBinding) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.activityBinding = activityBinding;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return new SearchUVCCamaraPlatformView(
                context, messenger, activityBinding, args);
    }
}
