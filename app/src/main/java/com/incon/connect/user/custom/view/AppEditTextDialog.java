package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewEditTextDialogBinding;

public class AppEditTextDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // required
    private final String leftButtonText; // required
    private final String rightButtonText; // required
    private final String hintText; // required
    private EditText editTextNotes; // required
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
        this.hintText = builder.hintText;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        ViewEditTextDialogBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_edit_text_dialog, null, false);
        View contentView = binding.getRoot();
        RelativeLayout.LayoutParams crlp = (RelativeLayout.LayoutParams) binding.dialogTitleTextView.getLayoutParams();
        crlp.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.8);

        editTextNotes = binding.edittextComments;
        binding.dialogTitleTextView.setText(title);
        if (!TextUtils.isEmpty(hintText)) {
            editTextNotes.setHint(hintText);
        }
        if (title.equals(getContext().getString(
                R.string.bottom_option_transfer))) {
            editTextNotes.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        binding.includeRegisterBottomButtons.buttonLeft.setText(
                TextUtils.isEmpty(leftButtonText) ? context.getString(
                        R.string.action_back) : leftButtonText);
        binding.includeRegisterBottomButtons.buttonRight.setText(
                TextUtils.isEmpty(leftButtonText) ? context.getString(
                        R.string.action_next) : rightButtonText);
        binding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        binding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);

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
                mAlertDialogCallback.enteredText(editTextNotes.getText().toString());
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
        private String hintText;


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

        public AlertDialogBuilder hintText(String hintText) {
            this.hintText = hintText;
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
