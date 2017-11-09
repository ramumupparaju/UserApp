package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewNoteBuyrequestBinding;

/**
 * Created by PC on 11/9/2017.
 */

public class AppNoteDialog  extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // required
    private EditText editNote; // required
    private final TextAlertDialogCallback mAlertDialogCallback; // required

    private AppNoteDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        ViewNoteBuyrequestBinding viewNoteBuyrequestBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_note_buyrequest, null,
                false);
        View contentView = viewNoteBuyrequestBinding.getRoot();
    }

    @Override
    public void onClick(View view) {

        if (mAlertDialogCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.button_submit:
                mAlertDialogCallback.enteredText(editNote.getText().toString());
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


        public AlertDialogBuilder(Context context, TextAlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder title(String title) {
            this.title = title;
            return this;
        }


         public AppNoteDialog build() {
             AppNoteDialog dialog = new AppNoteDialog(
                    this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }

    }



}
