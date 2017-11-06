package com.incon.connect.user.ui.register.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppOtpDialog;
import com.incon.connect.user.custom.view.CustomTextInputLayout;
import com.incon.connect.user.databinding.FragmentRegistrationUserBinding;
import com.incon.connect.user.dto.registration.Registration;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.notifications.PushPresenter;
import com.incon.connect.user.ui.register.RegistrationActivity;
import com.incon.connect.user.ui.termsandcondition.TermsAndConditionActivity;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;


/**
 * Created on 13 Jun 2017 4:01 PM.
 */
public class RegistrationUserFragment extends BaseFragment implements
        RegistrationUserFragmentContract.View {

    private RegistrationUserFragmentPresenter registrationUserInfoFragPresenter;
    private FragmentRegistrationUserBinding binding;
    private Registration register; // initialized from registration acticity
    private Animation shakeAnim;
    private HashMap<Integer, String> errorMap;
    private MaterialBetterSpinner genderSpinner;
    private AppOtpDialog dialog;
    private String enteredOtp;

    @Override
    protected void initializePresenter() {

        registrationUserInfoFragPresenter = new RegistrationUserFragmentPresenter();
        registrationUserInfoFragPresenter.setView(this);
        setBasePresenter(registrationUserInfoFragPresenter);
    }

    @Override
    public void setTitle() {
        // do nothing
    }
    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_registration_user, container, false);
        binding.setUserFragment(this);
        //here data must be an instance of the registration class
        register = ((RegistrationActivity) getActivity()).getRegistration();
        binding.setRegister(register);
        View rootView = binding.getRoot();

        loadData();
        setTitle();
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.TERMS_AND_CONDITIONS:
                   callRegisterApi();
                    break;
                case RequestCodes.ADDRESS_LOCATION:
                    register.setAddress(data.getStringExtra(IntentConstants.ADDRESS_COMMA));
                    register.setLocation(data.getStringExtra(IntentConstants.LOCATION_COMMA));
                    binding.setRegister(register);
                    break;
                default:
                    break;
            }
        }
    }

    private void callRegisterApi() {

        register.setGender(String.valueOf(register.getGender().charAt(0)));
        registrationUserInfoFragPresenter.register(register);
    }
    private void loadData() {
        shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        loadGenderSpinnerData();
        loadValidationErrors();
        setFocusListenersForEditText();
    }
    public void onAddressClick() {
        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        startActivityForResult(addressIntent, RequestCodes.ADDRESS_LOCATION);
    }

    public void onDobClick() {
        showDatePicker();
    }

    private void showDatePicker() {
        AppUtils.hideSoftKeyboard(getActivity(), getView());
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        String dateOfBirth = register.getDateOfBirthToShow();
        if (!TextUtils.isEmpty(dateOfBirth)) {
            cal.setTimeInMillis(DateUtils.convertStringFormatToMillis(
                    dateOfBirth, DateFormatterConstants.MM_DD_YYYY));
        }

        int customStyle = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                ? R.style.DatePickerDialogTheme : android.R.style.Theme_DeviceDefault_Light_Dialog;
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                customStyle,
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.show();
    }
    // Date Listener
    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {
                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay);

                    String dobInMMDDYYYY = DateUtils.convertDateToOtherFormat(
                            selectedDateTime.getTime(), DateFormatterConstants.MM_DD_YYYY);
                    register.setDateOfBirthToShow(dobInMMDDYYYY);

                    Pair<String, Integer> validate = binding.getRegister().
                            validateUserInfo((String) binding.edittextRegisterDob.getTag());
                    updateUiAfterValidation(validate.first, validate.second);
                }
            };

    void loadGenderSpinnerData() {
        String[] genderTypeList = getResources().getStringArray(R.array.gender_options_list);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.view_spinner, genderTypeList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        genderSpinner = binding.spinnerGender;
        genderSpinner.setAdapter(arrayAdapter);

    }
    private void loadValidationErrors() {

        errorMap = new HashMap<>();
        errorMap.put(RegistrationValidation.NAME_REQ,
                getString(R.string.error_name_req));

        errorMap.put(RegistrationValidation.PHONE_REQ,
                getString(R.string.error_phone_req));

        errorMap.put(RegistrationValidation.PHONE_MIN_DIGITS,
                getString(R.string.error_phone_min_digits));

        errorMap.put(RegistrationValidation.GENDER_REQ,
                getString(R.string.error_gender_req));

        errorMap.put(RegistrationValidation.DOB_REQ,
                getString(R.string.error_dob_req));

        errorMap.put(RegistrationValidation.DOB_FUTURE_DATE,
                getString(R.string.error_dob_futuredate));

        errorMap.put(RegistrationValidation.DOB_PERSON_LIMIT,
                getString(R.string.error_dob_patient_is_user));

        errorMap.put(RegistrationValidation.EMAIL_REQ,
                getString(R.string.error_email_req));

        errorMap.put(RegistrationValidation.EMAIL_NOTVALID,
                getString(R.string.error_email_notvalid));

        errorMap.put(RegistrationValidation.PASSWORD_REQ,
                getString(R.string.error_password_req));

        errorMap.put(RegistrationValidation.PASSWORD_PATTERN_REQ,
                getString(R.string.error_password_pattern_req));

        errorMap.put(RegistrationValidation.RE_ENTER_PASSWORD_REQ,
                getString(R.string.error_re_enter_password_req));

        errorMap.put(RegistrationValidation.RE_ENTER_PASSWORD_DOES_NOT_MATCH,
                getString(R.string.error_re_enter_password_does_not_match));

        errorMap.put(RegistrationValidation.ADDRESS_REQ, getString(R.string.error_address_req));
    }
    private void setFocusListenersForEditText() {

        TextView.OnEditorActionListener onEditorActionListener =
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            switch (textView.getId()) {
                                case R.id.edittext_register_phone:
                                    binding.spinnerGender.requestFocus();
                                    binding.spinnerGender.showDropDown();
                                    break;

                                default:
                            }
                        }
                        return true;
                    }
                };

        binding.edittextRegisterPhone.setOnEditorActionListener(onEditorActionListener);
        binding.edittextRegisterUserName.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextRegisterPhone.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextRegisterEmailid.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextRegisterPassword.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextRegisterReenterPassword.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextRegisterAddress.setOnFocusChangeListener(onFocusChangeListener);
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            Object fieldId = view.getTag();
            if (fieldId != null) {
                Pair<String, Integer> validation = register.
                        validateUserInfo((String) fieldId);
                if (!hasFocus) {
                    if (view instanceof TextInputEditText) {
                        TextInputEditText textInputEditText = (TextInputEditText) view;
                        textInputEditText.setText(textInputEditText.getText().toString().trim());
                    }
                    updateUiAfterValidation(validation.first, validation.second);
                }
            }
        }
    };

    private void updateUiAfterValidation(String tag, int validationId) {

        if (tag == null) {
            return;
        }

        View viewByTag = binding.getRoot().findViewWithTag(tag);
        setFieldError(viewByTag, validationId);
    }
    private void setFieldError(View view, int validationId) {

        if (view instanceof TextInputEditText) {
            ((CustomTextInputLayout) view.getParent().getParent())
                    .setError(validationId == VALIDATION_SUCCESS ? null
                            : errorMap.get(validationId));
        } else {
            ((MaterialBetterSpinner) view).setError(validationId == VALIDATION_SUCCESS ? null
                    : errorMap.get(validationId));
        }

        if (validationId != VALIDATION_SUCCESS) {
            view.startAnimation(shakeAnim);
            ((RegistrationActivity) getActivity()).focusOnView(binding.scrollviewUserInfo, view);
        }
    }
    /**
     * called from registration activity when user click on next in bottom bars
     * validate user , if all fields ok then call next screen
     */
    public void onClickNext() {
        if (validateFields()) {
            navigateToRegistrationActivityNext();
        }
    }

    private boolean validateFields() {
        binding.inputLayoutRegisterUserName.setError(null);
        binding.inputLayoutRegisterPhone.setError(null);
        binding.spinnerGender.setError(null);
        binding.inputLayoutRegisterEmailid.setError(null);
        binding.inputLayoutRegisterPassword.setError(null);
        binding.inputLayoutRegisterConfirmPassword.setError(null);
        binding.inputLayoutRegisterAddress.setError(null);

        Pair<String, Integer> validation = binding.getRegister().
                validateUserInfo(null);
        updateUiAfterValidation(validation.first, validation.second);

        return validation.second == VALIDATION_SUCCESS;
    }
    @Override
    public void navigateToRegistrationActivityNext() {
       // ((RegistrationActivity) getActivity()).navigateToNext();
        Intent eulaIntent = new Intent(getActivity(), TermsAndConditionActivity.class);
        startActivityForResult(eulaIntent, RequestCodes.TERMS_AND_CONDITIONS);
    }
    @Override
    public void navigateToHomeScreen() {
        PushPresenter pushPresenter = new PushPresenter();
        pushPresenter.pushRegisterApi();

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent(getActivity(),
                HomeActivity.class);
        // This is a convenient way to make the proper Intent to launch and
        // reset an application's task.
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        startActivity(mainIntent);
    }

    @Override
    public void validateOTP() {
        SharedPrefsUtils.loginProvider().setBooleanPreference(LoginPrefs.IS_REGISTERED, true);
        SharedPrefsUtils.loginProvider().setStringPreference(LoginPrefs.USER_MOBILE_NUMBER,
                register.getMobileNumber());
        showOtpDialog();
    }

    private void showOtpDialog() {
        final String mobileNumber = register.getMobileNumber();
        dialog = new AppOtpDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String otpString) {
                        enteredOtp = otpString;
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                if (TextUtils.isEmpty(enteredOtp)) {
                                    showErrorMessage(getString(R.string.error_otp_req));
                                    return;
                                }
                                HashMap<String, String> verifyOTP = new HashMap<>();
                                verifyOTP.put(ApiRequestKeyConstants.BODY_MOBILE_NUMBER,
                                        mobileNumber);
                                verifyOTP.put(ApiRequestKeyConstants.BODY_OTP, enteredOtp);
                                registrationUserInfoFragPresenter.validateOTP(verifyOTP);
                                break;
                            case AlertDialogCallback.CANCEL:
                                dialog.dismiss();
                                break;
                            case TextAlertDialogCallback.RESEND_OTP:
                                registrationUserInfoFragPresenter.registerRequestOtp(mobileNumber);
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.dialog_verify_title, mobileNumber))
                .build();
        dialog.showDialog();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        registrationUserInfoFragPresenter.disposeAll();
    }

}
