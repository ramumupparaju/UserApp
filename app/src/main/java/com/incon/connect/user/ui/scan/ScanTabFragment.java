package com.incon.connect.user.ui.scan;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.qrcodebaruser.UserInfoResponse;
import com.incon.connect.user.databinding.FragmentScanTabBinding;
import com.incon.connect.user.dto.warrantyregistration.WarrantyRegistration;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.qrcodescan.QrcodeBarcodeScanActivity;
import com.incon.connect.user.utils.ValidationUtils;


public class ScanTabFragment extends BaseFragment implements ScanTabContract.View {

    private static final String TAG = ScanTabFragment.class.getSimpleName();
    private View rootView;
    private FragmentScanTabBinding binding;
    private ScanTabPresenter scanTabPresenter;


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
            binding.setScanning(this);
            rootView = binding.getRoot();
            initViews();
        }
        setTitle();
        return rootView;
    }

    public void initViews() {
        binding.edittextMobileNumber.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            onDoneClick();
                            return true;
                        }
                        return false;
                    }
                });
    }

    public void onScanClick() {
        Intent intent = new Intent(getActivity(), QrcodeBarcodeScanActivity.class);
        intent.putExtra(IntentConstants.SCANNED_TITLE, getString(R.string.title_user_qr_code));
        startActivityForResult(intent, RequestCodes.USER_PROFILE_SCAN);
    }

    public void onDoneClick() {
        String phoneNumber = binding.edittextMobileNumber.getText().toString();
        if (ValidationUtils.isPhoneNumberValid(phoneNumber)) {
            /*binding.textMobilenumber.setText(phoneNumber);
            showUIType(SCAN_OPTIONS_UI);*/
            scanTabPresenter.userInfoUsingPhoneNumber(phoneNumber);
        } else {
            showErrorMessage(getString(R.string.error_phone_min_digits));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.USER_PROFILE_SCAN:
                    if (data != null) {
                        /*scanTabPresenter.userInterestedUsingQrCode(
                                data.getStringExtra(IntentConstants.SCANNED_CODE));*/
                //  TODO REMOVE Hard Coded UUID
                        scanTabPresenter.
                                userInterestedUsingQrCode("0d4e7ea7-d35f-4233-be2a-6e01b65e2bb9");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void userInfo(UserInfoResponse userInfoResponse) {
        WarrantyRegistration warrantyRegistration = new WarrantyRegistration();
        warrantyRegistration.setMobileNumber(userInfoResponse.getMsisdn());
        warrantyRegistration.setCustomerId(String.valueOf(userInfoResponse.getId()));
        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleConstants.WARRANTY_DATA, warrantyRegistration);
        ((HomeActivity) getActivity()).replaceFragmentAndAddToStack(
                ProductScanFragment.class, bundle);
        AppUtils.hideSoftKeyboard(getActivity(), rootView);
    }

    @Override
    public void userInterestedResponce(Object userInfoResponse) {

    }
}