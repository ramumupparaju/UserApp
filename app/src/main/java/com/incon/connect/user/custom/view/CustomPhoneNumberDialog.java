package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.CustomPhoneNumberAlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewCustomPhoneNumberDialogBinding;
import com.incon.connect.user.databinding.ViewEditTextDialogBinding;

public class CustomPhoneNumberDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // required
    private final String leftButtonText; // required
    private final String rightButtonText; // required
    private final CustomPhoneNumberAlertDialogCallback mAlertDialogCallback; // required
    private ViewCustomPhoneNumberDialogBinding binding;

    /**
     * @param builder
     */
    private CustomPhoneNumberDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.leftButtonText = builder.leftButtonText;
        this.rightButtonText = builder.rightButtonText;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_custom_phone_number_dialog, null, false);
        View contentView = binding.getRoot();
        binding.includeRegisterBottomButtons.buttonLeft.setText(context.getString(R.string.action_cancel));
        binding.includeRegisterBottomButtons.buttonRight.setText(context.getString(R.string.action_submit));
        binding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        binding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);
        //loadUsersSpinner();
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
            case R.id.button_left:
                mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.CANCEL);
                break;
            case R.id.button_right:
                mAlertDialogCallback.enteredName(binding.edittextUsername.getText().toString());
                mAlertDialogCallback.enteredPhoneNumber(binding.edittextPhoneNumber.getText().toString());
                mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.OK);
                break;
            default:
                break;
        }
    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final CustomPhoneNumberAlertDialogCallback callback;
        private String title;
        private String leftButtonText;
        private String rightButtonText;


        public AlertDialogBuilder(Context context, CustomPhoneNumberAlertDialogCallback callback) {
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
        public CustomPhoneNumberDialog build() {
            CustomPhoneNumberDialog dialog = new CustomPhoneNumberDialog(
                    this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }
    }
}
