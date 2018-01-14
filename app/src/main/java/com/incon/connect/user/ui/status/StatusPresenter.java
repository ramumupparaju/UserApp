package com.incon.connect.user.ui.status;

import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 12/1/2017.
 */

public class StatusPresenter extends BasePresenter<StatusContract.View> implements
        StatusContract.Presenter {

    private static final String TAG = StatusPresenter.class.getName();
    private ConnectApplication appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    // get status list
    @Override
    public void getDefaultStatusData(final int userId) {
        DisposableObserver<List<DefaultStatusData>> observer = new DisposableObserver<List<DefaultStatusData>>() {
            @Override
            public void onNext(List<DefaultStatusData> statusListResponses) {
                appContext.setStatusListData(statusListResponses);
                fetchUserRequests(userId);
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
        AppApiService.getInstance().getStatusList().subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void fetchUserRequests(int userId) {
        getView().showProgress(appContext.getString(R.string.progress_updating_status));
        if (appContext.getStatusListResponses() == null) {
            getDefaultStatusData(userId);
            return;
        }
        Observable<List<ProductInfoResponse>> userListObservable = getProductStatusListObservable(userId);
        Observable<ArrayList<ServiceStatus>> designationsListObservable =
                getServiceStatusListObservable(userId);

        Observable<String> zip = Observable.zip(userListObservable, designationsListObservable, new BiFunction<List<ProductInfoResponse>, ArrayList<ServiceStatus>, String>() {
            @Override
            public String apply(@NonNull List<ProductInfoResponse> usersList, @NonNull ArrayList<ServiceStatus> designationsList) throws Exception {

                getView().loadServiceRequests((ArrayList<ProductInfoResponse>) usersList, designationsList);
                return "";
            }
        });

        DisposableObserver<String> observer = new
                DisposableObserver<String>() {
                    @Override
                    public void onNext(String usersListOfServiceCenters) {
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
        zip.subscribe(observer);
        addDisposable(observer);
    }

    private Observable<ArrayList<ServiceStatus>> getServiceStatusListObservable(int userId) {
        return AppApiService.getInstance().fetchUserRequests(4566);
    }

    private Observable<List<ProductInfoResponse>> getProductStatusListObservable(int userId) {
        return AppApiService.getInstance().purchasedStatus(userId);
    }
}
