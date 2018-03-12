package io.chengguo.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.chengguo.libs.track.ITimeChangeListener;
import io.chengguo.libs.track.ITrackAdapter;
import io.chengguo.libs.track.TrackView;

public class MainActivity extends AppCompatActivity implements ITrackAdapter, ITimeChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected TrackView track;
    protected TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = findViewById(R.id.time);
        track = findViewById(R.id.track);
        track.setTrackAdapter(new ITrackAdapter() {
            @Override
            public int getAmplitude() {
                return 0;
            }
        });//ITrackAdapter
        track.setGraduationListener(new ITimeChangeListener() {
            @Override
            public void onTimeChanged(int millisecond) {

            }
        });//ITimeChangeListener
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        track.stop();
    }

    public void onClickStart(View view) {
        track.start();
    }

    public void onClickStop(View view) {
        track.stop();
    }

    @Override
    public int getAmplitude() {
        return (int) (Math.random() * 100) + 5;
    }

    @SuppressLint("DefaultLocale")
    public void onTimeChanged(int millisecond) {
        Log.d(TAG, "onTimeChanged() called with: currentGraduation = [" + millisecond + "]");
        int min = millisecond / 6000;
        int sec = (millisecond - min * 6000) / 100;
        time.setText(String.format("%02d:%02d.%02d", min, sec, millisecond % 100));
    }
}