package com.incon.connect.user.ui.scan;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.qrcodebaruser.UserInfoResponse;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import io.reactivex.observers.DisposableObserver;


/**
 * Created on 31 May 2017 11:19 AM.
 */
public class ScanTabPresenter extends BasePresenter<ScanTabContract.View> implements
        ScanTabContract.Presenter {

    private static final String TAG = ScanTabPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void userInfoUsingPhoneNumber(final String phoneNumber) {
        getView().showProgress(appContext.getString(R.string.progress_user_details));
        DisposableObserver<UserInfoResponse> observer = new
                DisposableObserver<UserInfoResponse>() {
                    @Override
                    public void onNext(UserInfoResponse userInfoResponse) {
                        if (userInfoResponse.getMsisdn() == null) {
                            newUserRegistration(phoneNumber);
                        } else {
                            getView().hideProgress();
                            getView().userInfo(userInfoResponse);
                        }
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
        AppApiService.getInstance().userInfoUsingPhoneNumber(phoneNumber).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void userInfoUsingQrCode(String qrCode) {

    }

    @Override
    public void newUserRegistration(String phoneNumber) {
        DisposableObserver<UserInfoResponse> observer = new
                DisposableObserver<UserInfoResponse>() {
                    @Override
                    public void onNext(UserInfoResponse userInfoResponse) {
                            getView().hideProgress();
                            getView().userInfo(userInfoResponse);
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
        AppApiService.getInstance().newUserRegistation(phoneNumber).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void userInterestedUsingQrCode(String qrCode) {
        getView().showProgress(appContext.getString(R.string.progress_user_details));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object userInfoResponse) {
                        getView().userInterestedResponce(userInfoResponse);
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
        AppApiService.getInstance().userInterestedUsingQrCode(qrCode).subscribe(observer);
        addDisposable(observer);
    }

}
