package io.chengguo.libs.track;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static io.chengguo.libs.track.Utils.createMPLayoutParams;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackRecyclerAdapter extends RecyclerView.Adapter<TrackHolder> {
    private static final String TAG = TrackRecyclerAdapter.class.getSimpleName();
    private final TrackView root;
    private List<Integer> datas;
    private int mItemCount;

    public TrackRecyclerAdapter(TrackView trackView) {
        root = trackView;
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Utils.l(TAG, "onCreateViewHolder() called with: parent = viewType = [%s]", viewType);
        LinearLayout container = new LinearLayout(parent.getContext());
        container.setLayoutParams(new LinearLayout.LayoutParams(parent.getMeasuredWidth() >> 1, LinearLayout.LayoutParams.MATCH_PARENT));
        container.addView(new Track(parent.getContext(), root), createMPLayoutParams());
        return new TrackHolder(container);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        Utils.l(TAG, "onBindViewHolder() called with: position = [%s]", position);
        Track trackView = holder.getTrackView();
        if (trackView != null) {
            trackView.notifyPositionChanged(position, getTrackDataForPosition(position, trackView.needAddOne()));
        }
    }

    /**
     * 获取指定位置的Track数据
     *
     * @param position
     * @param needAddOne
     * @return
     */
    @Nullable
    List<Integer> getTrackDataForPosition(int position, boolean needAddOne) {
        if (datas == null) {
            return null;
        }
        int size = datas.size();
        int fromIndex = (position - 1) * mItemCount;
        int toIndex = position * mItemCount + (needAddOne ? 1 : 0);
        //过滤可能会引发异常的Index
        if (fromIndex < 0 || fromIndex >= size || toIndex <= 0) {
            return null;
        }
        //计算真实的toIndex
        int d = toIndex - size;
        if (d > 0) {
            toIndex -= d;
        }
        return new ArrayList<>(datas.subList(fromIndex, toIndex));
    }

    @Override
    public int getItemCount() {
        return 2 + (datas == null || datas.isEmpty() || mItemCount == 0 ? 0 : ((int) Math.ceil(((double) datas.size()) / ((double) mItemCount))));
    }

    /**
     * 设置Item可绘制Track的个数
     *
     * @param itemCount
     */
    void setItemCount(int itemCount) {
        Utils.l(TAG, "setItemCount() called with: itemCount = [" + itemCount + "]");
        mItemCount = itemCount;
    }

    /**
     * 获取点的数量
     *
     * @return
     */
    int getGrades() {
        return datas == null ? 0 : datas.size();
    }

    /**
     * 添加Grade
     *
     * @param grade
     */
    void addGradeAndNotify(int grade) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.add(grade);
        notifyDataSetChanged();
    }

    void setGradeAndNotify(List<Integer> grades) {
        datas = grades;
        notifyDataSetChanged();
    }
}