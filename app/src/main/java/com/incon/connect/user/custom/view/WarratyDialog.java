package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.TextAddressDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewWarrantyBinding;

/**
 * Created by PC on 11/8/2017.
 */

public class WarratyDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    private ViewWarrantyBinding binding;
    private final TextAlertDialogCallback mAlertDialogCallback; // required
    private String years;
    private String months;
    private String days;

    private WarratyDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        years = builder.years;
        months = builder.months;
        days = builder.days;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_warranty, null, false);
        View contentView = binding.getRoot();
        binding.includeRegisterBottomButtons.buttonLeft.setText(
                context.getString(R.string.action_cancel));
        binding.includeRegisterBottomButtons.buttonRight.setText(
                context.getString(R.string.action_submit));
        binding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        binding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);


        binding.yearsNp.setMinValue(0);
        binding.yearsNp.setMaxValue(20);
        if (!TextUtils.isEmpty(years)) {
            binding.yearsNp.setValue(Integer.parseInt(years));
        }

        binding.monthNp.setMinValue(0);
        binding.monthNp.setMaxValue(12);
        if (!TextUtils.isEmpty(months)) {
            binding.monthNp.setValue(Integer.parseInt(months));
        }

        binding.daysNp.setMinValue(0);
        binding.daysNp.setMaxValue(31);
        if (!TextUtils.isEmpty(days)) {
            binding.daysNp.setValue(Integer.parseInt(days));
        }


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
                mAlertDialogCallback.alertDialogCallback(TextAddressDialogCallback.CANCEL);
                break;
            case R.id.button_right:
                mAlertDialogCallback.enteredText(binding.yearsNp.getValue() + AppConstants.COMMA_SEPARATOR + binding.monthNp.getValue() + AppConstants.COMMA_SEPARATOR + binding.daysNp.getValue());
                mAlertDialogCallback.alertDialogCallback(TextAddressDialogCallback.OK);
                break;
            default:
                break;
        }
    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final TextAlertDialogCallback callback;
        private String months;
        private String years;
        private String days;

        public AlertDialogBuilder(Context context, TextAlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder years(String years) {
            this.years = years;
            return this;
        }

        public AlertDialogBuilder months(String months) {
            this.months = months;
            return this;
        }

        public AlertDialogBuilder days(String days) {
            this.days = days;
            return this;
        }

        //Return the finally constructed User object
        public WarratyDialog build() {
            WarratyDialog dialog = new WarratyDialog(this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }

    }
}
