package com.incon.connect.user.ui.history.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/2/2017.
 */

public class InterestPresenter extends BasePresenter<InterestContract.View> implements
        InterestContract.Presenter {
    private static final String TAG = InterestPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void interestApi(int userId) {
        getView().showProgress(appContext.getString(R.string.progress_interest_history));
        DisposableObserver<List<ProductInfoResponse>> observer = new
                DisposableObserver<List<ProductInfoResponse>>() {
                    @Override
                    public void onNext(List<ProductInfoResponse> historyResponse) {
                        getView().loadInterestHistory(historyResponse);
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
        AppApiService.getInstance().interestApi(userId).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void deleteApi(int interestId) {
        getView().showProgress(appContext.getString(R.string.progress_interest_history));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object historyResponse) {
                        getView().loadInterestDeleteHistory(historyResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);
                        getView().showErrorMessage(errorDetails.second);
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().deleteApi(interestId).subscribe(observer);
        addDisposable(observer);
    }



    @Override
    public void buyrequestApi(int userId) {
        getView().showProgress(appContext.getString(R.string.progress_buy_request));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object buyRequestResponce) {
                        getView().loadBuyRequestResponce(buyRequestResponce);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);
                        getView().showErrorMessage(errorDetails.second);
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().buyrequestApi(userId).subscribe(observer);
        addDisposable(observer);
    }
}
