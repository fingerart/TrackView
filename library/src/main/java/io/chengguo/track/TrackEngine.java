package io.chengguo.track;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 动力引擎
 * Created by FingerArt on 2018/1/31.
 */
class TrackEngine {
    private final TrackView mOwner;
    private final int period;
    private Timer mTimer;

    public TrackEngine(TrackView trackView) {
        mOwner = trackView;
        period = 1000 / (trackView.getResources().getInteger(R.integer.TrackView_default_graduation_space_by_track_count) << 1);
    }

    public synchronized void start() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    mOwner.addTrack();
                }
            }, 0, period);
        }
    }

    public boolean isRunning() {
        return mTimer != null;
    }

    public synchronized void stop() {
        if (isRunning()) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
