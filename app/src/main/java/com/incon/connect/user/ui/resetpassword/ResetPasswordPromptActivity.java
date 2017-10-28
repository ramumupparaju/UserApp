package com.incon.connect.user.ui.resetpassword;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppOtpDialog;
import com.incon.connect.user.databinding.ActivityResetPasswordPromptBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.HashMap;

public class ResetPasswordPromptActivity extends BaseActivity {

    private static final String TAG = ResetPasswordPromptActivity.class.getName();
    private ActivityResetPasswordPromptBinding binding;
    private AppOtpDialog dialog;
    private String enteredOtp;
    private String phoneNumber;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password_prompt;
    }

    @Override
    protected void initializePresenter() {
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
                              //  registrationStoreFragmentPresenter.validateOTP(verifyOTP);

                                break;
                            case AlertDialogCallback.CANCEL:
                                dialog.dismiss();
                                ResetPasswordPromptActivity.this.finish();
                                break;
                            case TextAlertDialogCallback.RESEND_OTP:
                               // registrationStoreFragmentPresenter.registerRequestPasswordOtp(
                                     //   phoneNumber);
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




}
