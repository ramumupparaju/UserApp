package com.incon.connect.user.ui.changepassword;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.data.login.LoginDataManagerImpl;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;

public class ChangePasswordPresenter extends BasePresenter<ChangePasswordContract.View> implements
        ChangePasswordContract.Presenter {

    private static final String TAG = ChangePasswordPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void changePassword(HashMap<String, String> password) {
        getView().showProgress(appContext.getString(R.string.progress_changepassword));
        DisposableObserver<LoginResponse> observer = new DisposableObserver<LoginResponse>() {
            @Override
            public void onNext(LoginResponse loginResponse) {
                getView().navigateToLoginPage(loginResponse);
            }
            @Override
            public void onError(Throwable e) {
                Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                getView().handleException(errorDetails);
            }
            @Override
            public void onComplete() {
                getView().hideProgress();
            }
        };
        AppApiService.getInstance().changePassword(password).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void saveUserData(LoginResponse loginResponse) {
        LoginDataManagerImpl loginDataManager = new LoginDataManagerImpl();
        loginDataManager.saveLoginDataToPrefs(loginResponse);
    }

}
