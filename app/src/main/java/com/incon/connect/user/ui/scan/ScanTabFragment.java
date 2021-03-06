package com.incon.connect.user.ui.scan;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.databinding.FragmentScanTabBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.qrcodescan.QrcodeBarcodeScanActivity;
import com.incon.connect.user.utils.SharedPrefsUtils;


public class ScanTabFragment extends BaseFragment implements ScanTabContract.View {
    private static final String TAG = ScanTabFragment.class.getSimpleName();
    private View rootView;
    private FragmentScanTabBinding binding;
    private ScanTabPresenter scanTabPresenter;
    private AppAlertDialog productDetailsDialog;
    private ProductInfoResponse response;

    @Override
    protected void initializePresenter() {
        scanTabPresenter = new ScanTabPresenter();
        scanTabPresenter.setView(this);
        setBasePresenter(scanTabPresenter);
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.title_scan));
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_scan_tab, container, false);
            response = new ProductInfoResponse();
            binding.setProductinforesponse(response);
            binding.setScanning(this);
            rootView = binding.getRoot();
            SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.cacheProvider();
            boolean isScanFirst = sharedPrefsUtils.getBooleanPreference(
                    CachePrefs.IS_SCAN_FIRST, false);
            if (isScanFirst) {
                sharedPrefsUtils.setBooleanPreference(CachePrefs.IS_SCAN_FIRST, false);
            } else {
                onScanClick();
            }
        }
        setTitle();
        return rootView;
    }

    public void onScanClick() {
        /*scanTabPresenter.userInterestedUsingQrCode(SharedPrefsUtils.loginProvider().
                        getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE),
                "1-853-1511850039998");
        */Intent intent = new Intent(getActivity(), QrcodeBarcodeScanActivity.class);
        intent.putExtra(IntentConstants.SCANNED_TITLE, getString(R.string.title_user_qr_code));
        startActivityForResult(intent, RequestCodes.USER_PROFILE_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.USER_PROFILE_SCAN:
                    if (data != null) {
                        scanTabPresenter.userInterestedUsingQrCode(SharedPrefsUtils.loginProvider().
                                        getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE),
                                data.getStringExtra(IntentConstants.SCANNED_CODE));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void userInterestedResponce(ProductInfoResponse productInfoResponse) {
        Intent addressIntent = new Intent(getActivity(), ProductDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleConstants.PRODUCT_INFO_RESPONSE, productInfoResponse);
        addressIntent.putExtras(bundle);
        startActivity(addressIntent);
    }

    private void showProductDetailsDialog(ProductInfoResponse productInfoResponse) {
        productDetailsDialog = new AppAlertDialog.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                            case AlertDialogCallback.CANCEL:
                                productDetailsDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                })

                .title(getString(
                        R.string.bottom_option_main_features)
                        + productInfoResponse.getInformation()
                        + "\n"
                        + getString(
                        R.string.bottom_option_warranty)
                        + productInfoResponse.getWarrantyDays()
                        + "\n"
                        + getString(
                        R.string.bottom_option_description)
                        + productInfoResponse.getInformation()
                        + "\n"
                        + getString(
                        R.string.bottom_option_feedback)
                        + productInfoResponse.getInformation()
                        + "\n"
                        + getString(
                        R.string.bottom_option_price)
                        + productInfoResponse.getPrice()
                )
                .button1Text(getString(R.string.action_ok))
                .build();
        productDetailsDialog.showDialog();
        productDetailsDialog.setCancelable(true);
    }
}