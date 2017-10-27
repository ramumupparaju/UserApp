package com.incon.connect.user.ui.register.fragment;


import com.incon.connect.user.dto.registration.Registration;
import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;

import okhttp3.MultipartBody;

/**
 * Created on 08 Jun 2017 6:32 PM.
 *
 */
public class RegistrationStoreFragmentContract {

    public interface View extends BaseView {
        void navigateToRegistrationActivityNext();
        void navigateToHomeScreen();
        void uploadStoreLogo(int storeId);
        void validateOTP();
    }

    interface Presenter {
        void register(Registration registrationBody);
        void uploadStoreLogo(int storeId, MultipartBody.Part storeLogo);
        void validateOTP(HashMap<String, String> verify);
        void registerRequestOtp(String phoneNumber);
        void registerRequestPasswordOtp(String phoneNumber);
    }

}
