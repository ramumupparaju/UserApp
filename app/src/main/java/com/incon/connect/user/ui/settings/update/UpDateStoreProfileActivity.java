package com.incon.connect.user.ui.settings.update;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.defaults.CategoryResponse;
import com.incon.connect.user.apimodel.components.defaults.DefaultsResponse;
import com.incon.connect.user.apimodel.components.updatestoreprofile.UpDateStoreProfileResponce;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppCheckBoxListDialog;
import com.incon.connect.user.custom.view.PickImageDialog;
import com.incon.connect.user.custom.view.PickImageDialogInterface;
import com.incon.connect.user.databinding.ActivityUpdateStoreProfileBinding;
import com.incon.connect.user.dto.dialog.CheckedModelSpinner;
import com.incon.connect.user.dto.update.UpDateStoreProfile;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.settings.SettingsActivity;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.OfflineDataManager;
import com.incon.connect.user.utils.PermissionUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.incon.connect.user.AppConstants.LoginPrefs.STORE_ADDRESS;
import static com.incon.connect.user.AppConstants.LoginPrefs.STORE_CATEGORY_NAME;
import static com.incon.connect.user.AppConstants.LoginPrefs.STORE_EMAIL_ID;
import static com.incon.connect.user.AppConstants.LoginPrefs.STORE_GSTN;
import static com.incon.connect.user.AppConstants.LoginPrefs.STORE_NAME;
import static com.incon.connect.user.AppConstants.LoginPrefs.STORE_PHONE_NUMBER;

/**
 * Created by PC on 10/13/2017.
 */

