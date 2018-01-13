package com.incon.connect.user.ui.addnewmodel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.defaults.CategoryResponse;
import com.incon.connect.user.apimodel.components.defaults.DefaultsResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.Brand;
import com.incon.connect.user.apimodel.components.fetchcategorie.Division;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.custom.view.CustomAutoCompleteView;
import com.incon.connect.user.custom.view.CustomTextInputLayout;
import com.incon.connect.user.databinding.FragmentAddCustomProductBinding;
import com.incon.connect.user.dto.addnewmodel.AddCustomProductModel;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.addnewmodel.adapter.ModelSearchArrayAdapter;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/4/2017.
 */

public class AddCustomProductFragment extends BaseFragment implements AddCustomProductContract.View {
    private FragmentAddCustomProductBinding binding;
    private View rootView;
    private AddCustomProductPresenter addCustomProductPresenter;
    private AddCustomProductModel addCustomProductModel;

    private List<CategoryResponse> fetchCategorieList;
    private List<Division> fetchDivisionsList;
    private List<Brand> fetchBrandsList;


    private int categorySelectedPos = -1;
    private int divisionSelectedPos = -1;
    private int brandSelectedPos = -1;


    private DisposableObserver<TextViewAfterTextChangeEvent> observer;
    private ModelSearchArrayAdapter modelNumberAdapter;
    private List<ModelSearchResponse> modelSearchResponseList;
    private String selectedModelNumber;
    private int selectedPosition = -1;

    private HashMap<Integer, String> errorMap;
    private Animation shakeAnim;

