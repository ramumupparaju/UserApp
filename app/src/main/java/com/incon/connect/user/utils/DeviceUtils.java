package com.incon.connect.user.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.incon.connect.user.ConnectApplication;

import java.util.UUID;

public class DeviceUtils {

    /**
     * Returns the current screen dimensions in device independent pixels (DIP) as a {@link Point} object where
     * {@link Point#x} is the screen width and {@link Point#y} is the screen height.
     *
     * @param context Context instance
     *
     * @return The current screen dimensions in DIP.
     */
    public static Point getScreenDimensionsInDIP(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Configuration configuration = context.getResources().getConfiguration();
            return new Point(configuration.screenWidthDp, configuration.screenHeightDp);

        } else {
            // APIs prior to v13 gave the screen dimensions in pixels. We convert them to DIPs before returning them.
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            int screenWidthInDIP = (int) convertPxToDp(displayMetrics.widthPixels);
            int screenHeightInDIP = (int) convertPxToDp(displayMetrics.heightPixels);
            return new Point(screenWidthInDIP, screenHeightInDIP);
        }
    }
    public static float convertDpToPx(float valueInDp) {
        DisplayMetrics metrics = ConnectApplication.getAppContext().
                getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static int convertSpToPixels(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    public static float convertPxToDp(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                ConnectApplication.getAppContext().getResources().getDisplayMetrics());
    }
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public static String getUniqueID() {
        if (uniqueID == null) {
            SharedPrefsUtils appProvider = SharedPrefsUtils.appProvider();
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                appProvider.setStringPreference(PREF_UNIQUE_ID, uniqueID);
            }
        }
        return uniqueID;
    }
}
