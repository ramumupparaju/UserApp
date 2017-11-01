package com.incon.connect.user.ui.register.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.validateotp.ValidateWarrantyOtpResponse;
import com.incon.connect.user.data.login.LoginDataManagerImpl;
import com.incon.connect.user.dto.registration.Registration;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.ui.validateotp.ValidateOtpContract;
import com.incon.connect.user.ui.validateotp.ValidateOtpPresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;

/**
 * Created on 08 Jun 2017 8:31 PM.
 */
public class RegistrationUserFragmentPresenter extends
        BasePresenter<RegistrationUserFragmentContract.View> implements
        RegistrationUserFragmentContract.Presenter {

    private Context appContext;
    private static final String TAG = RegistrationUserFragmentPresenter.class.getName();
    private LoginDataManagerImpl loginDataManagerImpl;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
        loginDataManagerImpl = new LoginDataManagerImpl();
    }


    public void register(Registration registrationBody) {
        getView().showProgress(appContext.getString(R.string.progress_registering));
        DisposableObserver<LoginResponse> observer = new DisposableObserver<LoginResponse>() {
            @Override
            public void onNext(LoginResponse loginResponse) {
                getView().uploadUserData(loginResponse.getId());
                getView().validateOTP();
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
        AppApiService.getInstance().register(registrationBody).subscribe(observer);
        addDisposable(observer);
    }


    @Override
    public void validateOTP(HashMap<String, String> verify) {
        getView().showProgress(appContext.getString(R.string.validating_code));
        ValidateOtpPresenter otpPresenter = new ValidateOtpPresenter();
        otpPresenter.initialize(null);
        otpPresenter.setView(otpView);
        otpPresenter.validateOTP(verify);
    }


    ValidateOtpContract.View otpView = new ValidateOtpContract.View() {
        @Override
        public void validateOTP(LoginResponse loginResponse) {
      // save login data to shared preferences
            loginDataManagerImpl.saveLoginDataToPrefs(loginResponse);
            getView().hideProgress();
            getView().navigateToHomeScreen();
        }

        @Override
        public void validateWarrantyOTP(ValidateWarrantyOtpResponse warrantyOtpResponse) {
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

    @Override
    public void registerRequestOtp(String phoneNumber) {
        getView().showProgress(appContext.getString(R.string.progress_resend));
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object loginResponse) {
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
        AppApiService.getInstance().registerRequestOtp(phoneNumber).subscribe(observer);
        addDisposable(observer);

    }

    @Override
    public void registerRequestPasswordOtp(String phoneNumber) {

        getView().showProgress(appContext.getString(R.string.progress_resend));
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object loginResponse) {
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
        AppApiService.getInstance().registerRequestPasswordOtp(phoneNumber).subscribe(observer);
        addDisposable(observer);

    }

    /*public Observable<SendOtpResponse> getSendOtpObserver(HashMap<String, String> email) {
        return TueoApiService.getInstance().sendOtp(email);
    }

    public Observable<ValidateOtpResponse> getVerifyOtpObserver(HashMap<String, String> verify) {
        return TueoApiService.getInstance().verifyOtp(verify);
    }

    public Observable<ValidateEmailResponse> getValidateEmailObserver(String email) {
        return TueoApiService.getInstance().validateEmail(email);
    }

    @Override
    public void sendOtp(HashMap<String, String> email) {
        getView().showProgress(appContext.getString(R.string.progress_sendotp));
        Observable<SendOtpResponse> sendOtpObserver = getSendOtpObserver(email);
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                getView().hideProgress();
                getView().showVerifyEmailPopup();
            }
            @Override
            public void onError(Throwable e) {
                getView().hideProgress();
                getView().handleException(ErrorMsgUtil.getErrorDetails(e));
            }
            @Override
            public void onComplete() {
                getView().hideProgress();
            }
        };
        sendOtpObserver.subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void verifyOtp(HashMap<String, String> verifyOtpMap) {
        getView().showProgress(appContext.getString(R.string.progress_verifycode));
        Observable<ValidateOtpResponse> veriftOtpObserver = getVerifyOtpObserver(verifyOtpMap);
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {

                ValidateOtpResponse validateOtpResponse = (ValidateOtpResponse) o;
                Logger.v(TAG, "==> responseCallback : StatusCode = "
                        + validateOtpResponse.getStatusCode());

                int statusCode = validateOtpResponse.getStatusCode();
                switch (statusCode) {
                    case 200:
                        getView().navigateToNext();
                        break;
                    default:
                        String errorMsg = validateOtpResponse.getMessage();
                        getView().showErrorMessage(errorMsg);
                        break;
                }
            }
            @Override
            public void onError(Throwable e) {
                getView().hideProgress();
                getView().handleException(ErrorMsgUtil.getErrorDetails(e));
            }
            @Override
            public void onComplete() {
                getView().hideProgress();
            }
        };
        veriftOtpObserver.subscribe(observer);
        addDisposable(observer);
    }*/

    /*@Override
    public void validateEmailInUse(String email) {
        getView().showProgress(appContext.getString(R.string.progress_verifyemail));
        Observable<ValidateEmailResponse> veriftOtpObserver = getValidateEmailObserver(email);
        DisposableObserver<Object> observer = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                getView().hideProgress();
                ValidateEmailResponse validateEmailResponse = (ValidateEmailResponse) o;
                getView().onValidateEmailInUseCheck(validateEmailResponse);
            }
            @Override
            public void onError(Throwable e) {
                getView().hideProgress();
                getView().handleException(ErrorMsgUtil.getErrorDetails(e));
            }
            @Override
            public void onComplete() {
            }
        };
        veriftOtpObserver.subscribe(observer);
        addDisposable(observer);
    }*/

}
