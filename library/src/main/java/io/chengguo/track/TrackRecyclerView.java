package io.chengguo.track;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackRecyclerView extends RecyclerView {
    private TrackAdapter mAdapter;

    public TrackRecyclerView(Context context) {
        this(context, null);
    }

    public TrackRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new TrackAdapter();
        setAdapter(mAdapter);
    }

    public void howl(int grade) {
        if (getAdapter() != null && getAdapter() instanceof TrackAdapter) {
            ((TrackAdapter) getAdapter()).addGrade(grade);
        }
    }
}