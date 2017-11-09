package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewVerifyOtpBinding;

public class AppEditTextDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // required
    private final String leftButtonText; // required
    private final String rightButtonText; // required
    private EditText editTextOtp; // required
    private final TextAlertDialogCallback mAlertDialogCallback; // required

    /**
     * @param builder
     */
    private AppEditTextDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.leftButtonText = builder.leftButtonText;
        this.rightButtonText = builder.rightButtonText;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        ViewVerifyOtpBinding viewVerifyOtpBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_verify_otp, null, false);
        View contentView = viewVerifyOtpBinding.getRoot();

        editTextOtp = viewVerifyOtpBinding.edittextUsername;
        viewVerifyOtpBinding.textVerifyTitle.setText(title);

        if (TextUtils.isEmpty(leftButtonText)) {

        } else
        viewVerifyOtpBinding.includeRegisterBottomButtons.buttonLeft.setText(
                context.getString(R.string.action_back));
        viewVerifyOtpBinding.includeRegisterBottomButtons.buttonRight.setText(
                context.getString(R.string.action_next));
        viewVerifyOtpBinding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        viewVerifyOtpBinding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);
        viewVerifyOtpBinding.resendOtpTv.setOnClickListener(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }

    @Override
    public void onClick(View view) {
        if (mAlertDialogCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.resend_otp_tv:
                mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.RESEND_OTP);
                break;
            case R.id.button_left:
                mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.CANCEL);
                break;
            case R.id.button_right:
                mAlertDialogCallback.enteredText(editTextOtp.getText().toString());
                mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.OK);
                break;
            default:
                break;
        }
    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final TextAlertDialogCallback callback;
        private String title;
        private String leftButtonText;
        private String rightButtonText;


        public AlertDialogBuilder(Context context, TextAlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AlertDialogBuilder leftButtonText(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public AlertDialogBuilder rightButtonText(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        //Return the finally constructed User object
        public AppEditTextDialog build() {
            AppEditTextDialog dialog = new AppEditTextDialog(
                    this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }
    }
}
