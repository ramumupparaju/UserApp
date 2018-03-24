package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewStatusBinding;
import com.incon.connect.user.databinding.ViewVerifyOtpBinding;

public class AppStatusDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // required
    private final String description; // required
    private final String phoneNumber; // required
    private final TextAlertDialogCallback mAlertDialogCallback; // required

    /**
     * @param builder
     */
    private AppStatusDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.description = builder.description;
        this.phoneNumber = builder.phoneNumber;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        ViewStatusBinding viewStatusBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_status, null, false);
        View contentView = viewStatusBinding.getRoot();

        viewStatusBinding.titleTv.setText(title);
        viewStatusBinding.descTv.setText(description);
        viewStatusBinding.dialogCallButton.setOnClickListener(this);

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
            case R.id.dialog_call_button:
                mAlertDialogCallback.enteredText(phoneNumber);
                break;
            default:
                break;
        }
    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final TextAlertDialogCallback callback;
        private String title;
        private String description;
        private String phoneNumber;


        public AlertDialogBuilder(Context context, TextAlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AlertDialogBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AlertDialogBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        //Return the finally constructed User object
        public AppStatusDialog build() {
            AppStatusDialog dialog = new AppStatusDialog(
                    this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }
    }
}
