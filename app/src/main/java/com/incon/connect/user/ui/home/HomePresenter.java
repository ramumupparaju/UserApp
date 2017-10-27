package com.incon.connect.user.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import io.reactivex.observers.DisposableObserver;


/**
 * Created on 31 May 2017 11:19 AM.
 */
public class HomePresenter extends BasePresenter<HomeContract.View> implements
        HomeContract.Presenter {

    private static final String TAG = HomePresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void checkQrCodeValidity(final String qrCode) {
        getView().showProgress(appContext.getString(R.string.progress_qr_code_validity));
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object assignQrCodeResponse) {
                getView().navigateToProductAssignScreen(qrCode);
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
        AppApiService.getInstance().checkQrCodestatus(qrCode).subscribe(observer);
        addDisposable(observer);
    }
}
