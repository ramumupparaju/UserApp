package com.incon.connect.user.ui.history.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.history.purchased.PurchasedHistoryResponse;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.List;

import io.reactivex.observers.DisposableObserver;


/**
 * Created on 31 May 2017 11:19 AM.
 */
public class PurchasedPresenter extends BasePresenter<PurchasedContract.View> implements
        PurchasedContract.Presenter {

    private static final String TAG = PurchasedPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void purchased(int userId) {
        getView().showProgress(appContext.getString(R.string.progress_purchased_history));
        DisposableObserver<List<PurchasedHistoryResponse>> observer = new
                DisposableObserver<List<PurchasedHistoryResponse>>() {
            @Override
            public void onNext(List<PurchasedHistoryResponse> historyResponse) {
                getView().loadPurchasedHistory(historyResponse);
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
        AppApiService.getInstance().purchasedApi(userId).subscribe(observer);
        addDisposable(observer);
    }
}
