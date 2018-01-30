package io.chengguo.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import io.chengguo.track.library.SlideGraduationListener;
import io.chengguo.track.library.TrackAdapter;

public class MainActivity extends AppCompatActivity implements SlideGraduationListener, TrackAdapter {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected TrackView track;
    protected TextView time;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        track = findViewById(R.id.track);
        time = findViewById(R.id.time);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onGraduationChanged(int currentGraduation) {
        Log.d(TAG, "onGraduationChanged() called with: currentGraduation = [" + currentGraduation + "]");
        int min = currentGraduation / 6000;
        int sec = (currentGraduation - min * 6000) / 100;
        time.setText(String.format("%02d:%02d.%02d", min, sec, currentGraduation % 100));
    }

    public void onStart(View view) {
        for (int i = 0; i < 10; i++) {
            track.addTrack((int) (Math.random() * 100));
        }
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//            }
//        }, 0, 20);
    }

    public void onStop(View view) {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public int getAmplitude() {
        return ((int) (Math.random() * 100));
    }
}