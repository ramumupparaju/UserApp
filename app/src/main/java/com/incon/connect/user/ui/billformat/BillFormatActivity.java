package com.incon.connect.user.ui.billformat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.custom.view.PickImageDialog;
import com.incon.connect.user.custom.view.PickImageDialogInterface;
import com.incon.connect.user.databinding.ActivityBillFormatBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.PermissionUtils;

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
    private boolean showingOriginal = false;
    private BillFormatPresenter billFormatPresenter;
    private int purchaseId;


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
            toggleImage();

            File fileToUpload = new File(selectedFilePath == null ? "" : selectedFilePath);
            if (fileToUpload.exists()) {
                binding.includeRegisterBottomButtons.buttonLeft.setText(getString(R.string.action_new));
                binding.includeRegisterBottomButtons.buttonRight.setText(getString(R.string.action_upload_now));
            } else {
                showErrorMessage(getString(R.string.error_image_path_upload));
            }
        }
    };

    public void previewOrImage(View view) {

        if (!TextUtils.isEmpty(selectedFilePath)) {

            toggleImage();
            return;
        }
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

    private void toggleImage() {
        if (showingOriginal) {
            binding.scrollviewUserInfo.setVisibility(View.VISIBLE);
            binding.billPrevLayout.setVisibility(View.GONE);
        } else {
            binding.billPrevLayout.setVisibility(View.VISIBLE);
            loadImageUsingGlide(selectedFilePath, binding.billPrev);
            binding.scrollviewUserInfo.setVisibility(View.GONE);
        }
        showingOriginal = !showingOriginal;
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
        selectedFilePath = productInfoResponse.getBillUrl();


        binding.textDopValues.setText(DateUtils.convertMillisToStringFormat(productInfoResponse.getPurchasedDate(), DateFormatterConstants.DD_MM_YYYY));
        binding.includeRegisterBottomButtons.buttonLeft.setText(getString(R.string.action_back));
        binding.includeRegisterBottomButtons.buttonRight.setText(getString(R.string.action_change));
        binding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(onClickListener);
        binding.includeRegisterBottomButtons.buttonRight.setOnClickListener(onClickListener);
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
                showingOriginal = false;
                selectedFilePath = null;
                previewOrImage(view);
            } else if (button.getText().toString().equals(getString(R.string.action_cancel))) {
                selectedFilePath = productInfoResponse.getBillUrl();
                previewOrImage(view);
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
