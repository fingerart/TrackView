package io.chengguo.track;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
public class TrackView extends HorizontalScrollView {
    private static final String TAG = TrackView.class.getSimpleName();

    public TrackView(Context context) {
        this(context, null);
    }

    public TrackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        FrameLayout container = new FrameLayout(context);
        InnerTrackView innerTrackView = new InnerTrackView(context);
        CenterLineView centerLineView = new CenterLineView(context);
        FrameLayout.LayoutParams params = generateDefaultLayoutParams();
        params.width = 2;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        container.addView(innerTrackView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(centerLineView, params);
        addView(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "onScrollChanged() called with: l = [" + l + "], t = [" + t + "], oldl = [" + oldl + "], oldt = [" + oldt + "]");
    }

    private static class InnerTrackView extends View {

        private static final String TAG = InnerTrackView.class.getSimpleName();
        private static final int UNIT = 50;
        protected Paint mPaint;
        protected int viewport;

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

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int size = MeasureSpec.getSize(widthMeasureSpec);
            switch (MeasureSpec.getMode(widthMeasureSpec)) {
                case MeasureSpec.EXACTLY:
                    Log.d(TAG, "widthMeasureSpec: EXACTLY [" + size + "]");
                    break;
                case MeasureSpec.AT_MOST:
                    Log.d(TAG, "widthMeasureSpec: AT_MOST [" + size + "]");
                    break;
                case MeasureSpec.UNSPECIFIED:
                    Log.d(TAG, "widthMeasureSpec: UNSPECIFIED [" + size + "]");
                    break;
            }
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
            viewport = ((View) getParent().getParent()).getMeasuredWidth();
            Log.d(TAG, "viewport: " + viewport);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            drawGraduated(canvas);
        }

        private void drawGraduated(Canvas canvas) {
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(0);
            int offset = (viewport >> 1) % UNIT;
            int count = viewport / 50;
            float[] pts = new float[count << 2];
            for (int i = 0, x, y; i < count; i++) {
                x = i * UNIT + offset;
                y = i % 4 == 0 ? 40 : 20;
                pts[i * 4] = x;
                pts[i * 4 + 1] = 0;
                pts[i * 4 + 2] = x;
                pts[i * 4 + 3] = y;
            }
            canvas.drawLines(pts, mPaint);
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
            canvas.drawColor(Color.RED);
        }
    }

    public static int dipToPixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
}