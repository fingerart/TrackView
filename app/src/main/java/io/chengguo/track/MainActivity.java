package io.chengguo.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.chengguo.track.library.TrackView;

public class MainActivity extends AppCompatActivity implements TrackView.SlideGraduationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected TrackView track;
    protected TextView time;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        private int t;

        @Override
        public void handleMessage(Message msg) {
            removeMessages(0);
            if (t % 5 == 0) {
                track.go(((int) (Math.random() * 100)));
            } else {
                track.go();
            }
            t++;
            sendEmptyMessageDelayed(0, 10);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        track = (TrackView) findViewById(R.id.track);
        time = (TextView) findViewById(R.id.time);
        track.setOnSlideGraduationListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onGraduationChanged(int currentGraduation) {//毫秒
        Log.d(TAG, "onGraduationChanged() called with: currentGraduation = [" + currentGraduation + "]");
        int min = currentGraduation / 6000;
        int sec = (currentGraduation - min * 6000) / 100;
        time.setText(String.format("%02d:%02d.%02d", min, sec, currentGraduation % 100));
    }

    public void dw(View view) {
        handler.sendEmptyMessage(0);
        track.lock(true);
    }

    public void td(View view) {
        track.lock(false);
        handler.removeMessages(0);
    }
}