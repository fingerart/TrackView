package io.chengguo.track.library;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by FingerArt on 2017/12/5.
 */
public class SimpleTrackView extends TrackView {
    private TrackAdapter trackAdapter;
    private Timer timer;

    public SimpleTrackView(Context context) {
        this(context, null);
    }

    public SimpleTrackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTrackAdapter(TrackAdapter adapter) {
        this.trackAdapter = adapter;
    }

    public void start() {
        lock(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(createTask(), 0, 50);
    }

    public void stop() {
        lock(false);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void clear() {
        stop();
        track.clear();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private TimerTask createTask() {
        return new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        int decibel = 0;
                        if (trackAdapter != null) {
                            decibel = trackAdapter.getAmplitude();
                        }
                        go(decibel);
                    }
                });
            }
        };
    }
}
