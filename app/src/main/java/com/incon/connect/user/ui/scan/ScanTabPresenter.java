package com.incon.connect.user.ui.scan;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.HashMap;

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
    public void userInterestedUsingQrCode(int customerId, String qrCode) {
        HashMap<String, String> qrCodeMap = new HashMap<>();
        qrCodeMap.put(AppConstants.ApiRequestKeyConstants.BODY_PRODUCT_CODE,
                qrCode);

        getView().showProgress(appContext.getString(R.string.progress_product_details));
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
        AppApiService.getInstance().userInterestedUsingQrCode(customerId, qrCodeMap).
                subscribe(observer);
        addDisposable(observer);
    }

}