    @Override
    protected void initializePresenter() {
        addCustomProductPresenter = new AddCustomProductPresenter();
        addCustomProductPresenter.setView(this);
        setBasePresenter(addCustomProductPresenter);
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.title_custom_product));
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_add_custom_product, container, false);
            addCustomProductModel = new AddCustomProductModel();
            binding.setAddCustomProductModel(addCustomProductModel);
            binding.setAddCustomProductFragment(this);
            rootView = binding.getRoot();
            initViews();

            addCustomProductPresenter.getCategories();

        }
        setTitle();
        return rootView;
    }

    public void onPurchasedDateClick() {
        showDatePicker();
    }

    private void showDatePicker() {
        AppUtils.hideSoftKeyboard(getActivity(), getView());
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        // todo have to change
        String dateOfPurchased = addCustomProductModel.getDateOfPurchased();
        if (!TextUtils.isEmpty(dateOfPurchased)) {
            cal.setTimeInMillis(DateUtils.convertStringFormatToMillis(
                    dateOfPurchased, DateFormatterConstants.MM_DD_YYYY));
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
                    //   addCustomProductModel.setDateOfBirthToShow(dobInMMDDYYYY);

                    Pair<String, Integer> validate = addCustomProductModel.
                            validateAddNewModel((String) binding.edittextPurchasedDate.getTag());
                    updateUiAfterValidation(validate.first, validate.second);
                }
            };

    // category spinner
    private void loadCategorySpinnerData() {
        String[] stringCategoryList = new String[fetchCategorieList.size()];
        for (int i = 0; i < fetchCategorieList.size(); i++) {
            stringCategoryList[i] = fetchCategorieList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.view_spinner, stringCategoryList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerCategory.setAdapter(arrayAdapter);
        binding.spinnerCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (categorySelectedPos != position) {

                    CategoryResponse fetchCategories = fetchCategorieList.get(position);
                    addCustomProductModel.setCategoryId(fetchCategories.getId());
                    addCustomProductModel.setCategoryName(fetchCategories.getName());

                    //TODO division api call
                    divisionSelectedPos = -1;
                    fetchDivisionsList.clear();
                    loadDivisionSpinnerData(fetchDivisionsList);
                    binding.spinnerDivision.setText("");


                    brandSelectedPos = -1;

                    categorySelectedPos = position;
                    binding.spinnerBrand.setVisibility(View.GONE);
                }
                //For avoiding double tapping issue
                if (binding.spinnerCategory.getOnItemClickListener() != null) {
                    binding.spinnerCategory.onItemClick(parent, view, position, id);
                }

            }
        });
    }

    // division spinner
    private void loadDivisionSpinnerData(List<Division> divisions) {

        if (divisions.size() == 0) {
            binding.spinnerDivision.setVisibility(View.GONE);
            return;
        }

        binding.spinnerDivision.setVisibility(View.VISIBLE);
        String[] stringDivisionList = new String[divisions.size()];
        for (int i = 0; i < divisions.size(); i++) {
            stringDivisionList[i] = divisions.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.view_spinner, stringDivisionList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerDivision.setAdapter(arrayAdapter);
        binding.spinnerDivision.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (divisionSelectedPos != position) {
                    divisionSelectedPos = position;
                    Division division = fetchDivisionsList.get(divisionSelectedPos);
                    addCustomProductModel.setDivisionId(division.getId());
                    addCustomProductModel.setDivisionName(division.getName());

                    //TODO get brand api
                    brandSelectedPos = -1;
                    loadBrandSpinnerData(fetchBrandsList);
                    binding.spinnerBrand.setText("");
                }
                //For avoiding double tapping issue
                if (binding.spinnerDivision.getOnItemClickListener() != null) {
                    binding.spinnerDivision.onItemClick(parent, view, position, id);
                }
            }
        });
    }

    private void loadBrandSpinnerData(final List<Brand> brandList) {

        if (brandList.size() == 0) {
            binding.spinnerBrand.setVisibility(View.GONE);
            return;
        }
        binding.spinnerBrand.setVisibility(View.VISIBLE);
        String[] stringDivisionList = new String[brandList.size()];
        for (int i = 0; i < brandList.size(); i++) {
            stringDivisionList[i] = brandList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.view_spinner, stringDivisionList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerBrand.setAdapter(arrayAdapter);
        binding.spinnerBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addCustomProductModel.setBrandId(brandList.get(position).getId());
                addCustomProductModel.setBrandName(brandList.get(position).getName());
                //For avoiding double tapping issue
                if (binding.spinnerBrand.getOnItemClickListener() != null) {
                    binding.spinnerBrand.onItemClick(parent, view, position, id);
                }
            }
        });
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

    private void initializeModelNumberAdapter(List<ModelSearchResponse>
                                                      modelNumberList) {
        selectedPosition = -1;
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
                    ModelSearchResponse modelSearchResponse = modelSearchResponseList.get(selectedPosition);
                    selectedModelNumber = modelSearchResponse.getModelNumber();
                }
                AppUtils.hideSoftKeyboard(getActivity(), rootView);
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
                        addCustomProductPresenter.doModelSearchApi(modelNumberString);
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

    private void initViews() {
        shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        loadValidationErrors();
        setFocusForViews();

        selectedModelNumber = binding.edittextModelNumber.getText().toString();
        initializeModelNumberAdapter(new ArrayList<ModelSearchResponse>());
    }

    private void setFocusForViews() {
        binding.edittextModelNumber.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextName.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextPrice.setOnFocusChangeListener(onFocusChangeListener);


    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            Object fieldId = view.getTag();
            if (fieldId != null) {
                Pair<String, Integer> validation = addCustomProductModel.
                        validateAddNewModel((String) fieldId);
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
        binding.inputLayoutName.setError(null);
        binding.spinnerCategory.setError(null);
        binding.spinnerDivision.setError(null);
        binding.spinnerBrand.setError(null);
        binding.inputLayoutPrice.setError(null);

        Pair<String, Integer> validation = addCustomProductModel.validateAddNewModel(null);
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

    private void loadValidationErrors() {
        errorMap = new HashMap<>();
        errorMap.put(AddNewModelValidation.MODEL, getString(R.string.error_product_model));
        errorMap.put(AddNewModelValidation.NAME, getString(
                R.string.error_product_name));
        errorMap.put(AddNewModelValidation.CATEGORY, getString(R.string.error_product_category));
        errorMap.put(AddNewModelValidation.DIVISION, getString(R.string.error_product_division));
        errorMap.put(AddNewModelValidation.BRAND, getString(R.string.error_product_brand));
        errorMap.put(AddNewModelValidation.MRP_PRICE, getString(R.string.error_product_mrp_price));
        errorMap.put(AddNewModelValidation.PRICE, getString(R.string.error_product_price));
        errorMap.put(AddNewModelValidation.NOTE, getString(R.string.error_product_notes));


    }

    public void onSubmitClick() {
        if (validateFields()) {
            addCustomProductPresenter.addingNewModel(SharedPrefsUtils.loginProvider().
                    getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE), addCustomProductModel);
        }
    }

    @Override
    public void addNewModel(ModelSearchResponse modelSearchResponse) {

        Intent intent = new Intent();
        intent.putExtra(IntentConstants.MODEL_SEARCH_RESPONSE, modelSearchResponse);
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(),
                    Activity.RESULT_OK, intent);
        }
        getActivity().onBackPressed();
    }

    @Override
    public void loadCategoriesList() {
        fetchCategorieList = ConnectApplication.getAppContext().getCategoriesList();
        loadCategorySpinnerData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addCustomProductPresenter.disposeAll();
    }
}
