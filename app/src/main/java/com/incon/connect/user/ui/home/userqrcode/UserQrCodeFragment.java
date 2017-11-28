package com.incon.connect.user.ui.home.userqrcode;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.incon.connect.user.R;
import com.incon.connect.user.databinding.FragmentUserQrcodeBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.home.HomeActivity;

import net.glxn.qrgen.android.QRCode;

/**
 * Created by PC on 10/6/2017.
 */
public class UserQrCodeFragment extends BaseFragment {

    private View rootView;
    private FragmentUserQrcodeBinding binding;
    private Bitmap qrcodeBitmap;

    @Override
    protected void initializePresenter() {
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.user_uid));
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            // handle events from here using android binding
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_qrcode,
                    container, false);
            rootView = binding.getRoot();
            loadQrcode();
        }
        setTitle();
        return rootView;
    }

    // loading qr code
    private void loadQrcode() {
        Bundle bundle = getArguments();
        String data = bundle.getString(BundleConstants.QRCODE_DATA);
        if (TextUtils.isEmpty(data)) {
            getActivity().onBackPressed();
            return;
        }
        int width = (int) getResources().getDimension(R.dimen.qrcodescan_imageView_qrcode_width);
        int height = (int) getResources().getDimension(R.dimen.qrcodescan_imageView_qrcode_height);
        qrcodeBitmap = QRCode.from(data)
                .withErrorCorrection(ErrorCorrectionLevel.H)
                .withSize(width, height)
                .withColor(0xff000000, 0xffffffff).bitmap();
        binding.qrcodeIv.setImageBitmap(qrcodeBitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        qrcodeBitmap.recycle();
    }
}
