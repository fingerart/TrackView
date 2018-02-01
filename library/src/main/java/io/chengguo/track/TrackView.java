package io.chengguo.track;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.chengguo.track.library.R;

import static io.chengguo.track.Utils.createMPLayoutParams;
import static io.chengguo.track.Utils.l;

/**
 * 音频轨迹
 * Created by FingerArt on 2018/1/12.
 */
public class TrackView extends ViewGroup {
    private static final String TAG = TrackView.class.getSimpleName();

    /**
     * Track颜色
     */
    private int mTrackColor;
    /**
     * 小刻度颜色
     */
    private int mGraduationSmallColor;
    /**
     * 大刻度颜色
     */
    private int mGraduationLargeColor;
    /**
     * 刻度文本颜色
     */
    private int mGraduationTextColor;
    /**
     * 刻度文本大小
     */
    private int mGraduationTextSize;
    /**
     * TrackTime高度
     */
    private float mTrackTimeHeight;

    private Paint mCursorPaint;
    private Paint mBackgroundPaint;
    private TrackRecyclerView mTrackRecycler;
    private ITrackAdapter mTrackAdapter;
    private TrackEngine mTrackEngine;
    private ITimeChangeListener mGraduationListener;
    private boolean disableTouch = false;

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
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TrackView);
        int cursorWidth = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_cursor);
        int cursorColor = ta.getColor(R.styleable.TrackView_cursor_color, context.getResources().getColor(R.color.TrackView_default_cursor));
        int backgroundColor = ta.getColor(R.styleable.TrackView_background_color, context.getResources().getColor(R.color.TrackView_default_bg));
        mTrackColor = ta.getColor(R.styleable.TrackView_track_color, context.getResources().getColor(R.color.TrackView_default_track));
        mGraduationSmallColor = ta.getColor(R.styleable.TrackView_graduation_s_color, context.getResources().getColor(R.color.TrackView_default_graduation_s));
        mGraduationLargeColor = ta.getColor(R.styleable.TrackView_graduation_l_color, context.getResources().getColor(R.color.TrackView_default_graduation_l));
        mGraduationTextColor = ta.getColor(R.styleable.TrackView_graduation_text_color, context.getResources().getColor(R.color.TrackView_default_graduation_text));
        mGraduationTextSize = ta.getDimensionPixelOffset(R.styleable.TrackView_graduation_text_size, context.getResources().getDimensionPixelOffset(R.dimen.TrackView_default_graduation_text));
        ta.recycle();
        mTrackTimeHeight = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_time_height);
        //背景Paint
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(backgroundColor);
        //光标Paint
        mCursorPaint = new Paint();
        mCursorPaint.setColor(cursorColor);
        mCursorPaint.setStrokeWidth(cursorWidth);
        mTrackEngine = new TrackEngine(this);
    }

    private void initView(Context context) {
        //开启绘制
        setWillNotDraw(false);
        //组合事件
        TrackTime trackTime = new TrackTime(context, this);
        mTrackRecycler = new TrackRecyclerView(context, this);
        mTrackRecycler.addOnScrollListener(trackTime.createRecyclerScrollListener());
        mTrackRecycler.addOnScrollListener(createRecyclerScrollListener());
        //组合视图
        addView(mTrackRecycler, createMPLayoutParams());
        addView(trackTime, LayoutParams.MATCH_PARENT, (int) mTrackTimeHeight);
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
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        super.onDraw(canvas);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        drawCursor(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        l(TAG, "drawBackground() called");
        canvas.drawRect(0, mTrackTimeHeight, getRight(), getBottom(), mBackgroundPaint);
    }

    /**
     * 绘制光标
     *
     * @param canvas
     */
    private void drawCursor(Canvas canvas) {
        l(TAG, "onDrawForeground: %s", canvas);
        int widthHalf = canvas.getWidth() >> 1;
        int height = canvas.getHeight();
        //绘制中间光标
        canvas.drawLine(widthHalf, mTrackTimeHeight, widthHalf, height, mCursorPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (disableTouch) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private RecyclerView.OnScrollListener createRecyclerScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int scrollOffset = recyclerView.computeHorizontalScrollOffset();
                l(TAG, "computeHorizontalScrollOffset: %s", scrollOffset);
                if (mGraduationListener != null) {
                    mGraduationListener.onTimeChanged(scrollOffset);
                }
            }
        };
    }

    @Override
    protected void onDetachedFromWindow() {
        l(TAG, "onDetachedFromWindow() called");
        super.onDetachedFromWindow();
        stop();
    }

    void addTrack() {
        if (mTrackAdapter != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mTrackRecycler.howl(mTrackAdapter.getAmplitude());
                }
            });
        }
    }

    public int getTrackColor() {
        return mTrackColor;
    }

    public int getGraduationSmallColor() {
        return mGraduationSmallColor;
    }

    public int getGraduationLargeColor() {
        return mGraduationLargeColor;
    }

    public int getGraduationTextColor() {
        return mGraduationTextColor;
    }

    public int getGraduationTextSize() {
        return mGraduationTextSize;
    }

    public float getTrackTimeHeight() {
        return mTrackTimeHeight;
    }

    /**
     * 设置Track适配器
     *
     * @param trackAdapter
     */
    public void setTrackAdapter(ITrackAdapter trackAdapter) {
        mTrackAdapter = trackAdapter;
    }

    /**
     * 设置音轨滑动监听器
     *
     * @param graduationListener
     */
    public void setGraduationListener(ITimeChangeListener graduationListener) {
        mGraduationListener = graduationListener;
    }

    /**
     * 开始
     */
    public void start() {
        disableTouch = true;
        if (mTrackEngine != null) {
            mTrackEngine.start();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        disableTouch = false;
        if (mTrackEngine != null) {
            mTrackEngine.stop();
        }
    }

    /**
     * 清理数据
     */
    public void clear() {
        disableTouch = false;
        stop();
        mTrackRecycler.clear();
    }

    public void setTracks(List<Integer> tracks) {
        mTrackRecycler.howl(tracks);
    }
}