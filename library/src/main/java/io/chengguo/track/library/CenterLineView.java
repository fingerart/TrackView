package io.chengguo.track.library;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 中线View
 */
class CenterLineView extends View {
    private int indexColor;

    public CenterLineView(Context context) {
        this(context, null);
    }

    public CenterLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CenterLineView(Context context, int indexColor) {
        this(context);
        this.indexColor = indexColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(indexColor);
    }
}