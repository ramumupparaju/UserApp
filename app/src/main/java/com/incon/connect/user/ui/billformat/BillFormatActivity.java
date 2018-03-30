package com.incon.connect.user.ui.billformat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.BuildConfig;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.custom.view.PickImageDialog;
import com.incon.connect.user.custom.view.PickImageDialogInterface;
import com.incon.connect.user.databinding.ActivityBillFormatBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.history.adapter.ShowRoomAdapter;
import com.incon.connect.user.utils.AddressFromLatLngAddress;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.PermissionUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.incon.connect.user.ui.tutorial.TutorialActivity.TAG;


/**
 * Created by PC on 11/15/2017.
 */

public class BillFormatActivity extends BaseActivity implements BillFormatContract.View {
    private ActivityBillFormatBinding binding;
    private ProductInfoResponse productInfoResponse;

    private PickImageDialog pickImageDialog;
    private String selectedFilePath = "";
    private BillFormatPresenter billFormatPresenter;
    private int purchaseId;
    private AddressFromLatLngAddress addressFromLatLngAddress;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                default:
                    pickImageDialog.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bill_format;
    }

    @Override
    protected void initializePresenter() {
        billFormatPresenter = new BillFormatPresenter();
        billFormatPresenter.setView(this);
        setBasePresenter(billFormatPresenter);
    }

    private void showImageOptionsDialog() {
        pickImageDialog = new PickImageDialog(this);
        pickImageDialog.mImageHandlingDelegate = pickImageDialogInterface;
        pickImageDialog.initDialogLayout(false);
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
            File fileToUpload = new File(selectedFilePath == null ? "" : selectedFilePath);
            if (fileToUpload.exists()) {
                binding.includeRegisterBottomButtons.buttonLeft.setText(getString(R.string.action_new));
                binding.includeRegisterBottomButtons.buttonRight.setText(getString(R.string.action_upload_now));
                loadImageUsingGlide(selectedFilePath, binding.billPrev);
            } else {
                showErrorMessage(getString(R.string.error_image_path_upload));
            }
        }
    };

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    public void previewOrImage(View view) {
        toggleImage();
        showCameraOptions();
    }

    private void showCameraOptions() {

        if (TextUtils.isEmpty(selectedFilePath)) {
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
    }

    private void toggleImage() {
        if (binding.billPrevLayout.getVisibility() == View.VISIBLE) {
            binding.scrollviewUserInfo.setVisibility(View.VISIBLE);
            binding.billPrevLayout.setVisibility(View.GONE);
        } else {
            binding.billPrevLayout.setVisibility(View.VISIBLE);
            binding.scrollviewUserInfo.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        // Make us non-modal, so that others can receive touch events.
        Window window = getWindow();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        Drawable drawable = getResources().getDrawable(R.drawable.bg_round_corners);
        InsetDrawable inset = new InsetDrawable(drawable, 0, 100, 0, 100);
        getWindow().setBackgroundDrawable(inset);


        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setBillFormatActivity(this);
        Bundle bundle = getIntent().getExtras();

        productInfoResponse = bundle.getParcelable(BundleConstants.PRODUCT_INFO_RESPONSE);
        purchaseId = Integer.parseInt(productInfoResponse.getWarrantyId());
        binding.setProductinforesponse(productInfoResponse);
        selectedFilePath = TextUtils.isEmpty(productInfoResponse.getBillUrl()) ? "" : BuildConfig.SERVICE_ENDPOINT + productInfoResponse.getBillUrl();

        if (!TextUtils.isEmpty(selectedFilePath)) {
            loadImageUsingGlide(selectedFilePath, binding.billPrev);
        }

        if (productInfoResponse.getCategoryName().equals(AppConstants.CATEGORY_AUTOMOBILES)) {
            binding.textSn.setText(getString(R.string.add_vin));
        } else {
            binding.textSn.setText(getString(R.string.text_s_n));
        }

        RelativeLayout.LayoutParams customerDetailsBase = (RelativeLayout.LayoutParams) binding.customerDetailsBase.getLayoutParams();
        if (productInfoResponse.getCustomProductFlag().equalsIgnoreCase(CUSTOM)) {
            binding.textBillId.setVisibility(View.GONE);
            binding.textBillIdColon.setVisibility(View.GONE);
            binding.textBillIdValues.setVisibility(View.GONE);
            customerDetailsBase.addRule(RelativeLayout.BELOW, binding.productDetailsBase.getId());
        } else {
            customerDetailsBase.addRule(RelativeLayout.BELOW, binding.showroomDetailsBase.getId());
            binding.textBillId.setVisibility(View.VISIBLE);
            binding.textBillIdColon.setVisibility(View.VISIBLE);
            binding.textBillIdValues.setVisibility(View.VISIBLE);
        }
        try {
            String formattedPrice = productInfoResponse.getPrice().substring(0, productInfoResponse.getPrice().indexOf('.'));
            binding.textPriceValus.setText("INR " + formattedPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            binding.textPriceValus.setText("INR " + productInfoResponse.getPrice());
        }
        binding.textDopValues.setText(DateUtils.convertMillisToStringFormat(productInfoResponse.getPurchasedDate(), DateFormatterConstants.DD_MM_YYYY));
        binding.textInvoiceNoValues.setText(TextUtils.isEmpty(productInfoResponse.getInvoiceNumber()) ? "-" : productInfoResponse.getInvoiceNumber());
        binding.includeRegisterBottomButtons.buttonLeft.setText(getString(R.string.action_back));
        binding.includeRegisterBottomButtons.buttonRight.setText(getString(R.string.action_change));
        binding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(onClickListener);
        binding.includeRegisterBottomButtons.buttonRight.setOnClickListener(onClickListener);


        binding.textCustomerNameValues.setText(SharedPrefsUtils.loginProvider().getStringPreference(LoginPrefs.USER_NAME));
        binding.textCustomerContactDetailsValues.setText(SharedPrefsUtils.loginProvider().getStringPreference(LoginPrefs.USER_PHONE_NUMBER));
        String[] location = SharedPrefsUtils.loginProvider().getStringPreference(LoginPrefs.USER_LOCATION).split(COMMA_SEPARATOR);
        addressFromLatLngAddress.getAddressFromLocation(this,
                new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1])), AppConstants.RequestCodes.LOCATION_ADDRESS_FROM_LATLNG, new LocationHandler());

    }

    private class LocationHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            Address locationAddress = bundle.getParcelable(AppConstants.BundleConstants
                    .LOCATION_ADDRESS);
            if (locationAddress != null) {
                switch (message.what) {
                    case AppConstants.RequestCodes.LOCATION_ADDRESS_FROM_LATLNG:
                        binding.textCustomerAddressValues.setText(locationAddress.getAddressLine(0));
                        break;
                    default:
                        //do nothing
                }
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            if (button.getText().toString().equals(getString(R.string.action_upload_now))) {
                File fileToUpload = new File(selectedFilePath == null ? "" : selectedFilePath);
                if (fileToUpload.exists()) {
                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), fileToUpload);
                    // MultipartBody.Part is used to send also the actual file name
                    MultipartBody.Part imagenPerfil = MultipartBody.Part.createFormData(ApiRequestKeyConstants.STORE_LOGO,
                            fileToUpload.getName(), requestFile);
                    billFormatPresenter.uploadBill(purchaseId, imagenPerfil);
                } else {
                    showErrorMessage(getString(R.string.error_image_path_upload));
                }
            } else if (button.getText().toString().equals(getString(R.string.action_change)) ||
                    button.getText().toString().equals(getString(R.string.action_edit))) {
                selectedFilePath = null;
                showCameraOptions();
            } else if (button.getText().toString().equals(getString(R.string.action_cancel))) {
                selectedFilePath = productInfoResponse.getBillUrl();
                loadImageUsingGlide(selectedFilePath, binding.billPrev);
            } else if (button.getText().toString().equals(getString(R.string.action_back))) {
                finish();
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    @Override
    public void onBillUpload() {
        finish();
    }
}
