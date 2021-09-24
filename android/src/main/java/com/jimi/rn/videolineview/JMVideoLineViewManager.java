package com.jimi.rn.videolineview;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nullable;

public class JMVideoLineViewManager extends SimpleViewManager<VideoLineView> {

    @Override
    public String getName() {
        return "JMVideoLineView";
    }

    @Override
    protected VideoLineView createViewInstance(ThemedReactContext reactContext) {
        return new VideoLineView(reactContext);
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder().put(
                "onProgress", MapBuilder.of("registrationName", "onProgress")).build();
    }

    @ReactProp(name = "lineColor")
    public void setLineColor(VideoLineView videoLineView,String lineColor)  {
        videoLineView.setLineColor(lineColor);
    }

    @ReactProp(name = "centerLineColor")
    public void setCenterLineColor(VideoLineView videoLineView,String centerLineColor)  {
        videoLineView.setmCenterlineColor(centerLineColor);
    }
}