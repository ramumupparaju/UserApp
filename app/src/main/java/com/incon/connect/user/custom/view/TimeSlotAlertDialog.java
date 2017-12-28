package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TimeSlotAlertDialogCallback;
import com.incon.connect.user.databinding.DialogServiceRequestBinding;
import com.incon.connect.user.databinding.DialogTimeSlotBinding;

public class TimeSlotAlertDialog extends Dialog implements View.OnClickListener {
    private DialogTimeSlotBinding binding;

    private final Context context;
    //All final attributes
    private final TimeSlotAlertDialogCallback mAlertDialogCallback; // required

    /**
     * @param builder
     */
    private TimeSlotAlertDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_time_slot, null, false);
        View contentView = binding.getRoot();

        binding.button1.setOnClickListener(this);
        binding.button2.setOnClickListener(this);
        binding.button3.setOnClickListener(this);
        binding.dialogSubmitButton.setOnClickListener(this);


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
            case R.id.dialog_submit_button:
                mAlertDialogCallback.alertDialogCallback(AlertDialogCallback.OK);
                break;
            default:
                if (view.isSelected()) { //checking whether view is selected or not
                    view.setSelected(false);
                    //todo Reset value to send
                } else {
                    LinearLayout buttonsLayout = binding.buttonsLayout;
                    for (int i = 0; i < buttonsLayout.getChildCount(); i++) {
                        View childAt = buttonsLayout.getChildAt(i);
                        childAt.setSelected(view.getId() == childAt.getId() ? true : false);
                    }
                }
                break;
        }
    }

    public static class AlertDialogBuilder {

        private final Context context;
        private final TimeSlotAlertDialogCallback callback;

        public AlertDialogBuilder(Context context, TimeSlotAlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        //Return the finally constructed User object
        public TimeSlotAlertDialog build() {
            TimeSlotAlertDialog dialog = new TimeSlotAlertDialog(this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }
    }
}
