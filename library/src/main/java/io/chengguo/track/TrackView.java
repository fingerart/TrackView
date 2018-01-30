package io.chengguo.track;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
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

    private float trackTimeHeight;
    private TrackRecyclerView trackRecycler;

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
        trackTimeHeight = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_time_height);
    }

    private void initView(Context context) {
        //开启绘制
        setWillNotDraw(false);
        //组合事件
        TrackTime trackTime = new TrackTime(context);
        trackRecycler = new TrackRecyclerView(context);
        trackRecycler.addOnScrollListener(trackTime.createRecyclerScrollListener());
        //组合视图
        addView(trackRecycler, createMPLayoutParams());
        addView(trackTime, LayoutParams.MATCH_PARENT, (int) trackTimeHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = child.getLayoutParams();
            int childWidthMeasureSpec;
            int childHeightMeasureSpec;
            if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
            } else {
                childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
            }
            if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
            } else {
                childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height);
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        Log.d(TAG, "onDrawForeground: ");
        //绘制中间光标
        int cursorColor = getResources().getColor(R.color.TrackView_default_index);
        int cursorWidth = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_cursor);
        Paint cursorPaint = new Paint();
        cursorPaint.setColor(cursorColor);
        cursorPaint.setStrokeWidth(cursorWidth);
        canvas.drawLine(getMeasuredWidth() / 2, trackTimeHeight, getMeasuredWidth() / 2, getMeasuredHeight(), cursorPaint);
    }

    public void addTrack(int grade) {
        trackRecycler.howl(grade);
    }
}