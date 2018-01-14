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
    //默认索引
    private static final int DEFAULT_POSITION = -1;
    //每小格的距离
    private static final int UNIT_SCALE = 50;
    //每4小格是一大格
    private static final int UNIT_SCALE_INTERVAL = 4;
    private static final int UNIT_TRACK = 5;
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
        mGraduationSColor = getResources().getColor(R.color.TrackView_default_graduation_s);
        mGraduationLColor = getResources().getColor(R.color.TrackView_default_graduation_l);
    }

    /**
     * 重写绘制
     *
     * @param position
     */
    public void reDraw(int position) {
        if (position <= DEFAULT_POSITION) {
            Log.e(TAG, "parameter position must greater than " + DEFAULT_POSITION);
            return;
        }
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            postInvalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canDraw()) {
            drawBackground(canvas);
            drawGraduated(canvas);
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
    private void drawGraduated(Canvas canvas) {
        int offsetLeft = getOffsetLeft();
        //计算当前Track的总刻度数
        int count = (mWidth - offsetLeft) / UNIT_SCALE + 1;
        Log.d(TAG, "count: " + count);

        for (int i = 0, x, y; i < count; i++) {
            x = offsetLeft + i * UNIT_SCALE;
            if (isLargeScale(i, offsetLeft)) {
                //大刻度
                mPaint.setColor(mGraduationLColor);
                y = offsetTop + 60;
            } else {
                //小刻度
                mPaint.setColor(mGraduationSColor);
                y = offsetTop + 40;
            }
            canvas.drawLine(x, offsetTop, x, y, mPaint);
        }
    }

    /**
     * 是否是大刻度
     *
     * @param i
     * @param offsetLeft
     * @return
     */
    private boolean isLargeScale(int i, int offsetLeft) {
        //第一个Track的索引为-1；第二个Track的索引从0依次递增
        int index = mCurrentPosition - 1;
        //到当前的刻度数量
        int scaleCount = mWidth * index / UNIT_SCALE + i;
        //处理拼接刻度导致的索引偏移
        scaleCount += (offsetLeft > 0 ? index < 0 ? 0 : 1 : 0);
        return scaleCount % UNIT_SCALE_INTERVAL == 0;
    }

    /**
     * 获取左侧偏移值
     *
     * @return
     */
    private int getOffsetLeft() {
        //第一个，offset值为取余
        if (mCurrentPosition == 0) {
            return mWidth % UNIT_SCALE;
        }
        //前一个Track剩余的偏移值
        int prevOL = (mCurrentPosition - 1) * mWidth % UNIT_SCALE;
        if (prevOL > 0) {
            //计算当前Track的偏移值
            return UNIT_SCALE - prevOL;
        }
        return 0;
    }
}