public class UpDateStoreProfileActivity extends BaseActivity implements
        UpDateStoreProfileContract.View {
    ActivityUpdateStoreProfileBinding binding;
    private PickImageDialog pickImageDialog;
    private AppCheckBoxListDialog categoryDialog;
    private String selectedFilePath = "";
    private List<CheckedModelSpinner> categorySpinnerList;
    private List<CategoryResponse> categoryResponseList;
    private UpDateStoreProfile upDateStoreProfile;
    private UpDateStoreProfilePresenter upDateStoreProfilePresenter;
    private View rootView;
    private HashMap<Integer, String> errorMap;
    private Animation shakeAnim;
    private static final String TAG = UpDateStoreProfileActivity.class.getSimpleName();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_store_profile;
    }

    @Override
    protected void initializePresenter() {
        upDateStoreProfilePresenter = new UpDateStoreProfilePresenter();
        upDateStoreProfilePresenter.setView(this);
        setBasePresenter(upDateStoreProfilePresenter);

    }

    public void onSubmitClick() {
//        validateFields();
        upDateStoreProfilePresenter.upDateStoreProfile(SharedPrefsUtils.loginProvider().
                getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE), upDateStoreProfile);

    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setUpDateStoreProfileActivity(this);
        upDateStoreProfile = new UpDateStoreProfile();
        binding.setUpDateStoreProfile(upDateStoreProfile);
        binding.toolbarEditProfile.toolbarTextStore.setVisibility(View.VISIBLE);
        binding.toolbarEditProfile.toolbarTextUser.setVisibility(View.GONE);
        binding.toolbarEditProfile.toolbarLeftIv.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent backToSettings = new Intent(UpDateStoreProfileActivity.this,
                        SettingsActivity.class);
                startActivity(backToSettings);
            }
        });
        binding.toolbarEditProfile.toolbarEditImage.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.buttonSubmit.setVisibility(View.VISIBLE);
                binding.toolbarEditProfile.toolbarEditImage.setVisibility(View.GONE);

            }
        });
        loadData();

    }

    private void loadData() {
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
       // loadValidationErrors();
       // setFocusListenersForEditText();
        loadStoreData();


        categorySpinnerList = new ArrayList<>();
        DefaultsResponse defaultsResponse = new OfflineDataManager().loadData(
                DefaultsResponse.class, DefaultsResponse.class.getName());
        categoryResponseList = defaultsResponse.getCategories();
        for (int i = 0; i < categoryResponseList.size(); i++) {
            CheckedModelSpinner checkedModelSpinner = new CheckedModelSpinner();
            checkedModelSpinner.setName(categoryResponseList.get(i).getName());
            categorySpinnerList.add(checkedModelSpinner);
        }
    }

    private void loadStoreData() {
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.loginProvider();

        upDateStoreProfile.setStoreName(sharedPrefsUtils.getStringPreference(STORE_NAME));
        upDateStoreProfile.setContactNumber(sharedPrefsUtils.getStringPreference(
                STORE_PHONE_NUMBER));
        upDateStoreProfile.setStoreCategoryNames(sharedPrefsUtils.getStringPreference(
                STORE_CATEGORY_NAME));
        upDateStoreProfile.setStoreAddress(sharedPrefsUtils.getStringPreference(
                STORE_ADDRESS));
        upDateStoreProfile.setStoreEmail(sharedPrefsUtils.getStringPreference(
                STORE_EMAIL_ID));
        upDateStoreProfile.setGstn(sharedPrefsUtils.getStringPreference(
                STORE_GSTN));
    }

    public void openCameraToUpload() {
        PermissionUtils.getInstance().grantPermission(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                new PermissionUtils.Callback() {
                    @Override
                    public void onFinish(HashMap<String, Integer> permissionsStatusMap) {
                        int storageStatus = permissionsStatusMap.get(
                                Manifest.permission.CAMERA);
                        switch (storageStatus) {
                            case PermissionUtils.PERMISSION_GRANTED:
                                showImageOptionsDialog();
                                Logger.v(TAG, "location :" + "granted");
                                break;
                            case PermissionUtils.PERMISSION_DENIED:
                                Logger.v(TAG, "location :" + "denied");
                                break;
                            case PermissionUtils.PERMISSION_DENIED_FOREVER:
                                Logger.v(TAG, "location :" + "denied forever");
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    public void onAddressClick() {
        Intent addressIntent = new Intent(this, RegistrationMapActivity.class);
        startActivityForResult(addressIntent, RequestCodes.ADDRESS_LOCATION);
    }

    private void showImageOptionsDialog() {

        pickImageDialog = new PickImageDialog(this);
        pickImageDialog.mImageHandlingDelegate = pickImageDialogInterface;
        pickImageDialog.initDialogLayout();
    }
    public void onCategoryClick() {
        showCategorySelectionDialog();
    }

    private void showCategorySelectionDialog() {
        //set previous selected categories as checked
        String selectedCategories = binding.edittextUpDateCategory.getText().toString();
        if (!TextUtils.isEmpty(selectedCategories)) {
            String[] split = selectedCategories.split(COMMA_SEPARATOR);
            for (String categoryString : split) {
                CheckedModelSpinner checkedModelSpinner = new CheckedModelSpinner();
                checkedModelSpinner.setName(categoryString);
                int indexOf = categorySpinnerList.indexOf(checkedModelSpinner);
                categorySpinnerList.get(indexOf).setChecked(true);
            }

        }
        categoryDialog = new AppCheckBoxListDialog.AlertDialogBuilder(this, new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String caetogories) {
                        binding.edittextUpDateCategory.setText(caetogories);
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                categoryDialog.dismiss();
                                break;
                            case AlertDialogCallback.CANCEL:
                                categoryDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.register_category_hint))
                .spinnerItems(categorySpinnerList)
                .build();
        categoryDialog.showDialog();
    }


    private PickImageDialogInterface pickImageDialogInterface = new PickImageDialogInterface() {
        @Override
        public void handleIntent(Intent intent, int requestCode) {
            if (requestCode == RequestCodes.SEND_IMAGE_PATH) { // loading image in full screen
                if (TextUtils.isEmpty(selectedFilePath)) {
                    showErrorMessage(getString(R.string.error_image_path_req));
                } else {
                    intent.putExtra(IntentConstants.IMAGE_PATH, selectedFilePath);
                    startActivity(intent);
                }
                return;
            }
            startActivityForResult(intent, requestCode);
        }

        @Override
        public void displayPickedImage(String uri, int requestCode) {
            selectedFilePath = uri;
            ((BaseActivity) UpDateStoreProfileActivity.this).loadImageUsingGlide(
                    selectedFilePath, binding.storeLogoIv);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.TERMS_AND_CONDITIONS:
                    //TODO need to cal api cal
                    //callRegisterApi();
                    break;
                case RequestCodes.ADDRESS_LOCATION:
                    upDateStoreProfile.setStoreAddress(data.getStringExtra(
                            IntentConstants.ADDRESS_COMMA));
                    upDateStoreProfile.setStoreLocation(data.getStringExtra(
                            IntentConstants.LOCATION_COMMA));
                    break;
                default:
                    pickImageDialog.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }

    }

    @Override
    public void loadUpDateStoreProfileResponce(UpDateStoreProfileResponce
                                                           merchantId) {
        AppUtils.showSnackBar(rootView, String.valueOf(merchantId.getId()));

    }
}



