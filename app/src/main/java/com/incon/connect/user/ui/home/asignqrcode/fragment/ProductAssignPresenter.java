package com.incon.connect.user.ui.home.asignqrcode.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.dto.asignqrcode.AssignQrCode;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.ui.warrantyregistration.WarrantRegistrationContract;
import com.incon.connect.user.ui.warrantyregistration.WarrantRegistrationPresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/12/2017.
 */

public class ProductAssignPresenter extends BasePresenter<ProductAssignContract.View> implements
        ProductAssignContract.Presenter {
    private static final String TAG = ProductAssignPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    public void assignQrCodeToProduct(AssignQrCode qrCode) {
        getView().showProgress(appContext.getString(R.string.progress_qr_code_product));
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object assignQrCodeResponse) {
                getView().hideProgress();
                getView().productAssignQrCode(assignQrCodeResponse);
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
        AppApiService.getInstance().assignQrCodeToProduct(qrCode).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void doModelSearchApi(String modelNumberToSearch) {
        WarrantRegistrationPresenter warrantRegistrationPresenter =
                new WarrantRegistrationPresenter();
        warrantRegistrationPresenter.initialize(null);
        warrantRegistrationPresenter.setView(warrantyView);
        warrantRegistrationPresenter.doModelSearchApi(modelNumberToSearch);
    }

    WarrantRegistrationContract.View warrantyView = new WarrantRegistrationContract.View() {
        @Override
        public void loadModelNumberData(List<ModelSearchResponse> modelSearchResponseList) {
            getView().loadModelNumberData(modelSearchResponseList);
        }

        @Override
        public void warrantyRegistered(Object warrantyRegisteredResponse) {
            //DO nothing
        }

        @Override
        public void validateUserOTP() {
            //DO nothing
        }

        @Override
        public void showProgress(String message) {
            getView().showProgress(message);
        }

        @Override
        public void hideProgress() {
            getView().hideProgress();
        }

        @Override
        public void showErrorMessage(String errorMessage) {
            getView().showErrorMessage(errorMessage);
        }

        @Override
        public void handleException(Pair<Integer, String> error) {
            getView().handleException(error);
        }
    };
}
