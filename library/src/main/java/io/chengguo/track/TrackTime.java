package io.chengguo.track;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 绘制Track对应刻度的时间
 * Created by FingerArt on 2018/1/15.
 */
class TrackTime extends View {

    private static final String TAG = TrackTime.class.getSimpleName();
    private Paint mTextPaint;
    //大刻度的空间
    private int mLargeGraduationSpace;
    //RecyclerView 滚动的偏移量
    private int scrollOffset;
    private TrackView mParent;

    private TrackTime(Context context) {
        this(context, (AttributeSet) null);
    }

    private TrackTime(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private TrackTime(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TrackTime(Context context, TrackView trackView) {
        this(context);
        mParent = trackView;
        initAttrs();
    }

    private void initAttrs() {
        mLargeGraduationSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_space) * getResources().getInteger(R.integer.TrackView_default_graduation_space_by_track_count) * getResources().getInteger(R.integer.TrackView_default_graduation_large_space_by_graduation_count);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mParent.getGraduationTextSize());
        mTextPaint.setColor(mParent.getGraduationTextColor());
    }

    @Override
    @SuppressLint({"DefaultLocale", "DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        Utils.l(TAG, "onDraw() called");
        int widthHalf = canvas.getWidth() >> 1;
        int height = canvas.getHeight();
        //之前的时间索引
        int preIndex = 0;
        //第一个点的绘制偏移量
        int showOffset;
        if (scrollOffset < widthHalf) {
            showOffset = widthHalf - scrollOffset;
        } else {
            preIndex = (scrollOffset - widthHalf) / mLargeGraduationSpace;
            int hideOffset = (scrollOffset - widthHalf) % mLargeGraduationSpace;
            showOffset = -hideOffset;
        }

        //绘制文本
        int count = ((widthHalf << 1) - showOffset) / mLargeGraduationSpace + 2;
        for (int i = 0; i < count; i++) {
            int index = preIndex + i;
            String text = String.format("%02d:%02d", (index << 1) / 60, (index << 1) % 60);
            Rect rect = new Rect();
            mTextPaint.getTextBounds(text, 0, text.length(), rect);
            canvas.drawText(text, showOffset + mLargeGraduationSpace * i - (rect.width() >> 1), height - 10, mTextPaint);
        }
    }

    /**
     * 获取监听RecyclerView.OnScrollListener的监听器，以更新时间
     *
     * @return
     */
    public RecyclerView.OnScrollListener createRecyclerScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollOffset = recyclerView.computeHorizontalScrollOffset();
                Utils.l(TAG, "onScrolled() called with: computeHorizontalScrollOffset = [%s]", scrollOffset);
                invalidate();
            }
        };
    }
}
