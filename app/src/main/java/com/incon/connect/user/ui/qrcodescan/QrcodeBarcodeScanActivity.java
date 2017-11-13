package com.incon.connect.user.ui.qrcodescan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.ResultPoint;
import com.incon.connect.user.R;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.databinding.ActivityQrcodeBarcodescanBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.PermissionUtils;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 9/27/2017.
 */
public class QrcodeBarcodeScanActivity extends BaseActivity implements QrCodeBarcodeContract.View {

    private static final String TAG = QrcodeBarcodeScanActivity.class.getSimpleName();
    private ActivityQrcodeBarcodescanBinding binding;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_qrcode_barcodescan;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setQrcodeScanActivity(this);
        initViews();
    }

    private void initViews() {
        String scannedTile = getIntent().getStringExtra(IntentConstants.SCANNED_TITLE);
        if (!TextUtils.isEmpty(scannedTile)) {
        }
    }

    // qr code scan
    private void startQRScan() {
        PermissionUtils.getInstance().grantPermission(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                new PermissionUtils.Callback() {
                    @Override
                    public void onFinish(HashMap<String, Integer> permissionsStatusMap) {
                        int storageStatus = permissionsStatusMap.get(
                                Manifest.permission.CAMERA);
                        switch (storageStatus) {
                            case PermissionUtils.PERMISSION_GRANTED:

                                binding.qrcodeScanner.resume();
                                binding.qrcodeScanner.decodeContinuous(qrcodeCallback);
                                break;
                            case PermissionUtils.PERMISSION_DENIED:
                                Logger.v(TAG, "CAMERA :" + "denied");
                            case PermissionUtils.PERMISSION_DENIED_FOREVER:
                                Logger.v(TAG, "CAMERA :" + "denied forever");
                            default:
                                AppUtils.shortToast(QrcodeBarcodeScanActivity.this, getString(
                                        R.string.location_permission_msg));
                                finish();
                                break;
                        }
                    }
                });

    }

    // barcode result
    public BarcodeCallback qrcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String output = result.getText();
            if (!TextUtils.isEmpty(output)) {
                Log.d(TAG, "qrscan result: " + output);
                navigateToPreviousScreen(output);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        startQRScan();
    }


    @Override
    public void onPause() {
        super.onPause();
        binding.qrcodeScanner.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding.qrcodeScanner != null) {
            binding.qrcodeScanner.pause();
        }
    }

    @Override
    public void navigateToPreviousScreen(String qrCode) {
        Intent intentData = new Intent();
        intentData.putExtra(IntentConstants.SCANNED_CODE, qrCode);
        setResult(Activity.RESULT_OK, intentData);
        finish();
    }
}
