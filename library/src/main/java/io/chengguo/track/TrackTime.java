package io.chengguo.track;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.chengguo.track.library.R;

/**
 * 绘制Track对应刻度的时间
 * Created by FingerArt on 2018/1/15.
 */
class TrackTime extends View implements ITrackNotify {

    private static final String TAG = "TrackTime";
    private Paint mPaint;
    //大刻度的空间
    private int mLargeGraduationSpace;
    //一半的宽度 == Recycle' item 的宽度
    private int mWidthHalf;
    private int mHeight;
    //RecyclerView 滚动的偏移量
    private int scrollOffset;

    public TrackTime(Context context) {
        this(context, null);
    }

    public TrackTime(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackTime(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    private void initAttrs() {
        mLargeGraduationSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_graduation_space) << 2;
        int textSize = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_graduation_text);
        int textColor = getResources().getColor(R.color.TrackView_default_graduation_text);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidthHalf = getMeasuredWidth() >> 1;
        mHeight = getMeasuredHeight();
    }

    @Override
    @SuppressLint({"DefaultLocale", "DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        //之前的时间索引
        int preIndex = 0;
        //第一个点的绘制偏移量
        int showOffset;
        if (scrollOffset < mWidthHalf) {
            showOffset = mWidthHalf - scrollOffset;
        } else {
            preIndex = (scrollOffset - mWidthHalf) / mLargeGraduationSpace;
            int hideOffset = (scrollOffset - mWidthHalf) % mLargeGraduationSpace;
            showOffset = /*mLargeGraduationSpace */-hideOffset;
        }

        //绘制文本
        int count = ((mWidthHalf << 1) - showOffset) / mLargeGraduationSpace + 2;
        for (int i = 0; i < count; i++) {
            int index = preIndex + i;
            String text = String.format("%02d:%02d", (index << 1) / 60, (index << 1) % 60);
            Rect rect = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), rect);
            canvas.drawText(text, showOffset + mLargeGraduationSpace * i - (rect.width() >> 1), mHeight - 10, mPaint);
        }
    }

    public RecyclerView.OnScrollListener getRecyclerScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollOffset = recyclerView.computeHorizontalScrollOffset();
                Log.d(TAG, "onScrolled() called with: scrollOffset = [" + scrollOffset + "], scrollExtent = [" + recyclerView.computeHorizontalScrollExtent() + "], scrollRange = [" + recyclerView.computeHorizontalScrollRange() + "]");
                invalidate();
            }
        };
    }

    @Override
    public void onChanged(int position) {

    }
}
