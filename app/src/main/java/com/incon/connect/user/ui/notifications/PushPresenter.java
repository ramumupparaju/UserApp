package com.incon.connect.user.ui.notifications;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.dto.notifications.PushRegistrarBody;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.DeviceUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.TimeZone;

import io.reactivex.observers.DisposableObserver;

/**
 * Created on 31 May 2017 11:19 AM.
 */
public class PushPresenter extends BasePresenter<PushContract.View> implements
        PushContract.Presenter {

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
    }


    @Override
    public void pushRegisterApi() {

        int userId = SharedPrefsUtils.loginProvider()
                .getIntegerPreference(AppConstants.LoginPrefs.USER_ID, AppConstants.DEFAULT_VALUE);
        if (userId == AppConstants.DEFAULT_VALUE) {
            return;
        }

        PushRegistrarBody pushRegistrarBody = new PushRegistrarBody();

        pushRegistrarBody.setuId(DeviceUtils.getUniqueID());
        pushRegistrarBody.setPushKey(FirebaseInstanceId.getInstance().getToken());
        pushRegistrarBody.setOsVersion(String.valueOf(Build.VERSION.SDK_INT));
        pushRegistrarBody.setManufacturer(Build.MANUFACTURER);
        pushRegistrarBody.setLocale(TimeZone.getDefault().getID());
        pushRegistrarBody.setModel(Build.MODEL);
        pushRegistrarBody.setDeviceType(AppConstants.PushSubTypeConstants.PUSH_DEVICE_TYPE);

        DisposableObserver<Object> observer =
                new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object object) {
                        SharedPrefsUtils.cacheProvider().setBooleanPreference(AppConstants
                                .LoginPrefs.PUSH_TOKEN_STATUS, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                };

        AppApiService.getInstance().pushTokenApi(userId, pushRegistrarBody).subscribe(observer);
        addDisposable(observer);
    }

}
