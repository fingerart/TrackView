package io.chengguo.track.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static io.chengguo.track.library.Util.log;

/**
 * 音轨View
 */
class InnerTrackView extends View {

    private static final String TAG = InnerTrackView.class.getSimpleName();
    private static final int UNIT = 50;
    static final int UNIT_TRACK = 5;
    private Paint mPaint;
    private int offsetLeft;
    private int offsetTop = 100;
    private int offsetBigGraduation;
    private int viewportW;
    private int viewportH;
    private ArrayList<Integer> decibels = new ArrayList<>();
    private TrackViewAttrs trackAttrs;

    public InnerTrackView(Context context, TrackViewAttrs trackAttrs) {
        super(context);
        this.trackAttrs = trackAttrs;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void addDecibel(int decibel) {
        decibels.add(decibel);
    }

    public void setDecibel(List<Integer> decibels) {
        clear();
        decibels.addAll(decibels);
    }

    public void clear() {
        decibels.clear();
        getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        requestLayout();
    }

    public void notifyDecibelChange() {
        getLayoutParams().width = getMeasuredWidth() + UNIT_TRACK;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                log(TAG, "widthMeasureSpec: EXACTLY [" + sizeW + "]");
                break;
            case MeasureSpec.AT_MOST:
                log(TAG, "widthMeasureSpec: AT_MOST [" + sizeW + "]");
                break;
            case MeasureSpec.UNSPECIFIED:
                log(TAG, "widthMeasureSpec: UNSPECIFIED [" + sizeW + "]");
                break;
        }

        setMeasuredDimension(sizeW, MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        log(TAG, "onDraw() called with: canvas = [" + canvas + "]");
        drawBackground(canvas);
        drawGraduated(canvas);
        drawTrack(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#F9F9F9"));
        canvas.drawRect(0, offsetTop, getRight(), getBottom(), mPaint);
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawGraduated(Canvas canvas) {
        int measuredWidth = getMeasuredWidth();
        if (offsetLeft == 0) {
            viewportW = measuredWidth;
            viewportH = getMeasuredHeight();
            offsetLeft = (measuredWidth >> 1) % (UNIT << 2);//计算左边开始的大刻度偏移值
            offsetBigGraduation = ((measuredWidth >> 1) - offsetLeft) / (UNIT << 2);
        }
        int count = (measuredWidth) / UNIT;//能显示的刻度总个数
        log(TAG, "count: " + count);

        int bigCount = ((count >> 2) + 1);
        float[] bigPoints = new float[bigCount << 2];
        log(TAG, "bigCount: " + bigCount);

        int smallCount = count - bigCount + 1;
        log(TAG, "smallCount: " + smallCount);
        float[] smallPoints = new float[smallCount << 2];
        for (int i = 0, x, y; i < count; i++) {
            x = i * UNIT + offsetLeft;
            if (i % 4 == 0) {
                y = 40;
                int bi = i >> 2;
                log(TAG, i + " >> 2: " + bi);
                bigPoints[bi * 4] = x;
                bigPoints[bi * 4 + 1] = offsetTop;
                bigPoints[bi * 4 + 2] = x;
                bigPoints[bi * 4 + 3] = y + offsetTop;
                if (bi >= offsetBigGraduation) {//跳过大的偏移刻度
                    drawTime(canvas, bi - offsetBigGraduation, x, offsetTop - 15);//draw time text
                }
            } else {
                y = 20;
                int si = i - (i >> 2) - 1;
                smallPoints[si * 4] = x;
                smallPoints[si * 4 + 1] = offsetTop;
                smallPoints[si * 4 + 2] = x;
                smallPoints[si * 4 + 3] = y + offsetTop;
            }
        }

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(trackAttrs.graduationSColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawLines(smallPoints, mPaint);//小刻度
        mPaint.setColor(trackAttrs.graduationLColor);
        canvas.drawLines(bigPoints, mPaint);//大刻度
    }

    /**
     * 绘制时间文本
     *
     * @param canvas
     * @param index
     * @param x
     * @param y
     */
    @SuppressLint("DefaultLocale")
    private void drawTime(Canvas canvas, int index, int x, int y) {
        mPaint.setTextSize(trackAttrs.graduationTextSize);
        mPaint.setStrokeWidth(3);

        String text = String.format("%02d:%02d", (index << 1) / 60, (index << 1) % 60);
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        log(TAG, "rect: " + rect.toShortString());
        mPaint.setColor(trackAttrs.graduationTextColor);
        int center = x - rect.centerX();
        log(TAG, "center: " + center);
        canvas.drawText(text, center, y, mPaint);
    }

    /**
     * 绘制音轨线
     *
     * @param canvas
     */
    private void drawTrack(Canvas canvas) {
        int zeroPoint = viewportW >> 1;
        log(TAG, "viewportH: " + viewportH);
        int centerY = ((viewportH - offsetTop) >> 1) + offsetTop;
        mPaint.setColor(trackAttrs.trackColor);
        mPaint.setStrokeWidth(2.5f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        for (int i = 0; i < decibels.size(); i++) {
            int x = zeroPoint + i * UNIT_TRACK;
            int db = decibels.get(i) < UNIT_TRACK ? UNIT_TRACK : decibels.get(i);
            canvas.drawLine(x, centerY + db, x, centerY - db, mPaint);
        }
    }

    static class TrackViewAttrs {
        int trackColor;
        int graduationLColor;
        int graduationSColor;
        int graduationTextColor;
        float graduationTextSize;

        TrackViewAttrs(int trackColor, int graduationLColor, int graduationSColor, int graduationTextColor, float graduationTextSize) {
            this.trackColor = trackColor;
            this.graduationLColor = graduationLColor;
            this.graduationSColor = graduationSColor;
            this.graduationTextColor = graduationTextColor;
            this.graduationTextSize = graduationTextSize;
        }
    }
}