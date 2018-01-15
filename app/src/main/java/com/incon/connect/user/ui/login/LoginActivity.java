package com.incon.connect.user.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppOtpDialog;
import com.incon.connect.user.databinding.ActivityLoginBinding;
import com.incon.connect.user.dto.login.LoginUserData;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.forgotpassword.ForgotPasswordActivity;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.notifications.PushPresenter;
import com.incon.connect.user.ui.register.RegistrationActivity;
import com.incon.connect.user.ui.resetpassword.ResetPasswordPromptActivity;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.PermissionUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.HashMap;

import static com.incon.connect.user.AppConstants.HttpErrorCodeConstants.ERROR_OTP_VALIDATION;


public class LoginActivity extends BaseActivity implements LoginContract.View {

    private static final String TAG = LoginActivity.class.getName();

    private ActivityLoginBinding binding;
    private LoginPresenter loginPresenter;
    private AppOtpDialog dialog;
    private String enteredOtp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        loginPresenter = new LoginPresenter();
        loginPresenter.setView(this);
        setBasePresenter(loginPresenter);
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        // handle events from here using android binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setActivity(this);

//        LoginUserData loginUserData = new LoginUserData();
        LoginUserData loginUserData = new LoginUserData("9493486529", "qwerty123");
        String phoneNumberPreference = SharedPrefsUtils.loginProvider().
                getStringPreference(LoginPrefs.USER_PHONE_NUMBER);
        if (!TextUtils.isEmpty(phoneNumberPreference)) {
            loginUserData.setPhoneNumber(phoneNumberPreference);
            binding.edittextPassword.requestFocus();
        }
        binding.setUser(loginUserData);
        boolean isOtpVerifiedFailed = SharedPrefsUtils.loginProvider().getBooleanPreference(
                LoginPrefs.IS_REGISTERED, false);
        boolean isForgotOtpVerifiedFailed = SharedPrefsUtils.loginProvider().getBooleanPreference(
                LoginPrefs.IS_FORGOT_PASSWORD, false);
        if (isOtpVerifiedFailed) {
            showOtpDialog();
        } else if (isForgotOtpVerifiedFailed) {
            final String phoneNumber = SharedPrefsUtils.loginProvider().getStringPreference(
                    LoginPrefs.USER_PHONE_NUMBER);
            Intent registrationIntent = new Intent(this, ResetPasswordPromptActivity.class);
            registrationIntent.putExtra(IntentConstants.USER_PHONE_NUMBER, phoneNumber);
            startActivity(registrationIntent);
        }
    }

    @Override
    public void handleException(Pair<Integer, String> error) {
        if (error.first == ERROR_OTP_VALIDATION) {
            SharedPrefsUtils.loginProvider().setStringPreference(
                    LoginPrefs.USER_PHONE_NUMBER, binding.edittextUsername.getText().toString());
            showOtpDialog();
        } else {
            super.handleException(error);
        }
    }

    private void showOtpDialog() {
        final String phoneNumber = SharedPrefsUtils.loginProvider().getStringPreference(
                LoginPrefs.USER_PHONE_NUMBER);
        dialog = new AppOtpDialog.AlertDialogBuilder(LoginActivity.this, new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String otpString) {
                        enteredOtp = otpString;
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                if (TextUtils.isEmpty(enteredOtp)) {
                                    showErrorMessage(getString(R.string.error_otp_req));
                                    return;
                                }
                                HashMap<String, String> verifyOTP = new HashMap<>();
                                verifyOTP.put(ApiRequestKeyConstants.BODY_MOBILE_NUMBER,
                                        phoneNumber);
                                verifyOTP.put(ApiRequestKeyConstants.BODY_OTP, enteredOtp);
                                loginPresenter.validateOTP(verifyOTP);
                                break;
                            case AlertDialogCallback.CANCEL:
                                dialog.dismiss();
                                break;
                            case TextAlertDialogCallback.RESEND_OTP:
                                loginPresenter.registerRequestOtp(phoneNumber);
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.dialog_verify_title, phoneNumber))
                .build();
        dialog.showDialog();
    }

    @Override
    public void navigateToHomePage(LoginResponse loginResponse) {
        if (loginResponse == null) {
            clearData();
            return;
        }

        PushPresenter pushPresenter = new PushPresenter();
        pushPresenter.pushRegisterApi();

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(homeIntent);
        finish();
    }

    public void onLoginClick() {
        LoginUserData loginUserData = binding.getUser();
        int validationRes = loginUserData.validateLogin();

        binding.inputLayoutUsername.setError(null);
        binding.inputLayoutPassword.setError(null);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        switch (validationRes) {
            case LoginValidation.PHONE_NUMBER_REQ:
                binding.inputLayoutUsername.setError(getString(R.string.error_phone_req));
                binding.inputLayoutUsername.startAnimation(shake);
                break;
            case LoginValidation.PHONE_NUMBER_NOTVALID:
                binding.inputLayoutUsername.setError(getString(R.string.error_phone_min_digits));
                binding.inputLayoutUsername.startAnimation(shake);
                break;
            case LoginValidation.PASSWORD_REQ:
                binding.inputLayoutPassword.setError(getString(R.string.error_password_req));
                binding.inputLayoutPassword.startAnimation(shake);
                break;
            default:
                loginPresenter.doLogin(binding.getUser());
                break;
        }
    }

    public void onRegisterClick() {
        PermissionUtils.getInstance().grantPermission(LoginActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                new PermissionUtils.Callback() {
                    @Override
                    public void onFinish(HashMap<String, Integer> permissionsStatusMap) {
                        int locationStatus = permissionsStatusMap.get(
                                Manifest.permission.ACCESS_FINE_LOCATION);
                        switch (locationStatus) {
                            case PermissionUtils.PERMISSION_GRANTED:
                                navigateToRegisterScreen();
                                Logger.v(TAG, "location :" + "granted");
                            case PermissionUtils.PERMISSION_DENIED:
                                Logger.v(TAG, "location :" + "denied");
                                break;
                            case PermissionUtils.PERMISSION_DENIED_FOREVER:
                                Logger.v(TAG, "location :" + "denied forever");
                            default:
                                showErrorMessage(getString(R.string.location_permission_msg));
                                break;
                        }
                    }
                });
    }

    private void navigateToRegisterScreen() {
        Intent registrationIntent = new Intent(this, RegistrationActivity.class);
        String phoneNumber = binding.edittextUsername.getText().toString();
        if (!TextUtils.isEmpty(phoneNumber)) {
            registrationIntent.putExtra(IntentConstants.USER_PHONE_NUMBER, phoneNumber);
        }
        startActivity(registrationIntent);
        // donot finish current activity, since user may come back to login screen from registration
        // screens

    }


    public void onForgotPasswordClick() {
        Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
        String phoneNumber = binding.edittextUsername.getText().toString();
        if (!TextUtils.isEmpty(phoneNumber)) {
            forgotPasswordIntent.putExtra(IntentConstants.USER_PHONE_NUMBER, phoneNumber);
        }
        startActivityForResult(forgotPasswordIntent, RequestCodes.FORGOT_PASSWORD);
        // donot finish current activity, since user may come back to login screen from registration
        // screens
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.FORGOT_PASSWORD:
                    binding.edittextPassword.setText("");
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        loginPresenter.disposeAll();
    }
}