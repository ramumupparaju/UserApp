package com.incon.connect.user.ui.warrantyregistration;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.search.Category;
import com.incon.connect.user.apimodel.components.search.Division;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppOtpDialog;
import com.incon.connect.user.custom.view.CustomAutoCompleteView;
import com.incon.connect.user.custom.view.CustomTextInputLayout;
import com.incon.connect.user.databinding.FragmentWarrantyRegistrationBinding;
import com.incon.connect.user.dto.warrantyregistration.WarrantyRegistration;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.addnewmodel.AddNewModelFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.qrcodescan.QrcodeBarcodeScanActivity;
import com.incon.connect.user.ui.warrantyregistration.adapter.ModelSearchArrayAdapter;
import com.incon.connect.user.utils.SharedPrefsUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

import static com.incon.connect.user.ui.BaseActivity.TRANSACTION_TYPE_REPLACE;

/**
 * Created by PC on 9/28/2017.
 */
public class WarrantyRegistrationFragment extends BaseFragment implements
        WarrantRegistrationContract.View, View.OnClickListener {

    private View rootView;
    private FragmentWarrantyRegistrationBinding binding;
    private WarrantRegistrationPresenter warrantRegistrationPresenter;
    private DisposableObserver<TextViewAfterTextChangeEvent> observer;
    private WarrantyRegistration warrantyRegistration;

    private ModelSearchArrayAdapter modelNumberAdapter;
    private List<ModelSearchResponse> modelSearchResponseList;
    private String selectedModelNumber;
    private int selectedPosition = -1;
    boolean isOtpVerified;
    private AppAlertDialog warrantyStatusDialog;
    private AppOtpDialog userOtpDialog;
    private String enteredOtp;
    private HashMap<Integer, String> errorMap;
    private Animation shakeAnim;
    private KeyListener listener;


    @Override
    protected void initializePresenter() {
        warrantRegistrationPresenter = new WarrantRegistrationPresenter();
        warrantRegistrationPresenter.setView(this);
        setBasePresenter(warrantRegistrationPresenter);
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.title_warranty_register));
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_warranty_registration, container, false);
            binding.setFragment(this);
            warrantyRegistration = getArguments().getParcelable(BundleConstants.WARRANTY_DATA);
            warrantyRegistration.setMerchantId(SharedPrefsUtils.loginProvider().
                    getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE));
            binding.setWarrantyRegistration(warrantyRegistration);
            rootView = binding.getRoot();

            initViews();
        }
        setTitle();
        return rootView;
    }

    private void initViews() {
        shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        listener = binding.edittextModelNumber.getKeyListener();
        if (warrantyRegistration.isFromProductScan()) {
            binding.edittextModelNumber.setKeyListener(null);
            showViews(true);
        } else {
            binding.edittextModelNumber.setKeyListener(listener);
            initializeModelNumberAdapter(new ArrayList<ModelSearchResponse>());
        }
        initializeModelNumberAdapter(new ArrayList<ModelSearchResponse>());
        loadValidationErrors();
        setFocusForViews();
        setDrawableClickEvents();
    }

    private void setDrawableClickEvents() {
        binding.edittextBatchNo.setOnTouchListener(onTouchListener);
        binding.edittextSerialNo.setOnTouchListener(onTouchListener);
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int drawableRight = 2;

            EditText editText = (EditText) v;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.
                        getCompoundDrawables()[drawableRight].getBounds().width())) {
                    // your action here
                    {
                        switch (v.getId()) {
                            case R.id.edittext_batch_no:
                                onClick(binding.edittextBatchNo);
                                break;
                            case R.id.edittext_serial_no:
                                onClick(binding.edittextSerialNo);
                                break;
                            default:
                                //Do nothing
                                break;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    };

    private void initializeModelNumberAdapter(List<ModelSearchResponse>
                                                      modelNumberList) {
        this.modelSearchResponseList = modelNumberList;
        modelNumberAdapter = new ModelSearchArrayAdapter(getContext(),
                modelNumberList);
        binding.edittextModelNumber.setAdapter(modelNumberAdapter);
        setObservableForModelNumber(binding.edittextModelNumber);

        binding.edittextModelNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                if (pos != selectedPosition) {
                    selectedPosition = pos;
                    ModelSearchResponse modelSearchResponse = modelSearchResponseList.get(pos);
                    loadWarrantyRegistrationDataFromModelResponse(modelSearchResponse);
                }
                AppUtils.hideSoftKeyboard(getActivity(), rootView);
            }
        });
    }

    private void loadWarrantyRegistrationDataFromModelResponse(
            ModelSearchResponse modelSearchResponse) {
        selectedModelNumber = modelSearchResponse.getModelNumber();
        showViews(true);
        binding.adNewModelButton.setVisibility(View.GONE);

        warrantyRegistration.setProductId(String.valueOf(modelSearchResponse.getId()));
        Category category = modelSearchResponse.getCategory();
        warrantyRegistration.setCategoryId(category.getId());
        warrantyRegistration.setCategoryName(category.getName());
        Division division = modelSearchResponse.getDivision();
        warrantyRegistration.setDivisionId(division.getId());
        warrantyRegistration.setDivisionName(division.getName());
        warrantyRegistration.setPrice(String.valueOf(modelSearchResponse.getPrice()));
        String information = modelSearchResponse.getInformation();
        warrantyRegistration.setDescription(information != null ? information : "");

    }

    private void showViews(boolean isShow) {
        binding.inputLayoutDescription.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.inputLayoutSerialNo.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.inputLayoutBatchNo.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.inputLayoutMrpPrice.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.inputLayoutPrice.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.inputLayoutInvoicenumber.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void setObservableForModelNumber(CustomAutoCompleteView edittextModelNumber) {
        if (observer != null) {
            observer.dispose();
        }
        observer = new DisposableObserver<TextViewAfterTextChangeEvent>() {

            @Override
            public void onNext(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                String modelNumberString = textViewAfterTextChangeEvent.editable()
                        .toString();
                if ((TextUtils.isEmpty(selectedModelNumber) || !selectedModelNumber.equals(
                        modelNumberString))) {
                    if (modelNumberString.length() > WarrantyRegistrationConstants
                            .MINIMUM_MODELNUMBER_TO_SEARCH) {
                        warrantRegistrationPresenter.doModelSearchApi(modelNumberString);
                        selectedModelNumber = modelNumberString;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        RxTextView.afterTextChangeEvents(edittextModelNumber)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void loadModelNumberData(List<ModelSearchResponse> modelSearchResponseList) {
        if (modelSearchResponseList == null) {
            modelSearchResponseList = new ArrayList<>();
        }
        initializeModelNumberAdapter(modelSearchResponseList);
        binding.edittextModelNumber.showDropDown();
        if (modelSearchResponseList.size() == 0) {
            showErrorMessage(getString(R.string.error_model_message));
        }
    }

    @Override
    public void warrantyRegistered(Object warrantyRegisteredResponse) {
        showWarrantySuccessfulRegistionDialog();
    }

    @Override
    public void validateUserOTP() {
        dismissDialog(userOtpDialog);
        isOtpVerified = true;
        callWarrantyApi();
    }

    public void onNewModelClick() {
        ((HomeActivity) getActivity()).replaceFragmentAndAddToStackWithTargetFragment(
                AddNewModelFragment.class, this, RequestCodes.ADD_NEW_MODEL,
                null, 0, 0, TRANSACTION_TYPE_REPLACE);
    }

    public void onSubmitClick() {
        if (validateFields()) {
            if (isOtpVerified) {
                callWarrantyApi();
            } else {
                showOtpDialog();
            }
        }
    }

    private void callWarrantyApi() {
        if (binding.productStatus.isChecked()) {
            warrantyRegistration.setStatus(WarrantyRegistrationConstants.STATUS_PRODUCT_DELIVERED);
        } else {
            warrantyRegistration.setStatus(
                    WarrantyRegistrationConstants.STATUS_PRODUCT_NOT_DELIVERED);
        }
        warrantRegistrationPresenter.doWarrantyRegistrationApi(warrantyRegistration);
    }

    private void showOtpDialog() {
        final String phoneNumber = warrantyRegistration.getMobileNumber();
        userOtpDialog = new AppOtpDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String otpString) {
                        enteredOtp = otpString;
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        AppUtils.hideSoftKeyboard(getActivity(), rootView);
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                if (TextUtils.isEmpty(enteredOtp)) {
                                    showErrorMessage(getString(R.string.error_otp_req));
                                    return;
                                }
                                HashMap<String, String> verifyOTP = new HashMap<>();
                                verifyOTP.put(ApiRequestKeyConstants.BODY_MOBILE_NUMBER,
                                        phoneNumber);
                                verifyOTP.put(ApiRequestKeyConstants.BODY_OTP, enteredOtp);
                                warrantRegistrationPresenter.validateUserOTP(verifyOTP);
                                break;
                            case AlertDialogCallback.CANCEL:
                                userOtpDialog.dismiss();
                                break;
                            case TextAlertDialogCallback.RESEND_OTP:
                                warrantRegistrationPresenter.resendUserOTP(phoneNumber);
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.dialog_verify_title, phoneNumber))
                .build();
        userOtpDialog.showDialog();
    }

    private void showWarrantySuccessfulRegistionDialog() {
        warrantyStatusDialog = new AppAlertDialog.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                            case AlertDialogCallback.CANCEL:
                                getActivity().onBackPressed();
                                warrantyStatusDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.dialog_warranty_registered_successfully))
                .button1Text(getString(R.string.action_ok))
                .build();
        warrantyStatusDialog.showDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edittext_serial_no: {
                Intent intent = new Intent(getActivity(), QrcodeBarcodeScanActivity.class);
                intent.putExtra(IntentConstants.SCANNED_TITLE, getString(
                        R.string.title_warranty_serial_barcode));
                startActivityForResult(intent, RequestCodes.SERIAL_NO_SCAN);
            }
            break;
            case R.id.edittext_batch_no: {
                Intent intent = new Intent(getActivity(), QrcodeBarcodeScanActivity.class);
                intent.putExtra(IntentConstants.SCANNED_TITLE, getString(
                        R.string.title_warranty_batch_barcode));
                startActivityForResult(intent, RequestCodes.BATCH_NO_SCAN);
            }
            break;
            default:
                // do nothing
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.SERIAL_NO_SCAN:
                    if (data != null) {
                        binding.edittextSerialNo.setText(
                                data.getStringExtra(IntentConstants.SCANNED_CODE));
                    }
                    break;
                case RequestCodes.BATCH_NO_SCAN:
                    if (data != null) {
                        binding.edittextBatchNo.setText(
                                data.getStringExtra(IntentConstants.SCANNED_CODE));
                    }
                    break;
                case RequestCodes.ADD_NEW_MODEL:
                    if (data != null) {
                        ModelSearchResponse modelSearchResponse = data.getParcelableExtra(
                                IntentConstants.MODEL_SEARCH_RESPONSE);
                        selectedModelNumber = modelSearchResponse.getModelNumber();
                        ArrayList<ModelSearchResponse> modelSearchResponses = new ArrayList<>();
                        modelSearchResponses.add(modelSearchResponse);
                        initializeModelNumberAdapter(modelSearchResponses);
                        binding.edittextModelNumber.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.edittextModelNumber.setText(selectedModelNumber);
                                loadWarrantyRegistrationDataFromModelResponse(
                                        modelSearchResponseList.get(0));
                            }
                        }, 500);

                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void loadValidationErrors() {
        errorMap = new HashMap<>();
        errorMap.put(WarrantyregistationValidation.MODEL, getString(R.string.error_product_model));
        errorMap.put(WarrantyregistationValidation.INVALID_MODEL,
                getString(R.string.error_product_invalid_model));
        errorMap.put(WarrantyregistationValidation.DESCRIPTION, getString(
                R.string.error_product_description));
        errorMap.put(WarrantyregistationValidation.SERIAL_NO, getString(
                R.string.error_product_serial));
        errorMap.put(WarrantyregistationValidation.BATCH_NO, getString(
                R.string.error_product_batch));
        errorMap.put(WarrantyregistationValidation.MRP_PRICE, getString(
                R.string.error_product_mrp_price));
        errorMap.put(WarrantyregistationValidation.PRICE, getString(
                R.string.error_product_price));
        errorMap.put(WarrantyregistationValidation.INVOICENUMBER, getString(R.string
                .error_product_invoice_number));

    }

    private void setFocusForViews() {
        binding.edittextModelNumber.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextDescription.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextSerialNo.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextBatchNo.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextMrpPrice.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextPrice.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextInvoicenumber.setOnFocusChangeListener(onFocusChangeListener);
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            Object fieldId = view.getTag();
            if (fieldId != null) {
                Pair<String, Integer> validation = binding.getWarrantyRegistration().
                        validateWarrantyRegistration((String) fieldId);
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


    private boolean validateFields() {
        binding.inputLayoutModelNumber.setError(null);
        binding.inputLayoutDescription.setError(null);
        binding.inputLayoutSerialNo.setError(null);
        binding.inputLayoutBatchNo.setError(null);
        binding.inputLayoutMrpPrice.setError(null);
        binding.inputLayoutPrice.setError(null);
        binding.inputLayoutInvoicenumber.setError(null);

        Pair<String, Integer> validation = binding.getWarrantyRegistration().
                validateWarrantyRegistration(null);
        updateUiAfterValidation(validation.first, validation.second);

        return validation.second == VALIDATION_SUCCESS;
    }

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
        }

        if (validationId != VALIDATION_SUCCESS) {
            view.startAnimation(shakeAnim);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog(warrantyStatusDialog);
        dismissDialog(userOtpDialog);
        if (observer != null) {
            observer.dispose();
        }
        warrantRegistrationPresenter.disposeAll();
    }
}
