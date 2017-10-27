package com.incon.connect.user.custom.exception;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;

import java.io.IOException;

public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return ConnectApplication.getAppContext().getString(R.string.msg_check_internet);
    }
}
