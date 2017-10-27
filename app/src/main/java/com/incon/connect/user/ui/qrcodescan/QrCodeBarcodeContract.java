package com.incon.connect.user.ui.qrcodescan;

import com.incon.connect.user.ui.BaseView;

public interface QrCodeBarcodeContract {

    interface View extends BaseView {
        void navigateToPreviousScreen(String qrCode);
    }

    interface Presenter {
        //Do nothing
    }

}
