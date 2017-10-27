package com.incon.connect.user.ui.warrantyregistration;


import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.apimodel.components.validateotp.ValidateWarrantyOtpResponse;
import com.incon.connect.user.dto.warrantyregistration.WarrantyRegistration;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.ui.validateotp.ValidateOtpContract;
import com.incon.connect.user.ui.validateotp.ValidateOtpPresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.HashMap;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 9/26/2017.
 */
public class WarrantRegistrationPresenter extends BasePresenter<WarrantRegistrationContract.View>
        implements WarrantRegistrationContract.Presenter {

    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void doModelSearchApi(String modelNumberToSearch) {
        getView().showProgress(appContext.getString(R.string.progress_loading));
        DisposableObserver<List<ModelSearchResponse>> observer =
                new DisposableObserver<List<ModelSearchResponse>>() {
                    @Override
                    public void onNext(List<ModelSearchResponse> searchResponseList) {
                        getView().loadModelNumberData(searchResponseList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);
                        if (errorDetails.first != AppConstants.ErrorCodes.NO_NETWORK) {
                            getView().loadModelNumberData(null);
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().modelNumberSearch(modelNumberToSearch).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void doWarrantyRegistrationApi(WarrantyRegistration warrantyRegistration) {
        getView().showProgress(appContext.getString(R.string.progress_warranty_registering));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object warrantyRegisteredResponse) {
                        getView().warrantyRegistered(warrantyRegisteredResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        WarrantRegistrationContract.View view = getView();
                        view.hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        view.handleException(errorDetails);
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().warrantyRegisterApi(warrantyRegistration).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void validateUserOTP(HashMap<String, String> verify) {
        getView().showProgress(appContext.getString(R.string.validating_code));
        ValidateOtpPresenter otpPresenter = new ValidateOtpPresenter();
        otpPresenter.initialize(null);
        otpPresenter.setView(otpView);
        otpPresenter.validateWarrantyOTP(verify);
    }

    @Override
    public void resendUserOTP(String phoneNumber) {
        getView().showProgress(appContext.getString(R.string.progress_resend));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object warrantyRegisteredResponse) {
                        //do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        WarrantRegistrationContract.View view = getView();
                        view.hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        view.handleException(errorDetails);
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().warrantyRequestOtp(phoneNumber).subscribe(observer);
        addDisposable(observer);

    }

    ValidateOtpContract.View otpView = new ValidateOtpContract.View() {
        @Override
        public void validateOTP(LoginResponse loginResponse) {
            //Do nothing
        }

        @Override
        public void validateWarrantyOTP(ValidateWarrantyOtpResponse warrantyOtpResponse) {
            getView().hideProgress();
            getView().validateUserOTP();
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
