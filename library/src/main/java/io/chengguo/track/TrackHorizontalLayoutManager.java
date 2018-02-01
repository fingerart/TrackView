package io.chengguo.track;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.List;

import io.chengguo.track.library.R;

/**
 * Created by FingerArt on 2018/2/1.
 */
public class TrackHorizontalLayoutManager extends LinearLayoutManager {
    private TrackRecyclerView mRecyclerView;
    private int space;

    public TrackHorizontalLayoutManager(TrackRecyclerView recyclerView) {
        this(recyclerView.getContext());
        mRecyclerView = recyclerView;
        space = recyclerView.getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_space);
    }

    private TrackHorizontalLayoutManager(Context context) {
        this(context, LinearLayoutManager.HORIZONTAL, false);
    }

    private TrackHorizontalLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    private TrackHorizontalLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null) {
            TrackRecyclerAdapter adapter = (TrackRecyclerAdapter) mRecyclerView.getAdapter();
            List<Integer> data = adapter.getTrackDataForPosition(getItemCount() - 2);
            int d = (data == null ? 0 : data.size()) * space;
            int ddx = getWidth() / 2 - d;
            int ds = mRecyclerView.computeHorizontalScrollRange() - mRecyclerView.computeHorizontalScrollOffset();
            if (ds <= ddx) {
                dx = 0;
            }

            

        }
        return super.scrollHorizontallyBy(dx, recycler, state);
    }
}