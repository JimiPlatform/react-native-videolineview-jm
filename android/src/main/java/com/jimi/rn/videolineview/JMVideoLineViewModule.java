package com.jimi.rn.videolineview;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.annotations.ReactProp;

public class JMVideoLineViewModule extends ReactContextBaseJavaModule{
    private ReactApplicationContext mContext;


    public JMVideoLineViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mContext=reactContext;
    }

    @Override
    public String getName() {
        return "JMVideoLineViewModule";
    }

    @ReactMethod
    public void startAutoRun(boolean isAutoRun){

    }
}