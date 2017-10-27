package com.incon.connect.user.ui.forgotpassword;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;

public class ForgotPasswordPresenter extends BasePresenter<ForgotPasswordContract.View> implements
        ForgotPasswordContract.Presenter {

    private static final String TAG = ForgotPasswordPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void forgotPassword(HashMap<String, String> phoneNumber) {
        getView().showProgress(appContext.getString(R.string.progress_forgotpassword));
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                getView().navigateToResetPromtPage();
            }
            @Override
            public void onError(Throwable e) {
                getView().hideProgress();
                Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                getView().handleException(errorDetails);
            }
            @Override
            public void onComplete() {
                getView().hideProgress();
            }
        };
        AppApiService.getInstance().forgotPassword(phoneNumber).subscribe(observer);
        addDisposable(observer);
    }
}
