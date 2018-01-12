package io.chengguo.track;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import io.chengguo.track.library.R;

/**
 * Created by FingerArt on 2018/1/12.
 */
class MidLine extends FrameLayout {
    public MidLine(@NonNull Context context) {
        this(context, null);
    }

    public MidLine(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MidLine(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.color.TrackView_default_graduation_s);
    }
}
