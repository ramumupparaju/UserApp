package com.incon.connect.user.ui.register;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.defaults.DefaultsResponse;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;
import com.incon.connect.user.utils.OfflineDataManager;

import io.reactivex.observers.DisposableObserver;

public class RegistrationPresenter extends BasePresenter<RegistrationContract.View> implements
        RegistrationContract.Presenter {

    private static final String TAG = RegistrationPresenter.class.getName();
    private Context appContext;
    private OfflineDataManager offlineDataManager;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
        offlineDataManager = new OfflineDataManager();
    }

    /**
     * Uploading defaults data for registration
     */
    @Override
    public void defaultsApi() {
        getView().showProgress(appContext.getString(R.string.progress_defaults));
        DisposableObserver<DefaultsResponse> observer = new DisposableObserver<DefaultsResponse>() {
            @Override
            public void onNext(DefaultsResponse defaultsResponse) {
                getView().hideProgress();
                offlineDataManager.saveData(defaultsResponse,
                        DefaultsResponse.class.getName());
                getView().startRegistration(true);
            }

            @Override
            public void onError(Throwable e) {
                getView().hideProgress();
                Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                getView().handleException(errorDetails);
                if (errorDetails.first == AppConstants.ErrorCodes.NO_NETWORK) {
                    DefaultsResponse defaultsResponse = offlineDataManager
                            .loadData(DefaultsResponse.class,
                                    DefaultsResponse.class.getName());
                    if (defaultsResponse != null) {
                        getView().startRegistration(true);
                        return;
                    }
                }
                getView().startRegistration(false);
            }

            @Override
            public void onComplete() {
                getView().hideProgress();
            }
        };
        AppApiService.getInstance().defaultsApi().subscribe(observer);
        addDisposable(observer);
    }

}
