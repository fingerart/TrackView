package io.chengguo.track.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import static io.chengguo.track.library.InnerTrackView.UNIT_TRACK;
import static io.chengguo.track.library.Util.log;
import static io.chengguo.track.library.Util.getField;

class InnerScrollView extends HorizontalScrollView {
    private static final String TAG = TrackView.class.getSimpleName();
    InnerTrackView innerTrackView;
    protected SlideGraduationListener graduationListener;
    private OverScroller mScroller;
    private boolean mInTouch;
    private GestureDetector flingGestureDetector;

    public InnerScrollView(Context context, InnerTrackView.TrackViewAttrs trackAttrs) {
        this(context);
        initView(context, trackAttrs);
    }

    public InnerScrollView(Context context) {
        this(context, ((AttributeSet) null));
    }

    public InnerScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InnerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = (OverScroller) getField(this, "android.widget.HorizontalScrollView", "mScroller");
        flingGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }
        });
    }

    private void initView(Context context, InnerTrackView.TrackViewAttrs trackAttrs) {
        FrameLayout container = new FrameLayout(context);
        innerTrackView = new InnerTrackView(context, trackAttrs);
        innerTrackView.setId(R.id.track_view);
        container.addView(innerTrackView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mInTouch = ev.getAction() != MotionEvent.ACTION_UP;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!flingGestureDetector.onTouchEvent(ev)) {//不是fling
                    checkScrollStop();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 滚动是否停止
     */
    private void checkScrollStop() {
        boolean isScrollStop = mScroller != null && mScroller.isFinished() && !mInTouch;
        final int l = getScrollX();
        log(TAG, "checkScrollStop() called with: isScrollStop = [" + isScrollStop + "], l[" + l + "]");
        if (isScrollStop && l % UNIT_TRACK != 0) {
            int offset = l % UNIT_TRACK;
            scrollBy(offset < 3 ? -offset : UNIT_TRACK - offset, 0);
        }
    }

    public void setOnSlideGraduationListener(SlideGraduationListener listener) {
        graduationListener = listener;
    }

    public void scrollToLast() {
        scrollTo(innerTrackView.getMeasuredWidth(), 0);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        checkScrollStop();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        log(TAG, "onScrollChanged() called with: l = [" + l + "], t = [" + t + "], oldl = [" + oldl + "], oldt = [" + oldt + "]");
        super.onScrollChanged(l, t, oldl, oldt);
        if (graduationListener != null) {
            graduationListener.onGraduationChanged(l < 0 ? 0 : l);
        }
    }
}