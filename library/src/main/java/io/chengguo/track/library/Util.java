package io.chengguo.track.library;

import android.util.Log;

import java.lang.reflect.Field;

import static io.chengguo.track.library.BuildConfig.DEBUG;

/**
 * Created by FingerArt on 2017/12/5.
 */
class Util {

    public static Object getField(Object obj, String className, String fieldName) {
        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void log(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }
}
