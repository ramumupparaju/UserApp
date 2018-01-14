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
import com.incon.connect.user.apimodel.components.fetchcategorie.Brand;
import com.incon.connect.user.apimodel.components.search.Category;
import com.incon.connect.user.apimodel.components.search.Division;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.custom.view.CustomAutoCompleteView;
import com.incon.connect.user.custom.view.CustomTextInputLayout;
import com.incon.connect.user.databinding.FragmentAddCustomProductBinding;
import com.incon.connect.user.dto.addnewmodel.AddCustomProductModel;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.addnewmodel.adapter.ModelSearchArrayAdapter;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.register.RegistrationActivity;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

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

    private List<CategoryResponse> categoriesList = new ArrayList<>();
    private List<Division> divisionsList = new ArrayList<>();
    private List<Brand> fetchBrandsList = new ArrayList<>();

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
            Bundle bundle = getArguments();
            addCustomProductModel = new AddCustomProductModel();
            addCustomProductModel.setAddressId(bundle.getInt(BundleConstants.ADDRESS_ID));
            addCustomProductModel.setCustomerId(SharedPrefsUtils.loginProvider().getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE));
            binding.setAddCustomProductModel(addCustomProductModel);
            binding.setAddCustomProductFragment(this);
            rootView = binding.getRoot();
            initViews();
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
                            selectedDateTime.getTime(), DateFormatterConstants.DD_MM_YYYY);
                    addCustomProductModel.setDateOfPurchased(dobInMMDDYYYY);

                    Pair<String, Integer> validate = addCustomProductModel.
                            validateAddNewModel((String) binding.edittextPurchasedDate.getTag());
                    updateUiAfterValidation(validate.first, validate.second);
                }
            };

    // category spinner
    private void loadCategorySpinnerData() {
        String[] stringCategoryList = new String[categoriesList.size()];
        for (int i = 0; i < categoriesList.size(); i++) {
            stringCategoryList[i] = categoriesList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.view_spinner, stringCategoryList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerCategory.setAdapter(arrayAdapter);
        binding.spinnerCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (categorySelectedPos != position) {

                    CategoryResponse fetchCategories = categoriesList.get(position);
                    addCustomProductModel.setCategoryId(fetchCategories.getId());
                    addCustomProductModel.setCategoryName(fetchCategories.getName());

                    HashMap<Integer, List<Division>> divisionHashMap = ((HomeActivity) getActivity()).getDivisionHashMap();
                    List<Division> divisions = divisionHashMap.get(fetchCategories.getId());
                    divisionSelectedPos = -1;
                    if (divisions == null) {
                        if (divisionsList == null) {
                            divisionsList = new ArrayList<>();
                        } else
                            divisionsList.clear();
                        addCustomProductPresenter.getDivisionsFromCategoryId(fetchCategories.getId());
                    } else {
                        divisionsList.addAll(divisions);
                    }
                    loadDivisionSpinnerData(divisionsList);
                    binding.spinnerDivision.setText("");
                    categorySelectedPos = position;
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

        brandSelectedPos = -1;
        binding.spinnerBrand.setVisibility(View.GONE);

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
                    Division division = divisionsList.get(divisionSelectedPos);
                    addCustomProductModel.setDivisionId(division.getId());
                    addCustomProductModel.setDivisionName(division.getName());


                    HashMap<Integer, List<Brand>> brandHashMap = ((HomeActivity) getActivity()).getBrandHashMap();
                    List<Brand> brands = brandHashMap.get(division.getId());
                    brandSelectedPos = -1;
                    if (brands == null) {
                        fetchBrandsList.clear();
                        addCustomProductPresenter.getBrandsFromDivisionId(division.getId());
                    } else {
                        brandSelectedPos = -1;
                        fetchBrandsList.addAll(brands);
                    }
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
        String[] stringBrandList = new String[brandList.size()];
        for (int i = 0; i < brandList.size(); i++) {
            stringBrandList[i] = brandList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.view_spinner, stringBrandList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerBrand.setAdapter(arrayAdapter);
        binding.spinnerBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (brandSelectedPos != position) {
                    brandSelectedPos = position;
                    addCustomProductModel.setBrandId(brandList.get(position).getId());
                    addCustomProductModel.setBrandName(brandList.get(position).getName());
                }

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

                    //Clear spinner data
                    categorySelectedPos = -1;
                    categoriesList.clear();
                    loadCategorySpinnerData();
                    brandSelectedPos = -1;
                    fetchBrandsList.clear();
                    loadBrandSpinnerData(fetchBrandsList);
                    divisionSelectedPos = -1;
                    divisionsList.clear();
                    loadDivisionSpinnerData(divisionsList);
                    ///////////////////


                    //added data based on model selection
                    Category category = modelSearchResponse.getCategory();
                    addCustomProductModel.setCategoryId(category.getId());
                    addCustomProductModel.setCategoryName(category.getName());


                    binding.spinnerDivision.setVisibility(View.VISIBLE);
                    Division division = modelSearchResponse.getDivision();
                    addCustomProductModel.setDivisionId(division.getId());
                    addCustomProductModel.setDivisionName(division.getName());

                    binding.spinnerBrand.setVisibility(View.VISIBLE);
                    Brand brand = modelSearchResponse.getBrand();
                    addCustomProductModel.setBrandId(brand.getId());
                    addCustomProductModel.setBrandName(brand.getName());
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
        //TODO have to add scan for serial no, batch no
        shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        loadValidationErrors();
        setFocusForViews();

        selectedModelNumber = binding.edittextModelNumber.getText().toString();
        initializeModelNumberAdapter(new ArrayList<ModelSearchResponse>());

        if (ConnectApplication.getAppContext().getCategoriesList() == null) {
            addCustomProductPresenter.getCategories();
        } else {
            loadCategoriesList();
        }
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
        } else {
            ((MaterialBetterSpinner) view).setError(validationId == VALIDATION_SUCCESS ? null
                    : errorMap.get(validationId));
        }

        if (validationId != VALIDATION_SUCCESS) {
            view.startAnimation(shakeAnim);
            AppUtils.focusOnView(binding.scrollviewUserInfo, view);

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
        errorMap.put(AddNewModelValidation.PRICE, getString(R.string.error_product_price));


    }

    public void onSubmitClick() {
        if (validateFields()) {
            addCustomProductPresenter.addingCustomProduct(addCustomProductModel);
        }
    }

    @Override
    public void addNewModel() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(),
                    Activity.RESULT_OK, null);
        }
        getActivity().onBackPressed();
    }

    @Override
    public void loadCategoriesList() {
        categoriesList.addAll(ConnectApplication.getAppContext().getCategoriesList());
        loadCategorySpinnerData();
    }

    @Override
    public void loadBrandsList(List<Brand> brandList) {
        HashMap<Integer, List<Brand>> brandHashMap = ((HomeActivity) getActivity()).getBrandHashMap();
        brandHashMap.put(divisionsList.get(divisionSelectedPos).getId(), brandList);
        fetchBrandsList.addAll(brandList);
        loadBrandSpinnerData(brandList);
    }

    @Override
    public void loadDivisionsList(List<Division> divisionList) {
        HashMap<Integer, List<Division>> divisionHashMap = ((HomeActivity) getActivity()).getDivisionHashMap();
        divisionHashMap.put(categoriesList.get(categorySelectedPos).getId(), divisionList);
        this.divisionsList.addAll(divisionList);
        loadDivisionSpinnerData(divisionList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addCustomProductPresenter.disposeAll();
    }
}
