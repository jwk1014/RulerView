package com.jwk.rulerlibrary;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JWK on 2017. 2. 22..
 */

public class RulerView extends RelativeLayout {
    private final static int SMALL = 1;
    private final static int MEDIUM = 2;
    private final static int BIG = 3;
    private final static int UNKNOWN = 4;
    private boolean zeroMediumLine, zeroBigLine, smallLineEnble, mediumLineEnble, bigLineEnble;
    private int dp1, sp1, value, valueCount, minValue, maxValue, lineMargin,
            smallLineWidth, mediumLineWidth, bigLineWidth,
            mediumLinePosition, bigLinePosition,
            smallLineHeight, mediumLineHeight, bigLineHeight,
            textSize, textMargin, width, height,
            bgColor, smallLineColor, mediumLineColor, bigLineColor, textColor;
    private float smallLineHeightPercent, mediumLineHeightPercent, bigLineHeightPercent;
    private RulerSurfaceView rulerSurfaceView;
    private CustomHorizontalScrollView horizontalScrollView;
    private ImageView indicatorImageView;

    public RulerView(Context context){
        super(context);
        initValue();
        initView();
    }

    public RulerView(Context context, AttributeSet attrs){
        super(context, attrs);
        initValue();
        getAttrValues(attrs, 0, 0);
        initView();
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initValue();
        getAttrValues(attrs, defStyleAttr, 0);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        initValue();
        getAttrValues(attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void getAttrValues(AttributeSet attrs, int defStyleAttr, int defStyleRes){
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RulerAttrs, defStyleAttr, defStyleRes);
        try {
            value = ta.getInteger(R.styleable.RulerAttrs_value, value);
            valueCount = ta.getInteger(R.styleable.RulerAttrs_valueCount, valueCount);
            minValue = ta.getInteger(R.styleable.RulerAttrs_minValue, minValue);
            maxValue = ta.getInteger(R.styleable.RulerAttrs_maxValue, maxValue);
            lineMargin = ta.getDimensionPixelSize(R.styleable.RulerAttrs_lineMargin, lineMargin);
            bgColor = ta.getColor(R.styleable.RulerAttrs_bgColor, bgColor);
            smallLineColor = ta.getColor(R.styleable.RulerAttrs_smallLineColor, smallLineColor);
            mediumLineColor = ta.getColor(R.styleable.RulerAttrs_mediumLineColor, smallLineColor);
            bigLineColor = ta.getColor(R.styleable.RulerAttrs_bigLineColor, mediumLineColor);
            smallLineEnble = ta.getBoolean(R.styleable.RulerAttrs_smallLineEnable, smallLineEnble);
            mediumLineEnble = ta.getBoolean(R.styleable.RulerAttrs_mediumLineEnable, mediumLineEnble);
            bigLineEnble = ta.getBoolean(R.styleable.RulerAttrs_bigLineEnable, bigLineEnble);
            smallLineWidth = ta.getDimensionPixelSize(R.styleable.RulerAttrs_smallLineWidth, smallLineWidth);
            mediumLineWidth = ta.getDimensionPixelSize(R.styleable.RulerAttrs_mediumLineWidth, smallLineWidth);
            bigLineWidth = ta.getDimensionPixelSize(R.styleable.RulerAttrs_bigLineWidth, smallLineWidth);
            bigLinePosition = ta.getInteger(R.styleable.RulerAttrs_bigLinePosition, bigLinePosition);
            mediumLinePosition = ta.getInteger(R.styleable.RulerAttrs_mediumLinePosition, bigLinePosition / 2);
            smallLineHeight = ta.getDimensionPixelSize(R.styleable.RulerAttrs_smallLineHeight, smallLineHeight);
            smallLineHeightPercent = ta.getFloat(R.styleable.RulerAttrs_smallLineHeightPercent, smallLineHeightPercent);
            mediumLineHeight = ta.getDimensionPixelSize(R.styleable.RulerAttrs_mediumLineHeight, mediumLineHeight);
            mediumLineHeightPercent = ta.getFloat(R.styleable.RulerAttrs_mediumLineHeightPercent, mediumLineHeightPercent);
            bigLineHeight = ta.getDimensionPixelSize(R.styleable.RulerAttrs_bigLineHeight, bigLineHeight);
            bigLineHeightPercent = ta.getFloat(R.styleable.RulerAttrs_bigLineHeightPercent, bigLineHeightPercent);
            textSize = ta.getDimensionPixelSize(R.styleable.RulerAttrs_textSize, textSize);
            textMargin = ta.getDimensionPixelSize(R.styleable.RulerAttrs_textMargin, textMargin);
            textColor = ta.getColor(R.styleable.RulerAttrs_textColor, bigLineColor);
            zeroMediumLine = ta.getBoolean(R.styleable.RulerAttrs_zeroBigLine, zeroMediumLine);
            zeroBigLine = ta.getBoolean(R.styleable.RulerAttrs_zeroBigLine, bigLineEnble);
        } finally {
            ta.recycle();
        }
    }

    public void initView(){
        horizontalScrollView = new CustomHorizontalScrollView(getContext());
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        horizontalScrollView.setLayoutParams(layoutParams1);
        addView(horizontalScrollView);
        if(value != 0)
            horizontalScrollView.scrollTo( ((value - minValue)/valueCount) * lineMargin, 0 );

        changeIndicatorImageView(dp1 * 15, dp1 * 12, CENTER_HORIZONTAL, R.drawable.white_triangle);

        HorizontalScrollView.LayoutParams layoutParams2 = new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rulerSurfaceView = new RulerSurfaceView(getContext(), new RulerSurfaceViewCallback());
        rulerSurfaceView.setLayoutParams(layoutParams2);
        horizontalScrollView.addView(rulerSurfaceView);
    }

    public void initValue(){
        dp1 = getContext().getResources().getDimensionPixelSize(R.dimen.rulerview_dp1);
        sp1 = getContext().getResources().getDimensionPixelSize(R.dimen.rulerview_sp1);
        bgColor = Color.BLACK;
        smallLineColor = Color.WHITE;
        //mediumLineColor = Color.WHITE;
        //bigLineColor = Color.WHITE;
        //value = 0;
        valueCount = 1;
        //minValue = 0;
        maxValue = 100;
        lineMargin = dp1 * 10;
        smallLineEnble = true;
        //mediumLineEnble = false;
        bigLineEnble = true;
        smallLineWidth = dp1 * 2;
        //mediumLineWidth = dp1 * 2;
        //bigLineWidth = dp1 * 2;
        //mediumLinePosition = 5;
        bigLinePosition = 10;
        //smallLineHeight = 0;
        smallLineHeightPercent = (0.33f);
        //mediumLineHeight = 0;
        mediumLineHeightPercent = (0.49f);
        //bigLineHeight = 0;
        bigLineHeightPercent = (0.66f);
        textSize = sp1 * 15;
        textMargin = dp1 * 15;
        //textColor = Color.WHITE;
        //zeroMediumLine = false;
        //zeroBigLine = true;
    }

    public ImageView getIndicatorImageView(){
        return indicatorImageView;
    }

    public void changeIndicatorImageView(int width, int height, int rule, @DrawableRes int imageSrc){
        ImageView imageView = new ImageView(getContext());
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(width,height);
        layoutParams1.addRule(rule);
        imageView.setLayoutParams(layoutParams1);
        imageView.setImageResource(imageSrc);
        addView(imageView);
        if(indicatorImageView != null)
            removeView(indicatorImageView);
        indicatorImageView = imageView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);
        rulerSurfaceView.setForceMeasuredDimension(
                View.MeasureSpec.makeMeasureSpec(width + (lineMargin*((maxValue - minValue)/valueCount)), MeasureSpec.EXACTLY),
                heightMeasureSpec);
    }

