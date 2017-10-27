package com.incon.connect.user.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.incon.connect.user.ConnectApplication;

public class NetworkUtil {

    public static boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) ConnectApplication.getAppContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }


}
