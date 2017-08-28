package io.chengguo.track;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

/**
 * @author FingerArt http://fingerart.me
 * @date 2017年08月25日 10:10
 */
public class TrackView extends FrameLayout {
    protected static int offsetLeft;
    protected static int offsetBigGraduation;
    private static final String TAG = TrackView.class.getSimpleName();

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
        InnerScrollView innerScrollView = new InnerScrollView(context);
        CenterLineView centerLineView = new CenterLineView(context);
        FrameLayout.LayoutParams params = generateDefaultLayoutParams();
        params.width = 2;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 100;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(innerScrollView, ViewGroup.LayoutParams.MATCH_PARENT, dipToPixels(context, 200));
        addView(centerLineView, params);
    }

    public static class InnerScrollView extends HorizontalScrollView {
        private static final String TAG = io.chengguo.track.TrackView.class.getSimpleName();

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
            InnerTrackView innerTrackView = new InnerTrackView(context);
            container.addView(innerTrackView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            Log.d(TAG, "onScrollChanged() called with: l = [" + l + "], t = [" + t + "], oldl = [" + oldl + "], oldt = [" + oldt + "]");
        }
    }

    private static class InnerTrackView extends View implements OnClickListener {

        private static final String TAG = InnerTrackView.class.getSimpleName();
        private static final int UNIT = 50;
        private int offsetTop = 100;
        private Paint mPaint;

        public InnerTrackView(@NonNull Context context) {
            this(context, null);
        }

        public InnerTrackView(@NonNull Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
            setOnClickListener(this);
        }

        public InnerTrackView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int sizeW = MeasureSpec.getSize(widthMeasureSpec);
            switch (MeasureSpec.getMode(widthMeasureSpec)) {
                case MeasureSpec.EXACTLY:
                    Log.d(TAG, "widthMeasureSpec: EXACTLY [" + sizeW + "]");
                    break;
                case MeasureSpec.AT_MOST:
                    Log.d(TAG, "widthMeasureSpec: AT_MOST [" + sizeW + "]");
                    break;
                case MeasureSpec.UNSPECIFIED:
                    Log.d(TAG, "widthMeasureSpec: UNSPECIFIED [" + sizeW + "]");
                    break;
            }

            setMeasuredDimension(sizeW, MeasureSpec.getSize(heightMeasureSpec));
//            viewport = ((View) getParent().getParent()).getMeasuredWidth();
//            Log.d(TAG, "viewport: " + viewport);
        }

        /**
         * 重复绘制的资源浪费
         *
         * @param canvas
         */
        @Override
        protected void onDraw(Canvas canvas) {
            Log.d(TAG, "onDraw() called with: canvas = [" + canvas + "]");
            drawBackground(canvas);
            drawGraduated(canvas);
        }

        private void drawBackground(Canvas canvas) {
            mPaint.setColor(Color.parseColor("#F9F9F9"));
            canvas.drawRect(0, offsetTop, getRight(), getBottom(), mPaint);
        }

        private void drawGraduated(Canvas canvas) {
            int measuredWidth = getMeasuredWidth();
            if (offsetLeft == 0) {
                offsetLeft = (measuredWidth >> 1) % (UNIT << 2);//计算左边开始的大刻度偏移值
                offsetBigGraduation = ((measuredWidth >> 1) - offsetLeft) / (UNIT << 2);
            }
            int count = (measuredWidth) / UNIT;//能显示的刻度总个数
            Log.d(TAG, "count: " + count);

            int bigCount = ((count >> 2) + 1);
            float[] bigPoints = new float[bigCount << 2];
            Log.d(TAG, "bigCount: " + bigCount);

            int smallCount = count - bigCount + 1;
            Log.d(TAG, "smallCount: " + smallCount);
            float[] smallPoints = new float[smallCount << 2];
            for (int i = 0, x, y; i < count; i++) {
                x = i * UNIT + offsetLeft;
                if (i % 4 == 0) {
                    y = 40;
                    int bi = i >> 2;
                    Log.d(TAG, i + " >> 2: " + bi);
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
            mPaint.setColor(Color.RED);
            canvas.drawPoint(x, y, mPaint);

            String text = String.format("%02d:%02d", (index << 1) / 60, (index << 1) % 60);
            Rect rect = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), rect);
            Log.d(TAG, "rect: " + rect.toShortString());
            mPaint.setColor(Color.parseColor("#808080"));
            int center = x - rect.centerX();
            Log.d(TAG, "center: " + center);
            canvas.drawText(text, center, y, mPaint);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick() called with: v = [" + v + "]");
            getLayoutParams().width = getMeasuredWidth() + 100;
            requestLayout();
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

    public static int dipToPixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
}