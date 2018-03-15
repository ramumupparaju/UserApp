package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TimeSlotAlertDialogCallback;
import com.incon.connect.user.databinding.DialogTimeSlotBinding;
import com.incon.connect.user.utils.DateUtils;

public class TimeSlotAlertDialog extends Dialog implements View.OnClickListener {
    private DialogTimeSlotBinding binding;

    private final Context context;
    //All final attributes
    private final TimeSlotAlertDialogCallback mAlertDialogCallback; // required
    private String selectedDate;
    private String selectedTime;

    /**
     * @param builder
     */
    private TimeSlotAlertDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.selectedDate = builder.selectedDate;
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

        enableOrdisableButtons();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }

    private void enableOrdisableButtons() {
        LinearLayout buttonsLayout = binding.buttonsLayout;
        if (TextUtils.isEmpty(selectedDate)) {
            for (int i = 0; i < buttonsLayout.getChildCount(); i++) {
                Button childAt = (Button) buttonsLayout.getChildAt(i);
                int startTime = Integer.parseInt(childAt.getText().toString().split(AppConstants.HYPHEN_SEPARATOR)[0]);
                int currentTime = Integer.parseInt(DateUtils.convertMillisToStringFormat(System.currentTimeMillis(), AppConstants.DateFormatterConstants.HH));
                if (startTime <= currentTime) {
                    childAt.setActivated(true);
                    childAt.setEnabled(false);
                }
            }
        } else {
            //have to check whether selected date is today we have to diasable time based on current time if not nothing have to do just break
            String dateInDD_MM_YYYY = DateUtils.convertMillisToStringFormat(System.currentTimeMillis(), AppConstants.DateFormatterConstants.DD_MM_YYYY);
            if (dateInDD_MM_YYYY.equals(selectedDate)){
                selectedDate = "";
                enableOrdisableButtons();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mAlertDialogCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.dialog_submit_button:
                mAlertDialogCallback.selectedTimeSlot(selectedTime);
                mAlertDialogCallback.alertDialogCallback(AlertDialogCallback.OK);
                break;
            default:
                if (view.isSelected()) { //checking whether view is selected or not
                    view.setSelected(false);
                    selectedTime = "";
                } else {
                    LinearLayout buttonsLayout = binding.buttonsLayout;
                    for (int i = 0; i < buttonsLayout.getChildCount(); i++) {
                        View childAt = buttonsLayout.getChildAt(i);
                        childAt.setSelected(view.getId() == childAt.getId() ? true : false);
                        if (childAt.isSelected()) {
                            selectedTime = ((TextView) childAt).getText().toString();
                        }
                    }
                }
                break;
        }
    }

    public static class AlertDialogBuilder {

        private final Context context;
        private final String selectedDate;
        private final TimeSlotAlertDialogCallback callback;

        public AlertDialogBuilder(Context context, String selectedDate, TimeSlotAlertDialogCallback callback) {
            this.context = context;
            this.selectedDate = selectedDate;
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
