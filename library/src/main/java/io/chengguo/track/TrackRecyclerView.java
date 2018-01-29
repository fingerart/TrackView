package io.chengguo.track;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.chengguo.track.library.R;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackRecyclerView extends RecyclerView {
    private static final String TAG = TrackRecyclerView.class.getSimpleName();
    private LinearLayoutManager layoutManager;
    private TrackAdapter adapter;

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
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapter = new TrackAdapter();
        setLayoutManager(layoutManager);
        setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "], oldw = [" + oldw + "], oldh = [" + oldh + "]");
        int graduationSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_graduation_space);
        // TODO: 2018/1/29 count 计算错了
        int count = getWidth() / graduationSpace;
        adapter.setItemCount(count);
    }

    public void howl(int grade) {
        adapter.addGrade(grade);
        ViewHolder vh = findViewHolderForLayoutPosition(layoutManager.findLastCompletelyVisibleItemPosition());
        if (vh != null && vh instanceof TrackHolder) {
            Track track = ((TrackHolder) vh).getTrackView();
            if (track != null) {
                track.notifyDataChanged(adapter.getTrackDataForPosition(vh.getPosition()));
            }
        }
    }
}