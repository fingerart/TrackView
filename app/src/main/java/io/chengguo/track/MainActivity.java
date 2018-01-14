package io.chengguo.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.chengguo.track.library.SlideGraduationListener;
import io.chengguo.track.library.TrackAdapter;

public class MainActivity extends AppCompatActivity implements SlideGraduationListener, TrackAdapter {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected TrackView track;
    protected TextView time;

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

    public void onStop(View view) {
    }

    @Override
    public int getAmplitude() {
        return ((int) (Math.random() * 100));
    }
}