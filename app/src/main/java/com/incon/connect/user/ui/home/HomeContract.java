package com.incon.connect.user.ui.home;


import com.incon.connect.user.ui.BaseView;

/**
 * Created on 31 May 2017 11:18 AM.
 */
public interface HomeContract {

    interface View extends BaseView {
        void navigateToProductAssignScreen(String qrCode);
    }

    interface Presenter {
        void checkQrCodeValidity(String qrCode);
    }

}
