package io.chengguo.track;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static io.chengguo.track.Utils.createMPLayoutParams;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {
    private List<List<Integer>> datas;

    TrackAdapter() {
        datas = new ArrayList<>();
        datas.add(null);
        datas.add(null);
        datas.add(null);
        datas.add(null);
        datas.add(null);
        datas.add(null);
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout container = new LinearLayout(parent.getContext());
        container.setLayoutParams(new LinearLayout.LayoutParams(parent.getMeasuredWidth() >> 1, LinearLayout.LayoutParams.MATCH_PARENT));
        ViewGroup.LayoutParams mpLayoutParams = createMPLayoutParams();
        container.addView(new Track(parent.getContext()), mpLayoutParams);
        return new TrackHolder(container);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        Track trackView = holder.getTrackView();
        if (trackView != null) {
            trackView.notifyPositionChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}