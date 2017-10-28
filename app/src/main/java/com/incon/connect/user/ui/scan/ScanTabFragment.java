package com.incon.connect.user.ui.scan;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.databinding.FragmentScanTabBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.qrcodescan.QrcodeBarcodeScanActivity;


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
        }
        setTitle();
        return rootView;
    }

    public void onScanClick() {
        Intent intent = new Intent(getActivity(), QrcodeBarcodeScanActivity.class);
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
                        scanTabPresenter.userInterestedUsingQrCode(
                                data.getStringExtra(IntentConstants.SCANNED_CODE));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void userInterestedResponce(Object productInfoResponse) {
        //TODO have to show product details
    }
}