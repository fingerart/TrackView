package io.chengguo.track;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;

/**
 * 工具类
 * Created by FingerArt on 2018/1/12.
 */
class Utils {
    /**
     * 创建LayoutParams
     *
     * @return
     */
    @NonNull
    static <T extends ViewGroup> T.LayoutParams createMPLayoutParams() {
        return new T.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, T.LayoutParams.MATCH_PARENT);
    }

    static void l(String tag, String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, String.format(message, args));
        }
    }

    static void le(String tag, String message, Throwable throwable, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, String.format(message, args), throwable);
        }
    }

    static void le(String tag, String message, Object... args) {
        le(tag, message, null, args);
    }
}
