package com.incon.connect.user.ui.billformat;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.ui.login.LoginPresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import io.reactivex.observers.DisposableObserver;
import okhttp3.MultipartBody;

/**
 * Created by PC on 11/16/2017.
 */

public class BillFormatPresenter extends BasePresenter<BillFormatContract.View> implements
        BillFormatContract.Presenter {

    private static final String TAG = LoginPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void uploadBill(int purchaseId, MultipartBody.Part serviceCenterLogo) {
        getView().showProgress(appContext.getString(R.string.progress_uploading_bill));
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object billResponse) {
                getView().hideProgress();
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
        AppApiService.getInstance().uploadBill(purchaseId, serviceCenterLogo).subscribe(observer);
        addDisposable(observer);
    }

}
