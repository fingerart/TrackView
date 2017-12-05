package io.chengguo.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import io.chengguo.track.library.TrackView;

public class MainActivity extends AppCompatActivity implements TrackView.SlideGraduationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected TrackView track;
    protected TextView time;
    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj != null && msg.obj instanceof TrackView) {
                ((TrackView) msg.obj).go(((int) (Math.random() * 100)));
            }
        }
    };

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        track = findViewById(R.id.track);
        time = findViewById(R.id.time);
        track.setOnSlideGraduationListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onGraduationChanged(int currentGraduation) {
        Log.d(TAG, "onGraduationChanged() called with: currentGraduation = [" + currentGraduation + "]");
        int min = currentGraduation / 6000;
        int sec = (currentGraduation - min * 6000) / 100;
        time.setText(String.format("%02d:%02d.%02d", min, sec, currentGraduation % 100));
    }

    public void dw(View view) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "TimerTask#run() called");
                handler.obtainMessage(0, track).sendToTarget();
            }
        }, 0, 50);
        track.lock(true);
    }

    public void td(View view) {
        track.lock(false);
        timer.cancel();
        timer.purge();
    }
}