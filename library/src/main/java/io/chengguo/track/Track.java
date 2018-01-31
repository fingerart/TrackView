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

import static io.chengguo.track.Utils.l;
import static io.chengguo.track.Utils.le;

/**
 * 音轨item
 * Created by FingerArt on 2018/1/12.
 */
class Track extends View {
    private static final String TAG = Track.class.getSimpleName();
    //默认索引
    private static final int DEFAULT_POSITION = -1;
    //每小格刻度的间距
    private int mGraduationSpace;
    //N个小刻度组成一个大刻度间距
    private int mGraduationLargeSpaceByGraduationCount;
    //Track间距
    private int mTrackSpace;
    //刻度画笔
    private Paint mGraduationPaint;
    //Track画笔
    private Paint mTrackPaint;
    //在RecyclerView中的索引
    private int mCurrentPosition = DEFAULT_POSITION;
    //刻度数据
    private List<Integer> mTrackData;
    private TrackView mParent;
    private int mGraduationLargeLength;
    private int mGraduationLength;

    private Track(Context context) {
        this(context, (AttributeSet) null);
    }

    private Track(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private Track(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Track(Context context, TrackView trackView) {
        this(context);
        mParent = trackView;
        initAttrs();
        initPaint();
    }

    private void initAttrs() {
        setId(R.id.track_view);
        mGraduationSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_space) * getResources().getInteger(R.integer.TrackView_default_graduation_space_by_track_count);
        mGraduationLargeSpaceByGraduationCount = getResources().getInteger(R.integer.TrackView_default_graduation_large_space_by_graduation_count);
        mGraduationLength = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_graduation_length);
        mGraduationLargeLength = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_graduation_large_length);
        mTrackSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_space);
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
        mTrackPaint.setColor(mParent.getTrackColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        l(TAG, "onDraw() called with: canvas = [%s]", canvas);
        if (canDraw()) {
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
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawGraduation(Canvas canvas) {
        int width = canvas.getWidth();
        int offsetLeft = getGraduationOffsetLeft(width);
        //计算当前Track的总刻度数
        int graduationCount = (width - offsetLeft) / mGraduationSpace + 1;
        l(TAG, "drawGraduation() called with : graduationCount = [%s]", graduationCount);

        for (int i = 0, x, y; i < graduationCount; i++) {
            x = offsetLeft + i * mGraduationSpace;
            if (isLargeGraduation(width, i, offsetLeft)) {
                //大刻度
                mGraduationPaint.setColor(mParent.getGraduationLargeColor());
                y = (int) (mParent.getTrackTimeHeight()) + mGraduationLargeLength;
            } else {
                //小刻度
                mGraduationPaint.setColor(mParent.getGraduationSmallColor());
                y = (int) (mParent.getTrackTimeHeight()) + mGraduationLength;
            }
            canvas.drawLine(x, (int) (mParent.getTrackTimeHeight()), x, y, mGraduationPaint);
        }
    }

    /**
     * 绘制Track
     *
     * @param canvas
     */
    private void drawTrack(Canvas canvas) {
        int width = canvas.getWidth();
        int trackCount = mTrackData == null ? 0 : mTrackData.size();
        l(TAG, "drawTrack() called with: trackCount = [%s]", trackCount);
        if (trackCount <= 0)
            return;
        int offsetLeft = getTrackOffsetLeft(width);
        int cy = canvas.getHeight() / 2 + (int) (mParent.getTrackTimeHeight());
        for (int i = 0; i < trackCount; i++) {
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
        return graduationCount % mGraduationLargeSpaceByGraduationCount == 0;
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
            le(TAG, "Parameter position must greater than %s", DEFAULT_POSITION);
            return;
        }
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            mTrackData = trackData;
            invalidate();
        }
    }

    /**
     * 通知Track数据发生变化，绘制视图
     *
     * @param trackData
     */
    public void notifyDataChanged(List<Integer> trackData) {
        l(TAG, "notifyDataChanged() called with: trackData = [%s]", trackData);
        mTrackData = trackData;
        invalidate();
    }
}