package io.chengguo.track;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static io.chengguo.track.Utils.createMPLayoutParams;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {
    private static final String TAG = TrackAdapter.class.getSimpleName();
    private List<Integer> datas = new ArrayList<>();
    private int mItemCount;

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        LinearLayout container = new LinearLayout(parent.getContext());
        container.setLayoutParams(new LinearLayout.LayoutParams(parent.getMeasuredWidth() >> 1, LinearLayout.LayoutParams.MATCH_PARENT));
        ViewGroup.LayoutParams mpLayoutParams = createMPLayoutParams();
        container.addView(new Track(parent.getContext()), mpLayoutParams);
        return new TrackHolder(container);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: position = [" + position + "]");
        Track trackView = holder.getTrackView();
        if (trackView != null) {
            trackView.notifyPositionChanged(position, getTrackDataForPosition(position));
        }
    }

    /**
     * 获取指定位置的Track数据
     *
     * @param position
     * @return
     */
    @Nullable
    List<Integer> getTrackDataForPosition(int position) {
        int size = datas.size();
        int fromIndex = (position - 1) * mItemCount;
        int toIndex = position * mItemCount;
        //过滤
        if (fromIndex < 0 || fromIndex >= size || toIndex <= 0) {
            return null;
        }
        //计算真实的toIndex
        int d = toIndex - size;
        if (d > 0) {
            toIndex -= d;
        }
        return datas.subList(fromIndex, toIndex);
    }

    @Override
    public int getItemCount() {
        return 2 + (datas == null || datas.isEmpty() || mItemCount == 0 ? 0 : ((int) Math.ceil(((double) datas.size()) / ((double) mItemCount))));
    }

    /**
     * 设置Item可绘制的个数
     *
     * @param itemCount
     */
    void setItemCount(int itemCount) {
        mItemCount = itemCount;
    }

    /**
     * 添加Grade
     *
     * @param grade
     */
    void addGrade(int grade) {
        datas.add(grade);
        notifyDataSetChanged();
    }
}