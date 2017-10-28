package com.incon.connect.user.ui.register.fragment;


import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;

/**
 * Created on 08 Jun 2017 6:32 PM.
 *
 */
public class RegistrationUserFragmentContract {

    interface View extends BaseView {
        void navigateToRegistrationActivityNext();
        void uploadUserData(int userId);
        void navigateToHomeScreen();
        void validateOTP();

    }

    interface Presenter {
        void validateOTP(HashMap<String, String> verify);
        void registerRequestOtp(String phoneNumber);
        void registerRequestPasswordOtp(String phoneNumber);
    }

}
