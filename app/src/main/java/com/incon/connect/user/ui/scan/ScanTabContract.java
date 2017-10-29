package com.incon.connect.user.ui.scan;


import com.incon.connect.user.ui.BaseView;

/**
 * Created on 31 May 2017 11:18 AM.
 *
 */
public interface ScanTabContract {

    interface View extends BaseView {
        void userInterestedResponce(Object userInfoResponse);
    }

    interface Presenter {
        void userInterestedUsingQrCode(int customerId, String qrCode);
    }

}
