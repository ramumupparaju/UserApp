package com.incon.connect.user.ui.login;


import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.dto.login.LoginUserData;
import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;

public interface LoginContract {

    interface View extends BaseView {
        void navigateToHomePage(LoginResponse loginResponse);
    }

    interface Presenter {
        void doLogin(LoginUserData loginUserData);
        void validateOTP(HashMap<String, String> verify);
        void registerRequestOtp(String phoneNumber);
    }

}
