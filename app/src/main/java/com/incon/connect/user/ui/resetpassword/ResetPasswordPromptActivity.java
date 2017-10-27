package com.incon.connect.user.ui.resetpassword;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppOtpDialog;
import com.incon.connect.user.databinding.ActivityResetPasswordPromptBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.changepassword.ChangePasswordActivity;
import com.incon.connect.user.ui.register.fragment.RegistrationStoreFragmentContract;
import com.incon.connect.user.ui.register.fragment.RegistrationStoreFragmentPresenter;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.HashMap;

public class ResetPasswordPromptActivity extends BaseActivity implements
        RegistrationStoreFragmentContract.View {

    private static final String TAG = ResetPasswordPromptActivity.class.getName();
    private ActivityResetPasswordPromptBinding binding;
    private RegistrationStoreFragmentPresenter registrationStoreFragmentPresenter;
    private AppOtpDialog dialog;
    private String enteredOtp;
    private String phoneNumber;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password_prompt;
    }

    @Override
    protected void initializePresenter() {
        registrationStoreFragmentPresenter = new RegistrationStoreFragmentPresenter();
        registrationStoreFragmentPresenter.setView(this);
        setBasePresenter(registrationStoreFragmentPresenter);
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        // handle events from here using android binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setResetPasswordPrompt(this);
        phoneNumber = getIntent().getStringExtra(IntentConstants.USER_PHONE_NUMBER);


        //make it as registration done but not verified otp
        SharedPrefsUtils.loginProvider().setBooleanPreference(LoginPrefs.IS_FORGOT_PASSWORD, true);
        SharedPrefsUtils.loginProvider().setStringPreference(LoginPrefs.USER_PHONE_NUMBER,
                phoneNumber);
        showOtpDialog();
    }

    private void showOtpDialog() {
        dialog = new AppOtpDialog.AlertDialogBuilder(ResetPasswordPromptActivity.this, new
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
                                registrationStoreFragmentPresenter.validateOTP(verifyOTP);

                                break;
                            case AlertDialogCallback.CANCEL:
                                dialog.dismiss();
                                ResetPasswordPromptActivity.this.finish();
                                break;
                            case TextAlertDialogCallback.RESEND_OTP:
                                registrationStoreFragmentPresenter.registerRequestPasswordOtp(
                                        phoneNumber);
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
    public void showProgress(String message) {
    }

    @Override
    public void hideProgress() {
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        super.showErrorMessage(errorMessage);
    }

    public void onOkClick() {
        //DO nothing
    }

    @Override
    public void navigateToRegistrationActivityNext() {
        //DO nothing
    }

    @Override
    public void navigateToHomeScreen() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent(this,
                ChangePasswordActivity.class);
        intent.putExtra(IntentConstants.FROM_FORGOT_PASSWORD_SCREEN, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void uploadStoreLogo(int storeId) {
        //DO nothing
    }

    @Override
    public void validateOTP() {
        //DO nothing
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        registrationStoreFragmentPresenter.disposeAll();
    }
}
