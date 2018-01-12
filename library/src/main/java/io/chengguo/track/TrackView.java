package io.chengguo.track;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import io.chengguo.track.library.R;

/**
 * 音频轨迹
 * Created by FingerArt on 2018/1/12.
 */
public class TrackView extends FrameLayout {

    private TrackRecyclerView trackRecyclerView;

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
    }

    private void initView(Context context) {
        addTrackRecycler(context);
        addMidLine(context);
    }

    private void addTrackRecycler(Context context) {
        trackRecyclerView = new TrackRecyclerView(context);
        addView(trackRecyclerView, generateDefaultLayoutParams());
    }

    private void addMidLine(Context context) {
        LayoutParams params = generateDefaultLayoutParams();
        params.width = 3;
        params.height = LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(new MidLine(context), params);
    }

    public void start() {
        trackRecyclerView.getAdapter().notifyDataSetChanged();
    }
}