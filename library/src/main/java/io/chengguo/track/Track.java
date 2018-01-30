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
    //每小格刻度的间距
    private int mGraduationSpace;
    //Track间距
    private int mTrackSpace;
    //刻度画笔
    private Paint mGraduationPaint;
    //Track画笔
    private Paint mTrackPaint;
    private int mBackground;
    //小刻度颜色
    private int mGraduationSColor;
    //大刻度颜色
    private int mGraduationLColor;
    //在RecyclerView中的索引
    private int mCurrentPosition = DEFAULT_POSITION;
    //TrackTime高度
    private int mOffsetTop;
    //刻度数据
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
        initPaint();
    }

    private void initAttrs() {
        setId(R.id.track_view);
        mBackground = getResources().getColor(R.color.TrackView_default_bg);
        mGraduationSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_space) * R.integer.TrackView_default_graduation_space_by_track_count;
        mGraduationSColor = getResources().getColor(R.color.TrackView_default_graduation_s);
        mGraduationLColor = getResources().getColor(R.color.TrackView_default_graduation_l);
        mTrackSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_space);
        mOffsetTop = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_time_height);
    }

    private void initPaint() {
        //刻度Paint
        mGraduationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraduationPaint.setStrokeCap(Paint.Cap.ROUND);
        mGraduationPaint.setStyle(Paint.Style.FILL);
        //TrackPaint
        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setStrokeCap(Paint.Cap.ROUND);
        mTrackPaint.setStyle(Paint.Style.FILL);
        mTrackPaint.setColor(getResources().getColor(R.color.TrackView_default_track));
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
        mGraduationPaint.setColor(mBackground);
        canvas.drawRect(0, mOffsetTop, getRight(), getBottom(), mGraduationPaint);
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawGraduation(Canvas canvas) {
        int width = canvas.getWidth();
        int offsetLeft = getGraduationOffsetLeft(width);
        //计算当前Track的总刻度数
        int count = (width - offsetLeft) / mGraduationSpace + 1;
        Log.d(TAG, "count: " + count);

        for (int i = 0, x, y; i < count; i++) {
            x = offsetLeft + i * mGraduationSpace;
            if (isLargeGraduation(width, i, offsetLeft)) {
                //大刻度
                mGraduationPaint.setColor(mGraduationLColor);
                y = mOffsetTop + 40;
            } else {
                //小刻度
                mGraduationPaint.setColor(mGraduationSColor);
                y = mOffsetTop + 30;
            }
            canvas.drawLine(x, mOffsetTop, x, y, mGraduationPaint);
        }
    }

    /**
     * 绘制Track
     *
     * @param canvas
     */
    private void drawTrack(Canvas canvas) {
        int width = canvas.getWidth();
        int size = mTrackData == null ? 0 : mTrackData.size();
        if (size <= 0)
            return;
        int offsetLeft = getTrackOffsetLeft(width);
        int cy = canvas.getHeight() / 2 + mOffsetTop;
        for (int i = 0; i < size; i++) {
            int x = offsetLeft + mTrackSpace * i;
            canvas.drawLine(x, cy - mTrackData.get(i), x, cy + mTrackData.get(i), mTrackPaint);
        }
    }

    /**
     * 获取Track的偏移量
     *
     * @param width
     * @return
     */
    private int getTrackOffsetLeft(int width) {
        int preOL = (mCurrentPosition - 1) * width % mTrackSpace;
        if (preOL > 0) {
            return mTrackSpace - preOL;
        }
        return 0;
    }

    /**
     * 是否是大刻度
     *
     * @param width
     * @param i
     * @param offsetLeft
     * @return
     */
    private boolean isLargeGraduation(int width, int i, int offsetLeft) {
        //第一个Track的索引为-1；第二个Track的索引从0依次递增
        int index = mCurrentPosition - 1;
        //到当前的刻度数量
        int graduationCount = width * index / mGraduationSpace + i;
        //处理拼接刻度导致的索引偏移
        graduationCount += (offsetLeft > 0 ? index < 0 ? 0 : 1 : 0);
        return graduationCount % R.integer.TrackView_default_graduation_large_space_by_graduation_count == 0;
    }

    /**
     * 获取刻度的左侧偏移值
     *
     * @param width
     * @return
     */
    private int getGraduationOffsetLeft(int width) {
        //第一个，offset值为取余
        if (mCurrentPosition == 0) {
            return width % mGraduationSpace;
        }
        //前一个Track剩余的偏移值
        int prevOL = (mCurrentPosition - 1) * width % mGraduationSpace;
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