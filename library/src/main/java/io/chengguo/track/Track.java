package io.chengguo.track;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.chengguo.track.library.R;

/**
 * 音轨item
 * Created by FingerArt on 2018/1/12.
 */
class Track extends View {
    private static final String TAG = "Track";
    private Paint mPaint;
    private int mBackground;

    public Track(Context context) {
        this(context, null);
    }

    public Track(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Track(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
        setBackgroundResource(R.color.TrackView_default_bg);
    }

    private void initAttrs() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackground = getResources().getColor(R.color.TrackView_default_bg);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        Log.d(TAG, "getSuggestedMinimumWidth: " + getSuggestedMinimumWidth());
        Log.d(TAG, "getDefaultSize: " + getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec));
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
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(mBackground);
        canvas.drawRect(0, 50, getRight(), getBottom(), mPaint);
    }

}
