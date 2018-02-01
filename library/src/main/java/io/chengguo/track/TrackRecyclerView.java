package io.chengguo.track;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.List;

import io.chengguo.track.library.R;

import static io.chengguo.track.Utils.l;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackRecyclerView extends RecyclerView {
    private static final String TAG = TrackRecyclerView.class.getSimpleName();
    private LinearLayoutManager layoutManager;
    private TrackRecyclerAdapter adapter;
    //Track间距
    private int trackSpace;
    private TrackView mParent;

    private TrackRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    private TrackRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private TrackRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TrackRecyclerView(Context context, TrackView trackView) {
        this(context);
        mParent = trackView;
        initAttrs();
        initView(context);
    }

    private void initAttrs() {
        trackSpace = getResources().getDimensionPixelOffset(R.dimen.TrackView_default_track_space);
    }

    private void initView(Context context) {
        layoutManager = new TrackHorizontalLayoutManager(this);
        adapter = new TrackRecyclerAdapter(mParent);
        setLayoutManager(layoutManager);
        setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        l(TAG, "onSizeChanged() called with: w = [%s], h = [%s]", w, h);
        int count = (getWidth() >> 1) / trackSpace;
        adapter.setItemCount(count);
    }

    private void invalidateTrace() {
        ViewHolder vh = findViewHolderForLayoutPosition(layoutManager.findLastCompletelyVisibleItemPosition());
        if (vh != null && vh instanceof TrackHolder) {
            scrollBy(trackSpace, 0);
            Track track = ((TrackHolder) vh).getTrackView();
            if (track != null) {
                int position = vh.getAdapterPosition();
                List<Integer> data = adapter.getTrackDataForPosition(position);
                track.notifyDataChanged(data);
            }
        }
    }

    /**
     * 添加新的Track值
     *
     * @param grade
     */
    void howl(int grade) {
        adapter.addGradeAndNotify(grade);
        invalidateTrace();
    }

    void howl(List<Integer> tracks) {
        adapter.setGradeAndNotify(tracks);
    }

    void clear() {
        adapter.setGradeAndNotify(null);
        invalidateTrace();
    }
}