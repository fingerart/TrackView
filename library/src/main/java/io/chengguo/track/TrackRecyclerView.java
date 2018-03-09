package io.chengguo.track;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackRecyclerView extends RecyclerView {
    private static final String TAG = TrackRecyclerView.class.getSimpleName();
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
        adapter = new TrackRecyclerAdapter(mParent);
        setLayoutManager(createLayoutManager(context));
        setAdapter(adapter);
    }

    private LinearLayoutManager createLayoutManager(Context context) {
        return new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {

            /**
             * 限制滑动边界
             *
             * @param dx
             * @param recycler
             * @param state
             * @return
             */
            @Override
            public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
                int scrollOffset = computeHorizontalScrollOffset(state);
                int maxScrollOffset = adapter.getGrades() * trackSpace;
                Utils.l(TAG, "scrollOffset: %s, dx: %s, maxScrollOffset: %s", scrollOffset, dx, maxScrollOffset);
                int d = maxScrollOffset - (scrollOffset + dx);
                if (d < 0) {
                    dx = maxScrollOffset - scrollOffset;
                }
                return super.scrollHorizontallyBy(dx, recycler, state);
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Utils.l(TAG, "onSizeChanged() called with: w = [%s], h = [%s]", w, h);
        int count = (getWidth() >> 1) / trackSpace;
        adapter.setItemCount(count);
    }

    private void invalidateTrace() {
        ViewHolder vh = findViewHolderForLayoutPosition(((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition());
        if (vh != null && vh instanceof TrackHolder) {
            scrollBy(trackSpace, 0);
            Track track = ((TrackHolder) vh).getTrackView();
            if (track != null) {
                List<Integer> data = adapter.getTrackDataForPosition(vh.getAdapterPosition(), track.needAddOne());
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