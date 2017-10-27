package com.incon.connect.user.ui.forgotpassword;

import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;

public interface ForgotPasswordContract {

    interface View extends BaseView {
        void navigateToResetPromtPage();
        boolean validatePhoneNumber();
    }

    interface Presenter {
        void forgotPassword(HashMap<String, String> email);
    }

}