    public int getValue(){
        return minValue + horizontalScrollView.getValue();
    }

    public void setValue(int value){
        horizontalScrollView.scrollTo( ((value - minValue)/valueCount) * lineMargin, 0 );
    }

    int getLineType(int index){
        if(index != 0) {
            if(index % bigLinePosition == 0 && bigLineEnble)
                return BIG;
            else if(index % mediumLinePosition == 0 && mediumLineEnble)
                return MEDIUM;
            else if(smallLineEnble)
                return SMALL;
            else
                return UNKNOWN;
        }else {
            if(zeroBigLine)
                return BIG;
            else if(zeroMediumLine)
                return MEDIUM;
            else if(smallLineEnble)
                return SMALL;
            else
                return UNKNOWN;
        }
    }

    boolean isBigLine(int index){
        if(index != 0)
            return index % bigLinePosition == 0;
        else
            return zeroBigLine;
    }

    int getLineX(int index){
        return width / 2 + (lineMargin * (index - minValue) / valueCount);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%d", getValue());
    }

    class CustomHorizontalScrollView extends HorizontalScrollView{
        private int scrollX;
        private int timerCount;
        private Timer timer;
        private TimerTask timerTask;
        private Handler handler;

        public CustomHorizontalScrollView(Context context){
            super(context);
            handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    int remain = scrollX % lineMargin;
                    if(remain != 0){
                        int moveX = (scrollX / lineMargin + ((remain > lineMargin/2) ? 1 : 0)) * lineMargin;
                        smoothScrollTo(moveX, 0);
                    }
                }
            };
        }

        public int getValue(){
            int remain = scrollX % lineMargin;
            return scrollX / lineMargin * valueCount + ((remain > lineMargin/2) ? valueCount : 0);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if(ev.getAction() == MotionEvent.ACTION_UP){
                try {
                    if(timer != null){
                        try{ timer.cancel(); }catch (Exception e){}
                    }
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public synchronized void run() {
                            if(timerCount<=0) {
                                handler.sendEmptyMessage(0);
                                timer.cancel();
                            }
                            timerCount--;
                        }
                    };
                    timer.schedule(timerTask, 250, 250);
                }catch (Exception e){ e.printStackTrace(); }
                timerCount = 1;
            }
            return super.onTouchEvent(ev);
        }

        @Override
        protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
            timerCount = 1;
            this.scrollX = scrollX;
        }
    }

    class RulerSurfaceView extends SurfaceView{
        private SurfaceHolder surfaceHolder;
        public RulerSurfaceView(Context context, RulerSurfaceViewCallback rulerSurfaceViewCallback){
            super(context);
            surfaceHolder = getHolder();
            rulerSurfaceViewCallback.createRulerSurfaceViewThread(surfaceHolder);
            surfaceHolder.addCallback(rulerSurfaceViewCallback);
        }

        void setForceMeasuredDimension(int widthMeasureSpec, int heightMeasureSpec){
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(
                    View.MeasureSpec.makeMeasureSpec(width + (lineMargin*((maxValue - minValue)/valueCount)), MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    class RulerSurfaceViewCallback implements SurfaceHolder.Callback {
        private SurfaceHolder surfaceHolder;
        private RulerSurfaceViewThread rulerSurfaceViewThread;

        void createRulerSurfaceViewThread(SurfaceHolder surfaceHolder){
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if(rulerSurfaceViewThread == null)
                rulerSurfaceViewThread = new RulerSurfaceViewThread(surfaceHolder);
            rulerSurfaceViewThread.setRunning(true);
            rulerSurfaceViewThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            RulerSurfaceViewThread tmp = rulerSurfaceViewThread;
            rulerSurfaceViewThread = null;
            boolean retry = true;
            tmp.setRunning(false);
            while (retry) {
                try {
                    tmp.join();
                    retry = false;
                } catch (Exception e) {}
            }
        }
    }

    class RulerSurfaceViewThread extends Thread {
        private boolean running;
        private final SurfaceHolder surfaceHolder;

        RulerSurfaceViewThread(SurfaceHolder surfaceHolder){
            this.surfaceHolder = surfaceHolder;
        }

        void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    synchronized (surfaceHolder) {
                        c = surfaceHolder.lockCanvas(null);
                        if(c != null) {
                            Paint mPaint = new Paint();
                            mPaint.setColor(bgColor);
                            c.drawPaint(mPaint);

                            Paint mTextPaint = new Paint();
                            mTextPaint.setTextAlign(Paint.Align.CENTER);
                            mTextPaint.setTextSize(textSize);
                            mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
                            mTextPaint.setColor(textColor);
                            for (int i = minValue; i <= maxValue; i+=valueCount) {
                                float lineX = getLineX(i);
                                float lineY;
                                int lineColor;
                                int lineWidth;
                                switch (getLineType(i)){
                                    case SMALL:
                                        lineColor = smallLineColor;
                                        lineWidth = smallLineWidth;
                                        lineY = (smallLineHeight > 0) ? smallLineHeight : height * smallLineHeightPercent;
                                        break;
                                    case MEDIUM:
                                        lineColor = mediumLineColor;
                                        lineWidth = mediumLineWidth;
                                        lineY = (mediumLineHeight > 0) ? mediumLineHeight : height * mediumLineHeightPercent;
                                        break;
                                    case BIG:
                                        lineColor = bigLineColor;
                                        lineWidth = bigLineWidth;
                                        lineY = (bigLineHeight > 0) ? bigLineHeight : height * bigLineHeightPercent;
                                        c.drawText( "" + i,
                                                lineX,  lineY + textMargin,
                                                mTextPaint);
                                        break;
                                    default:
                                        continue;
                                }
                                mPaint.setColor(lineColor);

                                c.drawRect( lineX - (lineWidth / 2),    0,
                                            lineX + (lineWidth / 2),    lineY,
                                            mPaint);
                            }
                        }
                    }
                } finally {
                    if (c != null)
                        surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setLineMargin(int lineMargin) {
        this.lineMargin = lineMargin;
    }

    public int getLineMargin() {
        return lineMargin;
    }

    public int getSmallLineWidth() {
        return smallLineWidth;
    }

    public void setSmallLineWidth(int smallLineWidth) {
        this.smallLineWidth = smallLineWidth;
    }

    public int getBigLineWidth() {
        return bigLineWidth;
    }

    public void setBigLineWidth(int bigLineWidth) {
        this.bigLineWidth = bigLineWidth;
    }

    public int getBigLinePosition() {
        return bigLinePosition;
    }

    public void setBigLinePosition(int bigLinePosition) {
        this.bigLinePosition = bigLinePosition;
    }

    public int getSmallLineHeight() {
        return smallLineHeight;
    }

    public void setSmallLineHeight(int smallLineHeight) {
        this.smallLineHeight = smallLineHeight;
    }

    public int getBigLineHeight() {
        return bigLineHeight;
    }

    public void setBigLineHeight(int bigLineHeight) {
        this.bigLineHeight = bigLineHeight;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextMargin() {
        return textMargin;
    }

    public void setTextMargin(int textMargin) {
        this.textMargin = textMargin;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(@ColorInt int bgColor) {
        this.bgColor = bgColor;
    }

    public int getSmallLineColor() {
        return smallLineColor;
    }

    public void setSmallLineColor(@ColorInt int smallLineColor) {
        this.smallLineColor = smallLineColor;
    }

    public int getBigLineColor() {
        return bigLineColor;
    }

    public void setBigLineColor(@ColorInt int bigLineColor) {
        this.bigLineColor = bigLineColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(@ColorInt int textColor){
        this.textColor = textColor;
    }
}
