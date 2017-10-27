package com.incon.connect.user.ui.home.asignqrcode.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.incon.connect.user.R;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.custom.view.CustomAutoCompleteView;
import com.incon.connect.user.custom.view.CustomTextInputLayout;
import com.incon.connect.user.databinding.FragmentProductAssignBinding;
import com.incon.connect.user.dto.asignqrcode.AssignQrCode;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.addnewmodel.AddNewModelFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.warrantyregistration.adapter.ModelSearchArrayAdapter;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/6/2017.
 */
public class ProductAssignFragment extends BaseFragment implements ProductAssignContract.View {

    private View rootView;
    private FragmentProductAssignBinding binding;
    private ProductAssignPresenter assignPresenter;
    private AssignQrCode assignQrCode;
    private List<ModelSearchResponse> modelSearchResponseList;
    private ModelSearchArrayAdapter modelNumberAdapter;
    private int selectedPosition;
    private DisposableObserver<TextViewAfterTextChangeEvent> observer;
    private String selectedModelNumber;
    private HashMap<Integer, String> errorMap;
    private Animation shakeAnim;

    @Override
    protected void initializePresenter() {
        assignPresenter = new ProductAssignPresenter();
        assignPresenter.setView(this);
        setBasePresenter(assignPresenter);
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.
                progress_qr_code_product));
    }

    public void onSubmitClick() {
        if (validateFields()) {
            assignPresenter.assignQrCodeToProduct(assignQrCode);
        }
    }

    public void onNewModelClick() {
        ((HomeActivity) getActivity()).replaceFragmentAndAddToStack(
                AddNewModelFragment.class, null);
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            // handle events from here using android binding
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_assign,
                    container, false);
            assignQrCode = new AssignQrCode();
            binding.setAssignQrCode(assignQrCode);
            binding.setProductassignfragment(this);

            rootView = binding.getRoot();
            initViews();
        }
        setTitle();
        return rootView;
    }

    private void initViews() {
        shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        assignQrCode.setCode(getArguments().getString(BundleConstants.SCANNED_QRCODE));
        initializeModelNumberAdapter(new ArrayList<ModelSearchResponse>());
        loadValidationErrors();
        setFocusForViews();
    }

    private void initializeModelNumberAdapter(List<ModelSearchResponse> modelNumberList) {
        this.modelSearchResponseList = modelNumberList;
        modelNumberAdapter = new ModelSearchArrayAdapter(getContext(),
                modelNumberList);
        binding.edittextModelNumber.setAdapter(modelNumberAdapter);
        setObservableForModelNumber(binding.edittextModelNumber);
        binding.edittextModelNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                selectedPosition = pos;
                ModelSearchResponse modelSearchResponse = modelSearchResponseList.get(pos);
                assignQrCode.setProductId(String.valueOf(modelSearchResponse.getId()));
                selectedModelNumber = modelSearchResponseList.get(
                        selectedPosition).getModelNumber();
                assignQrCode.setDescription(modelSearchResponse.getInformation());
                binding.adNewModelButton.setVisibility(View.GONE);
            }
        });

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
                        assignPresenter.doModelSearchApi(modelNumberString);
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
    public void productAssignQrCode(Object assignQrCodeResponse) {
        AppUtils.shortToast(getContext(), getString(R.string.hint_product_assigned_success));
        getActivity().onBackPressed();
    }

    private void loadValidationErrors() {
        errorMap = new HashMap<>();
        errorMap.put(ProductAssignValidation.MODEL, getString(R.string.error_product_model));
        errorMap.put(ProductAssignValidation.INVALID_MODEL,
                getString(R.string.error_product_invalid_model));
        errorMap.put(ProductAssignValidation.DESCRIPTION, getString(
                R.string.error_product_description));
        errorMap.put(ProductAssignValidation.MRP_PRICE, getString(
                R.string.error_product_mrp_price));
        errorMap.put(ProductAssignValidation.PRICE, getString(R.string.error_product_price));

    }

    private void setFocusForViews() {
        binding.edittextModelNumber.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextModelPrice.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextDescription.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextMrpPrice.setOnFocusChangeListener(onFocusChangeListener);
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            Object fieldId = view.getTag();
            if (fieldId != null) {
                Pair<String, Integer> validation = binding.getAssignQrCode().
                        validateProductModel((String) fieldId);
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
        binding.inputLayoutModelPrice.setError(null);
        binding.inputLayoutMrpPrice.setError(null);

        Pair<String, Integer> validation = binding.getAssignQrCode().validateProductModel(null);
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
        } else if (view instanceof CustomAutoCompleteView) {
            ((CustomTextInputLayout) view.getParent().getParent())
                    .setError(validationId == VALIDATION_SUCCESS ? null
                            : errorMap.get(validationId));
        }

        if (validationId != VALIDATION_SUCCESS) {
            view.startAnimation(shakeAnim);
        }
    }

}
