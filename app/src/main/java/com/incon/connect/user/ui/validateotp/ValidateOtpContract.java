package com.incon.connect.user.ui.validateotp;


import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.validateotp.ValidateWarrantyOtpResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;

/**
 * Created on 08 Jun 2017 6:32 PM.
 *
 */
public class ValidateOtpContract {

    public interface View extends BaseView {
        void validateOTP(LoginResponse loginResponse);
        void validateWarrantyOTP(ValidateWarrantyOtpResponse warrantyOtpResponse);
    }

    interface Presenter {
        void validateOTP(HashMap<String, String> verify);
        void validateWarrantyOTP(HashMap<String, String> verify);
    }

}
