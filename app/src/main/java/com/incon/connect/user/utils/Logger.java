package com.incon.connect.user.utils;

import android.util.Log;

import com.incon.connect.user.BuildConfig;


public class Logger {

    public static void v(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void e(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void d(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

}
