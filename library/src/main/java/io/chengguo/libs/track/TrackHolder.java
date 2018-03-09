package io.chengguo.libs.track;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by FingerArt on 2018/1/12.
 */
class TrackHolder extends RecyclerView.ViewHolder {
    public TrackHolder(View itemView) {
        super(itemView);
    }

    /**
     * 获取TrackView
     *
     * @return
     */
    public Track getTrackView() {
        if (itemView != null) {
            View track = itemView.findViewById(R.id.track_view);
            if (track != null && track instanceof Track) {
                return (Track) track;
            }
        }
        return null;
    }
}
