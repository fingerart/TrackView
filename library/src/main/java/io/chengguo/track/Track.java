package io.chengguo.track;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

import io.chengguo.track.library.R;

/**
 * 音轨item
 * Created by FingerArt on 2018/1/12.
 */
class Track extends View {
    private static final String TAG = "Track";
    //默认索引
    private static final int DEFAULT_POSITION = -1;
    //每小格的距离
    private int mGraduationSpace;
    //每4小格是一大格
    private static final int UNIT_GRADUATION_INTERVAL = 4;
    private static final int UNIT_TRACK = 6;
    private Paint mPaint;
    private int mBackground;
    //小刻度颜色
    private int mGraduationSColor;
    //大刻度颜色
    private int mGraduationLColor;
    //在RecyclerView中的索引
    private int mCurrentPosition = DEFAULT_POSITION;
    private int offsetTop = 50;
    private int mWidth;
    public boolean isFull = false;
    private List<Integer> mTrackData;

    public Track(Context context) {
        this(context, null);
    }

    public Track(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Track(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    private void initAttrs() {
        setId(R.id.track_view);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mBackground = getResources().getColor(R.color.TrackView_default_bg);
        mGraduationSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_graduation_space);
        mGraduationSColor = getResources().getColor(R.color.TrackView_default_graduation_s);
        mGraduationLColor = getResources().getColor(R.color.TrackView_default_graduation_l);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: ");
        if (canDraw()) {
            drawBackground(canvas);
            drawGraduation(canvas);
            drawTrack(canvas);
        }
    }

    /**
     * 是否能够进行绘制
     *
     * @return
     */
    private boolean canDraw() {
        return mCurrentPosition > DEFAULT_POSITION;
    }

    /**
     * 绘制背景
     * TODO: 转移到父布局减少绘制次数
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        mPaint.setColor(mBackground);
        canvas.drawRect(0, offsetTop, getRight(), getBottom(), mPaint);
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawGraduation(Canvas canvas) {
        int offsetLeft = getOffsetLeft();
        //计算当前Track的总刻度数
        int count = (mWidth - offsetLeft) / mGraduationSpace + 1;
        Log.d(TAG, "count: " + count);

        for (int i = 0, x, y; i < count; i++) {
            x = offsetLeft + i * mGraduationSpace;
            if (isLargeGraduation(i, offsetLeft)) {
                //大刻度
                mPaint.setColor(mGraduationLColor);
                y = offsetTop + 40;
            } else {
                //小刻度
                mPaint.setColor(mGraduationSColor);
                y = offsetTop + 30;
            }
            canvas.drawLine(x, offsetTop, x, y, mPaint);
        }
    }

    private void drawTrack(Canvas canvas) {
        int cy = canvas.getHeight() / 2 + offsetTop;
        int size = mTrackData == null ? 0 : mTrackData.size();
        for (int i = 0; i < size; i++) {
            // TODO: 2018/1/29
        }
    }

    /**
     * 是否是大刻度
     *
     * @param i
     * @param offsetLeft
     * @return
     */
    private boolean isLargeGraduation(int i, int offsetLeft) {
        //第一个Track的索引为-1；第二个Track的索引从0依次递增
        int index = mCurrentPosition - 1;
        //到当前的刻度数量
        int graduationCount = mWidth * index / mGraduationSpace + i;
        //处理拼接刻度导致的索引偏移
        graduationCount += (offsetLeft > 0 ? index < 0 ? 0 : 1 : 0);
        return graduationCount % UNIT_GRADUATION_INTERVAL == 0;
    }

    /**
     * 获取左侧偏移值
     *
     * @return
     */
    private int getOffsetLeft() {
        //第一个，offset值为取余
        if (mCurrentPosition == 0) {
            return mWidth % mGraduationSpace;
        }
        //前一个Track剩余的偏移值
        int prevOL = (mCurrentPosition - 1) * mWidth % mGraduationSpace;
        if (prevOL > 0) {
            //计算当前Track的偏移值
            return mGraduationSpace - prevOL;
        }
        return 0;
    }

    /**
     * 通知Adapter位置发生变化，绘制视图
     *
     * @param position
     * @param trackData
     */
    public void notifyPositionChanged(int position, List<Integer> trackData) {
        if (position <= DEFAULT_POSITION) {
            Log.e(TAG, "parameter position must greater than " + DEFAULT_POSITION);
            return;
        }
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            mTrackData = trackData;
            postInvalidate();
        }
    }

    /**
     * 通知Track数据发生变化，绘制视图
     *
     * @param trackData
     */
    public void notifyDataChanged(List<Integer> trackData) {
        mTrackData = trackData;
        Log.d(TAG, "notifyDataChanged() called with: trackData = [" + trackData + "]");
        postInvalidate();
    }
}