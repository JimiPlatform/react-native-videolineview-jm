package com.jimi.rn.videolineview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class VideoLineView extends View {
    Paint paint = new Paint();
    //中间线颜色
    private int mCenterlineColor;
    //刻度线颜色
    private int mLineColor;
    //文字颜色
    private int mTextColor;
    //视频段的背景颜色
    private int mVideoColor;
    //视频轴的背景颜色
    private int mBgColor;
    //文字大小
    private int mTextSize;
    //当前刻度时间
    private Date mTime = new Date();
    private int mHeight;
    private int mWidth;
    private int intervalWidth = 30;//每个刻度30dp宽度
    private int intervalMinute = 10;//每个刻度表示10分钟
    private List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
    private float mStartX = 0;
    private VideoLineViewChangeListener videoLineViewChangeListener;
    private boolean autoRun = true;//是否自动改变时间
    private Object data;//用来绑定用户数据
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
    };
    private Runnable autoRunTime = new Runnable() {
        @Override
        public void run() {
            if (mTime != null && mTime.getTime() < videoInfos.get(videoInfos.size() - 1).getEndTime().getTime()) {
                mTime.setTime(mTime.getTime() + 1000);
                invalidate();
                handler.postDelayed(this, 1000);
            }
        }
    };


    public VideoLineView(Context context) {
        this(context, null);
    }

    public VideoLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public VideoLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VideoLineView);
        mCenterlineColor = typedArray.getColor(R.styleable.VideoLineView_centerlineColor, Color.RED);
        mLineColor = typedArray.getColor(R.styleable.VideoLineView_lineColor, ContextCompat.getColor(getContext(), R.color.line_video));
        mTextColor = typedArray.getColor(R.styleable.VideoLineView_txColor, ContextCompat.getColor(getContext(), R.color.white));
        mVideoColor = typedArray.getColor(R.styleable.VideoLineView_videoColor, 0x770000FF);//默认带透明蓝色
        mBgColor = typedArray.getColor(R.styleable.VideoLineView_bgColor, Color.WHITE);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.VideoLineView_textSize, Functions.sp2px(getContext(), 10));
        typedArray.recycle();
    }

    public Date getmTime() {
        return mTime;
    }

    public void setmTime(Date mTime) {
        if (mTime != null) {
            this.mTime.setTime(mTime.getTime());
            startRunTime();
        }
    }

    public List<VideoInfo> getVideoInfos() {
        return videoInfos;
    }

    public void setVideoInfos(List<VideoInfo> videoInfos) {
        this.videoInfos = videoInfos;
        if (videoInfos != null && !videoInfos.isEmpty()) {
            setmTime(videoInfos.get(0).getStartTime());
        }
    }

    public boolean isAutoRun() {
        return autoRun;
    }

    private Date getMaxTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(videoInfos.get(0).getEndTime());
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return  calendar.getTime();

    }
    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    public VideoLineViewChangeListener getVideoLineViewChangeListener() {
        return videoLineViewChangeListener;
    }

    public void setVideolineChangeListener(VideoLineViewChangeListener videoLineViewChangeListener) {
        this.videoLineViewChangeListener = videoLineViewChangeListener;
    }

    public Object getData() {
        return data;
    }

    public VideoLineView setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = getHeight();
        mWidth = getWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = measureVideoLine(heightMeasureSpec);
        mWidth = measureVideoLine(widthMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    private int measureVideoLine(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                result = Functions.dip2px(getContext(), 32);//默认40dp
                break;
            case MeasureSpec.EXACTLY:
                result = Math.max(specSize, Functions.dip2px(getContext(), 32));
                break;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                stopRunTime();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                float distance = event.getX() - mStartX;
                long zeng = getTimeByDistance(distance);
                mStartX = event.getX();
                mTime.setTime(mTime.getTime() - zeng);
                /*if(mTime.getTime()>=getMaxTime().getTime()){
                    return false;
                }*/
                invalidate();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (videoLineViewChangeListener != null) {
                        if (mTime.getTime() > new Date().getTime()) {
                            Toast.makeText(getContext(), "不可以超过当前时间", Toast.LENGTH_LONG).show();
                            mTime.setTime(mTime.getTime() + zeng);//滑动条撤回原处
                            invalidate();
                            return true;
                        }
                        videoLineViewChangeListener.onChange(mTime, this);

                    }
                    disPatchEvent(mTime.getTime()+zeng);
                    startRunTime();
                }
                break;
        }
        return true;
    }

    private void disPatchEvent(Long time) {
        WritableMap eventMap = Arguments.createMap();
        eventMap.putDouble("progress", time);
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "onProgress",
//                "topChange",
                eventMap
        );
    }



    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(mLineColor);
        paint.setStrokeWidth(Functions.dip2px(getContext(), 2));
        //绘制刻度尺
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, paint);
        //往左绘制
        drawLine(canvas, paint, mTime.getTime(), mWidth / 2, true);
        //往右绘制
        drawLine(canvas, paint, mTime.getTime(), mWidth / 2, false);
        //中间线最后绘制
        paint.setColor(mCenterlineColor);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, paint);
    }

    private void drawLine(Canvas canvas, Paint paint, long time, float startX, boolean left) {
        paint.setColor(mLineColor);
        long residue = time % (60 * 1000 * intervalMinute);//能被十分钟求余
        long nextTime = 0;
        float offset = Functions.dip2px(getContext(), intervalWidth);
        if (residue == 0) {
            nextTime = left ? time - 60 * 1000 * intervalMinute : time + 60 * 1000 * intervalMinute;
            startX = left ? startX - offset : startX + offset;
        } else {
            nextTime = left ? time - residue : time - residue + 60 * 1000 * intervalMinute;
            //重新计算startX;
            float v = offset / (60 * 10);//计算出每一秒的距离
            startX = left ? startX - residue / 1000 * v : startX + (60 * 1000 * intervalMinute - residue) / 1000 * v;
        }
        if (startX < 0 || startX > mWidth) {
            return;
        }
        int endY = nextTime % (3600 * 1000) == 0 ? 6 : 3;
        paint.setStrokeWidth(Functions.dip2px(getContext(), 1));
        paint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        canvas.drawLine(startX, mHeight / 2 + Functions.dip2px(getContext(), endY), startX, mHeight / 2 - Functions.dip2px(getContext(), endY), paint);
        if (endY == 6) {//10分钟整点
            Date t = new Date(nextTime);
            paint.setColor(mTextColor);
            paint.setTextSize(mTextSize);
            paint.setAntiAlias(true);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float value = fontMetrics.bottom - fontMetrics.top;
            float v = paint.measureText(t.getHours() + ":00");
            canvas.drawText(t.getHours() + ":00", startX - v / 2, mHeight / 2 + value + 8, paint);
        }
        drawLine(canvas, paint, nextTime, startX, left);
    }


    /**
     * 根据时间获取该时间在当前数轴的X坐标
     *
     * @param date
     * @return
     */
    private float getXByTime(Date date) {
        float offset_px = Functions.dip2px(getContext(), intervalWidth);
        float secondsWidth = offset_px / (60 * 10);
        long s = (date.getTime() - mTime.getTime()) / 1000;
        float x = mWidth / 2.0f + s * secondsWidth;
        return x;
    }

    private int getTimeByDistance(float distance) {
        float offset_px = Functions.dip2px(getContext(), intervalWidth);
        float secondsWidth = offset_px / (60 * 10);
        return Math.round(distance / secondsWidth) * 1000;
    }

    /**
     * 开启自动计时  前提autoRun=true
     * 同步时间
     */
    public void startRunTime() {
        handler.removeCallbacks(autoRunTime);
        if (autoRun && videoInfos != null && !videoInfos.isEmpty()) {
            handler.postDelayed(autoRunTime, 1000);
        }
    }

    public void stopRunTime() {
        handler.removeCallbacks(autoRunTime);
    }

    public void setLineColor(String lineColor){
        mLineColor=Color.parseColor(lineColor);
        postInvalidate();
    }

    public void setmCenterlineColor(String centerlineColor){
        mCenterlineColor=Color.parseColor(centerlineColor);
        postInvalidate();
    }
}
