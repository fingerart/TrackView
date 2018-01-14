package io.chengguo.track;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import io.chengguo.track.library.R;

import static io.chengguo.track.Utils.createMPLayoutParams;

/**
 * 音频轨迹
 * Created by FingerArt on 2018/1/12.
 */
public class TrackView extends ViewGroup {
    private static final String TAG = "TrackView";

    private TrackRecyclerView trackRecyclerView;

    public TrackView(@NonNull Context context) {
        this(context, null);
    }

    public TrackView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
    }

    private void initView(Context context) {
        setWillNotDraw(false);
        addTrackRecycler(context);
    }

    private void addTrackRecycler(Context context) {
        trackRecyclerView = new TrackRecyclerView(context);
        addView(trackRecyclerView, createMPLayoutParams());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            trackRecyclerView.layout(0, 0, r, b - t);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制中间光标
        int cursorColor = getResources().getColor(R.color.TrackView_default_index);
        int offsetTop = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_offset_top);
        Paint cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorPaint.setStrokeCap(Paint.Cap.ROUND);
        cursorPaint.setColor(cursorColor);
        cursorPaint.setStrokeWidth(3);
        canvas.drawLine(getMeasuredWidth() / 2, offsetTop, getMeasuredWidth() / 2, getMeasuredHeight(), cursorPaint);
    }
}