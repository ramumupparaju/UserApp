package com.incon.connect.user.ui.addnewmodel;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.fetchcategorie.Brand;
import com.incon.connect.user.apimodel.components.fetchcategorie.Division;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.custom.view.CustomAutoCompleteView;
import com.incon.connect.user.custom.view.CustomTextInputLayout;
import com.incon.connect.user.databinding.FragmentAddNewModelBinding;
import com.incon.connect.user.dto.addnewmodel.AddNewModel;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 10/4/2017.
 */

public class AddNewModelFragment extends BaseFragment implements AddNewModelContract.View {
    private FragmentAddNewModelBinding binding;
    private View rootView;
    private AddNewModelPresenter addNewModelPresenter;
    private AddNewModel addNewModel;
    private List<FetchCategories> fetchCategorieList;
    private int categorySelectedPos = -1;
    private int divisionSelectedPos = -1;
    private HashMap<Integer, String> errorMap;
    private Animation shakeAnim;

    @Override
    protected void initializePresenter() {
        addNewModelPresenter = new AddNewModelPresenter();
        addNewModelPresenter.setView(this);
        setBasePresenter(addNewModelPresenter);
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.title_add_new_model));
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_add_new_model, container, false);
            addNewModel = new AddNewModel();
            binding.setAddNewModel(addNewModel);
            binding.setAddNewModelFragment(this);
            rootView = binding.getRoot();
            initViews();

            /*addNewModelPresenter.getCategories(SharedPrefsUtils.loginProvider().
                    getIntegerPreference(LoginPrefs.STORE_ID, DEFAULT_VALUE));*/

        }
        setTitle();
        return rootView;
    }
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
                    FetchCategories fetchCategories = fetchCategorieList.get(position);
                    addNewModel.setCategoryId(fetchCategories.getId());
                    addNewModel.setCategoryName(fetchCategories.getName());
                    loadDivisionSpinnerData(fetchCategories.getDivisions());
                    binding.spinnerDivision.setText("");
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
                    FetchCategories fetchCategories = fetchCategorieList.get(categorySelectedPos);
                    Division divisions1 = fetchCategories.getDivisions().get(divisionSelectedPos);
                    addNewModel.setDivisionId(divisions1.getId());
                    addNewModel.setDivisionName(divisions1.getName());
                    loadBrandSpinnerData(divisions1.getBrands());
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
                addNewModel.setBrandId(brandList.get(position).getId());
                addNewModel.setBrandName(brandList.get(position).getName());
                //For avoiding double tapping issue
                if (binding.spinnerBrand.getOnItemClickListener() != null) {
                    binding.spinnerBrand.onItemClick(parent, view, position, id);
                }
            }
        });
    }


    private void initViews() {
        shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        loadValidationErrors();
        setFocusForViews();
    }

    private void setFocusForViews() {
        binding.edittextModelNumber.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextName.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextMrpPrice.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextPrice.setOnFocusChangeListener(onFocusChangeListener);
        binding.edittextNotes.setOnFocusChangeListener(onFocusChangeListener);


    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            Object fieldId = view.getTag();
            if (fieldId != null) {
                Pair<String, Integer> validation = binding.getAddNewModel().
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
        binding.inputLayoutMrpPrice.setError(null);
        binding.inputLayoutPrice.setError(null);
        binding.inputLayoutNotes.setError(null);

        Pair<String, Integer> validation = binding.getAddNewModel().validateAddNewModel(null);
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
            addNewModelPresenter.addingNewModel(SharedPrefsUtils.loginProvider().
                    getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE), addNewModel);
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
    public void loadCategoriesList(List<FetchCategories> categoriesList) {
        fetchCategorieList = categoriesList;
        loadCategorySpinnerData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addNewModelPresenter.disposeAll();
    }
}
