package com.incon.connect.user.custom.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.CustomAddOptionsCallback;
import com.incon.connect.user.databinding.CustomAddOptionsBinding;
import com.incon.connect.user.utils.DateUtils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by PC on 12/26/2017.
 */

public class CustomAddOptionsDialog extends Dialog implements View.OnClickListener {
    private CustomAddOptionsBinding binding;
    private final Context context;
    private final CustomAddOptionsCallback customAddOptionsCallback;
    private final String title; // required
    private final String leftButtonText; // required
    private final String rightButtonText; // required
    private EditText editTextNotes;
    private String[] optionsArray;
    public CustomAddOptionsDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.customAddOptionsCallback = builder.callback;
        this.leftButtonText = builder.leftButtonText;
        this.rightButtonText = builder.rightButtonText;
    }




    public void showDialog() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.custom_add_options, null, false);
        View contentView = binding.getRoot();
        editTextNotes = binding.edittextComment;
        binding.includeRegisterBottomButtons.buttonLeft.setText(
                context.getString(R.string.action_cancel));
        binding.includeRegisterBottomButtons.buttonRight.setText(
                context.getString(R.string.action_submit));
        binding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        binding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);
        loadAddOptionsSpinner();
        loadDateAndTimePicker();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }



    private void loadDateAndTimePicker() {
        //AppUtils.hideSoftKeyboard( getContext(),getView() );
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
      /*  String dateOfBirth = register.getDateOfBirthToShow();
        if (!TextUtils.isEmpty(dateOfBirth)) {
            cal.setTimeInMillis(DateUtils.convertStringFormatToMillis(
                    dateOfBirth, AppConstants.DateFormatterConstants.MM_DD_YYYY));
        }*/

        int customStyle = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                ? R.style.DatePickerDialogTheme : android.R.style.Theme_DeviceDefault_Light_Dialog;
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                customStyle,
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.show();

    }

    // date Listener
    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {
                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay);

                    String dobInMMDDYYYY = DateUtils.convertDateToOtherFormat(
                            selectedDateTime.getTime(), AppConstants.DateFormatterConstants.MM_DD_YYYY);
                   // register.setDateOfBirthToShow(dobInMMDDYYYY);

                    /*Pair<String, Integer> validate = binding.getRegister().
                            validateUserInfo((String) binding.edittextRegisterDob.getTag());
                    updateUiAfterValidation(validate.first, validate.second);*/
                }
            };

    private void loadAddOptionsSpinner() {
        Context context = binding.getRoot().getContext();
        optionsArray = context.getResources().getStringArray(R.array.add_options_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                R.layout.view_spinner, optionsArray);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerAddOptions.setAdapter(arrayAdapter);
        binding.spinnerAddOptions.setText(optionsArray[0]);

    }
    public static class AlertDialogBuilder {
        private final Context context;
        private final CustomAddOptionsCallback callback;
        private String title;
        private String leftButtonText;
        private String rightButtonText;


        public AlertDialogBuilder(Context context, CustomAddOptionsCallback callback) {
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

        public  CustomAddOptionsDialog build() {
            CustomAddOptionsDialog dialog = new CustomAddOptionsDialog(this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }

    }


    @Override
    public void onClick(View view) {
        if (customAddOptionsCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.button_left:
                customAddOptionsCallback.alertDialogCallback(CustomAddOptionsCallback.CANCEL);
                break;
            case R.id.button_right:
                customAddOptionsCallback.alertDialogCallback(CustomAddOptionsCallback.OK);
                break;
            default:
                break;
        }

    }
}
