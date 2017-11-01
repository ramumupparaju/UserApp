package com.incon.connect.user.ui.settings.update;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.data.login.LoginDataManagerImpl;
import com.incon.connect.user.dto.update.UpDateUserProfile;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/13/2017.
 */

public class UpDateUserProfilePresenter extends BasePresenter<UpDateUserProfileContract.View>
        implements UpDateUserProfileContract.Presenter {
    private Context appContext;
    private static final String TAG = UpDateUserProfilePresenter.class.getName();


    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    public void upDateUserProfile(int userId, UpDateUserProfile upDateUserProfile) {
        getView().showProgress(appContext.getString(R.string.progress_updateuserprofile));
        DisposableObserver<LoginResponse> observer = new
                DisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        getView().hideProgress();
                        getView().loadUpDateUserProfileResponce(loginResponse);
               saveUserData(loginResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);
                    }

                    @Override
                    public void onComplete() {

                    }
                };
        AppApiService.getInstance().upDateUserProfile(userId, upDateUserProfile).
                subscribe(observer);
        addDisposable(observer);

    }

    @Override
    public void saveUserData(LoginResponse loginResponse) {
        LoginDataManagerImpl loginDataManager = new LoginDataManagerImpl();
        loginDataManager.saveLoginDataToPrefs(loginResponse);
    }

}
