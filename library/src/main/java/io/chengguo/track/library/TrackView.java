package io.chengguo.track.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FingerArt http://fingerart.me
 * @date 2017年08月25日 10:10
 */
public class TrackView extends FrameLayout {
    private static final String TAG = TrackView.class.getSimpleName();
    private static final boolean DEBUG = false;
    private InnerScrollView innerScrollView;
    private InnerTrackView track;
    protected boolean lock;

    public TrackView(Context context) {
        this(context, null);
    }

    public TrackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        innerScrollView = new InnerScrollView(context);
        FrameLayout.LayoutParams params = generateDefaultLayoutParams();
        params.width = 3;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 100;
        params.gravity = Gravity.CENTER_HORIZONTAL;

        addView(innerScrollView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(new CenterLineView(context), params);

        track = findViewById(innerScrollView, R.id.track_view);
    }

    public void go(int decibel) {
        track.addDecibel(decibel);
        go();
    }

    public void go() {
        track.notifyDecibelChange();
        post(new Runnable() {
            @Override
            public void run() {
                innerScrollView.scrollToLast();
            }
        });
    }

    public void setOnSlideGraduationListener(SlideGraduationListener onSlideGraduationListener) {
        innerScrollView.setOnSlideGraduationListener(onSlideGraduationListener);
    }

    public void lock(boolean lock) {
        this.lock = lock;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return lock || super.onInterceptTouchEvent(ev);
    }

    public static class InnerScrollView extends HorizontalScrollView {
        private static final String TAG = TrackView.class.getSimpleName();
        InnerTrackView innerTrackView;
        protected SlideGraduationListener graduationListener;

        public InnerScrollView(Context context) {
            this(context, null);
        }

        public InnerScrollView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public InnerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView(context, attrs, defStyleAttr);
        }

        private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
            FrameLayout container = new FrameLayout(context);
            innerTrackView = new InnerTrackView(context);
            innerTrackView.setId(R.id.track_view);
            container.addView(innerTrackView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        public void setOnSlideGraduationListener(SlideGraduationListener listener) {
            graduationListener = listener;
        }

        public void scrollToLast() {
            scrollTo(innerTrackView.getMeasuredWidth(), 0);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            log(TAG, "onScrollChanged() called with: l = [" + l + "], t = [" + t + "], oldl = [" + oldl + "], oldt = [" + oldt + "]");
            if (graduationListener != null) {
                graduationListener.onGraduationChanged(l < 0 ? 0 : l);
            }
        }
    }

    private static class InnerTrackView extends View {

        private static final String TAG = InnerTrackView.class.getSimpleName();
        private static final int UNIT = 50;
        private Paint mPaint;
        private int offsetLeft;
        private int offsetTop = 100;
        private int offsetBigGraduation;
        private int viewportW;
        private int viewportH;
        private ArrayList<Integer> decibels = new ArrayList<>();

        public InnerTrackView(@NonNull Context context) {
            this(context, null);
        }

