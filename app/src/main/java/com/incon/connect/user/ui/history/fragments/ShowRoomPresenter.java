package com.incon.connect.user.ui.history.fragments;

import android.content.Context;
import android.os.Bundle;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.ui.BasePresenter;

/**
 * Created by PC on 1/23/2018.
 */

public class ShowRoomPresenter extends BasePresenter<ShowRoomContract.View> implements
        ShowRoomContract.Presenter {

    private static final String TAG = ShowRoomPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

}
