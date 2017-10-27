package com.incon.connect.user.ui.scan;


import com.incon.connect.user.apimodel.components.qrcodebaruser.UserInfoResponse;
import com.incon.connect.user.ui.BaseView;

/**
 * Created on 31 May 2017 11:18 AM.
 *
 */
public interface ScanTabContract {

    interface View extends BaseView {
        void userInfo(UserInfoResponse userInfoResponse);
    }

    interface Presenter {
        void userInfoUsingPhoneNumber(String phoneNumber);
        void userInfoUsingQrCode(String qrCode);
        void newUserRegistration(String phoneNumber);
    }

}