        public InnerTrackView(@NonNull Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public InnerTrackView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        public void addDecibel(int decibel) {
            decibels.add(decibel);
        }

        public void setDecibel(List<Integer> decibels) {
            decibels.clear();
            decibels.addAll(decibels);
        }

        public void clear() {
            decibels.clear();
        }

        public void notifyDecibelChange() {
            getLayoutParams().width = getMeasuredWidth() + 1;
            requestLayout();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int sizeW = MeasureSpec.getSize(widthMeasureSpec);
            switch (MeasureSpec.getMode(widthMeasureSpec)) {
                case MeasureSpec.EXACTLY:
                    log(TAG, "widthMeasureSpec: EXACTLY [" + sizeW + "]");
                    break;
                case MeasureSpec.AT_MOST:
                    log(TAG, "widthMeasureSpec: AT_MOST [" + sizeW + "]");
                    break;
                case MeasureSpec.UNSPECIFIED:
                    log(TAG, "widthMeasureSpec: UNSPECIFIED [" + sizeW + "]");
                    break;
            }

            setMeasuredDimension(sizeW, MeasureSpec.getSize(heightMeasureSpec));
        }

        /**
         * 重复绘制的资源浪费
         *
         * @param canvas
         */
        @Override
        protected void onDraw(Canvas canvas) {
            log(TAG, "onDraw() called with: canvas = [" + canvas + "]");
            drawBackground(canvas);
            drawGraduated(canvas);
            drawTrack(canvas);
        }

        private void drawBackground(Canvas canvas) {
            mPaint.setColor(Color.parseColor("#F9F9F9"));
            canvas.drawRect(0, offsetTop, getRight(), getBottom(), mPaint);
        }

        private void drawGraduated(Canvas canvas) {
            int measuredWidth = getMeasuredWidth();
            if (offsetLeft == 0) {
                viewportW = measuredWidth;
                viewportH = getMeasuredHeight();
                offsetLeft = (measuredWidth >> 1) % (UNIT << 2);//计算左边开始的大刻度偏移值
                offsetBigGraduation = ((measuredWidth >> 1) - offsetLeft) / (UNIT << 2);
            }
            int count = (measuredWidth) / UNIT;//能显示的刻度总个数
            log(TAG, "count: " + count);

            int bigCount = ((count >> 2) + 1);
            float[] bigPoints = new float[bigCount << 2];
            log(TAG, "bigCount: " + bigCount);

            int smallCount = count - bigCount + 1;
            log(TAG, "smallCount: " + smallCount);
            float[] smallPoints = new float[smallCount << 2];
            for (int i = 0, x, y; i < count; i++) {
                x = i * UNIT + offsetLeft;
                if (i % 4 == 0) {
                    y = 40;
                    int bi = i >> 2;
                    log(TAG, i + " >> 2: " + bi);
                    bigPoints[bi * 4] = x;
                    bigPoints[bi * 4 + 1] = offsetTop;
                    bigPoints[bi * 4 + 2] = x;
                    bigPoints[bi * 4 + 3] = y + offsetTop;
                    if (bi >= offsetBigGraduation) {//跳过大的偏移刻度
                        drawTime(canvas, bi - offsetBigGraduation, x, offsetTop - 15);//draw time text
                    }
                } else {
                    y = 20;
                    int si = i - (i >> 2) - 1;
                    smallPoints[si * 4] = x;
                    smallPoints[si * 4 + 1] = offsetTop;
                    smallPoints[si * 4 + 2] = x;
                    smallPoints[si * 4 + 3] = y + offsetTop;
                }
            }

            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setColor(Color.parseColor("#E4E4E4"));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawLines(smallPoints, mPaint);//小刻度
            mPaint.setColor(Color.parseColor("#D2D2D2"));
            canvas.drawLines(bigPoints, mPaint);//大刻度
        }

        @SuppressLint("DefaultLocale")
        private void drawTime(Canvas canvas, int index, int x, int y) {
            mPaint.setTextSize(dipToPixels(getContext(), 12));
            mPaint.setStrokeWidth(3);

            String text = String.format("%02d:%02d", (index << 1) / 60, (index << 1) % 60);
            Rect rect = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), rect);
            log(TAG, "rect: " + rect.toShortString());
            mPaint.setColor(Color.parseColor("#808080"));
            int center = x - rect.centerX();
            log(TAG, "center: " + center);
            canvas.drawText(text, center, y, mPaint);
        }

        private void drawTrack(Canvas canvas) {
            int zeroPoint = viewportW >> 1;
            log(TAG, "viewportH: " + viewportH);
            int centerY = ((viewportH - offsetTop) >> 1) + offsetTop;
            mPaint.setColor(Color.parseColor("#ADADAD"));
            mPaint.setStrokeWidth(2.5f);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            for (int i = 0; i < decibels.size(); i++) {
                int x = zeroPoint + i * 5;
                int db = decibels.get(i) < 5 ? 5 : decibels.get(i);
                canvas.drawLine(x, centerY + db, x, centerY - db, mPaint);
            }
        }
    }

    private static class CenterLineView extends View {

        public CenterLineView(Context context) {
            this(context, null);
        }

        public CenterLineView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CenterLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.parseColor("#F79898"));
        }
    }

    public interface SlideGraduationListener {
        void onGraduationChanged(int currentGraduation);
    }

    static int dipToPixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @Nullable
    static <T> T findViewById(@NonNull View view, @IdRes int id) {
        return ((T) view.findViewById(id));
    }

    static void log(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }
}