package io.chengguo.track.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * TrackView
 *
 * @author FingerArt http://fingerart.me
 * @date 2017年08月25日 10:10
 */
public class TrackView extends FrameLayout {
    private static final String TAG = TrackView.class.getSimpleName();
    protected InnerScrollView innerScrollView;
    protected InnerTrackView track;
    protected boolean lock;
    private int indexColor;
    private int trackColor;
    private int graduationLColor;
    private int graduationSColor;
    private int graduationTextColor;
    private float graduationTextSize;

    public TrackView(Context context) {
        this(context, null);
    }

    public TrackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrackView);
        indexColor = typedArray.getColor(R.styleable.TrackView_index_color, getResources().getColor(R.color.TrackView_default_index));
        trackColor = typedArray.getColor(R.styleable.TrackView_track_color, getResources().getColor(R.color.TrackView_default_track));
        graduationLColor = typedArray.getColor(R.styleable.TrackView_graduation_l_color, getResources().getColor(R.color.TrackView_default_graduation_l));
        graduationSColor = typedArray.getColor(R.styleable.TrackView_graduation_s_color, getResources().getColor(R.color.TrackView_default_graduation_s));
        graduationTextColor = typedArray.getColor(R.styleable.TrackView_graduation_text_color, getResources().getColor(R.color.TrackView_default_graduation_text));
        graduationTextSize = typedArray.getDimension(R.styleable.TrackView_graduation_text_size, getResources().getDimension(R.dimen.TrackView_default_graduation_text));
    }

    private void initView(Context context) {
        InnerTrackView.TrackViewAttrs trackAttrs = new InnerTrackView.TrackViewAttrs(trackColor, graduationLColor, graduationSColor, graduationTextColor, graduationTextSize);
        innerScrollView = new InnerScrollView(context, trackAttrs);
        FrameLayout.LayoutParams params = generateDefaultLayoutParams();
        params.width = 3;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 100;
        params.gravity = Gravity.CENTER_HORIZONTAL;

        addView(innerScrollView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(new CenterLineView(context, indexColor), params);

        track = innerScrollView.findViewById(R.id.track_view);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return lock || super.onInterceptTouchEvent(ev);
    }

    public void go(int decibel) {
        track.addDecibel(decibel);
        go();
    }

    public void go() {
        track.notifyDecibelChange();
        post(new Runnable() {
            @Override
            public void run() {
                innerScrollView.scrollToLast();
            }
        });
    }

    /**
     * 禁止触摸
     *
     * @param lock
     */
    public void lock(boolean lock) {
        this.lock = lock;
    }

    /**
     * 设置刻度滑动监听器
     *
     * @param onSlideGraduationListener
     */
    public void setOnSlideGraduationListener(SlideGraduationListener onSlideGraduationListener) {
        innerScrollView.setOnSlideGraduationListener(onSlideGraduationListener);
    }

}