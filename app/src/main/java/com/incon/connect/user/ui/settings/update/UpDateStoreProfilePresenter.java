package com.incon.connect.user.ui.settings.update;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.updatestoreprofile.UpDateStoreProfileResponce;
import com.incon.connect.user.dto.update.UpDateStoreProfile;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/13/2017.
 */

public class UpDateStoreProfilePresenter extends BasePresenter<UpDateStoreProfileContract.View>
        implements UpDateStoreProfileContract.Presenter {
    private static final String TAG = UpDateStoreProfilePresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    public void upDateStoreProfile(int merchantId, UpDateStoreProfile upDateStoreProfile) {
        getView().showProgress(appContext.getString(R.string.progress_updateuserprofile));
        DisposableObserver<UpDateStoreProfileResponce> observer = new
                DisposableObserver<UpDateStoreProfileResponce>() {
                    @Override
                    public void onNext(UpDateStoreProfileResponce upDateStoreProfileResponce) {
                        getView().hideProgress();
                        getView().loadUpDateStoreProfileResponce(upDateStoreProfileResponce);
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
        AppApiService.getInstance().upDateStoreProfile(merchantId, upDateStoreProfile).
                subscribe(observer);
        addDisposable(observer);

    }

}
