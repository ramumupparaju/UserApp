package com.incon.connect.user.ui.settings;

import android.content.Context;
import android.os.Bundle;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.ui.BasePresenter;

/**
 * Created on 31 May 2017 11:19 AM.
 *
 */
public class SettingsPresenter extends BasePresenter<SettingsContract.View> implements
        SettingsContract.Presenter {

    private Context appContext;
    private static final String TAG = SettingsPresenter.class.getName();

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

}
