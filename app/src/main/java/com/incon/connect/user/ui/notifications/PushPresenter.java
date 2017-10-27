package com.incon.connect.user.ui.notifications;

import android.os.Bundle;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.SharedPrefsUtils;

/**
 * Created on 31 May 2017 11:19 AM.
 *
 */
public class PushPresenter extends BasePresenter<PushContract.View> implements
        PushContract.Presenter {

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
    }


    @Override
    public void pushRegisterApi() {

        if (SharedPrefsUtils.loginProvider()
                .getIntegerPreference(AppConstants.LoginPrefs.USER_ID, AppConstants
                        .DEFAULT_VALUE) == AppConstants.DEFAULT_VALUE) {
            return;
        }

        //TODO enable
       /* PushRegistrarBody pushRegistrarBody = new PushRegistrarBody();

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

        AppApiService.getInstance().pushTokenApi(pushRegistrarBody).subscribe(observer);
        addDisposable(observer);*/
    }

}
