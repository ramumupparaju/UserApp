package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.TextAddressDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewNoteBuyrequestBinding;

/**
 * Created by PC on 11/9/2017.
 */

public class AppNotesDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // required
    private EditText editNote; // required
    private final TextAlertDialogCallback mAlertDialogCallback; // required
    ViewNoteBuyrequestBinding binding;

    private AppNotesDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_note_buyrequest, null,
                false);
        View contentView = binding.getRoot();
        //binding.buttonSubmit.setOnClickListener(this);
        binding.buyRequestBottomButtons.buttonLeft.setText(
                context.getString(R.string.action_cancel));
        binding.buyRequestBottomButtons.buttonRight.setText(
                context.getString(R.string.action_submit));
        binding.buyRequestBottomButtons.buttonLeft.setOnClickListener(this);
        binding.buyRequestBottomButtons.buttonRight.setOnClickListener(this);
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
                mAlertDialogCallback.alertDialogCallback(TextAddressDialogCallback.CANCEL);
                break;
            case R.id.button_right:
                mAlertDialogCallback.alertDialogCallback(TextAddressDialogCallback.OK);
                break;
            default:
                break;
        }

        //mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.OK);

    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final TextAlertDialogCallback callback;
        private String title;

        public AlertDialogBuilder(Context context, TextAlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AppNotesDialog build() {
            AppNotesDialog dialog = new AppNotesDialog(
                    this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }

    }


}
