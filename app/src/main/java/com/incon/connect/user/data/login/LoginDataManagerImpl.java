package com.incon.connect.user.data.login;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.login.StoreResponse;
import com.incon.connect.user.utils.SharedPrefsUtils;

public class LoginDataManagerImpl implements LoginDataManager, AppConstants.LoginPrefs {
    private static final String TAG = LoginDataManagerImpl.class.getName();

    @Override
    public void saveLoginDataToPrefs(LoginResponse loginResponse) {
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.loginProvider();

        sharedPrefsUtils.setBooleanPreference(LOGGED_IN, true);
        //Adding user details to preferences
        sharedPrefsUtils.setIntegerPreference(USER_ID,
                loginResponse.getId());
        sharedPrefsUtils.setStringPreference(USER_NAME,
                loginResponse.getName());
        sharedPrefsUtils.setStringPreference(USER_EMAIL_ID,
                loginResponse.getEmail());
        sharedPrefsUtils.setStringPreference(USER_PHONE_NUMBER,
                loginResponse.getMsisdn());
        sharedPrefsUtils.setStringPreference(USER_DOB,
                loginResponse.getDobInMillis());
        sharedPrefsUtils.setStringPreference(USER_GENDER,
                loginResponse.getGender());

        //Adding Store details to preferences
        StoreResponse storeDetails = loginResponse.getStore();
        sharedPrefsUtils.setIntegerPreference(STORE_ID,
                storeDetails.getId());
        sharedPrefsUtils.setStringPreference(STORE_NAME,
                storeDetails.getName());
        sharedPrefsUtils.setStringPreference(STORE_EMAIL_ID,
                storeDetails.getStoreEmail());
        sharedPrefsUtils.setStringPreference(STORE_PHONE_NUMBER,
                storeDetails.getContactNumber());
        saveStoreLogo(storeDetails.getLogo());
        sharedPrefsUtils.setStringPreference(STORE_GSTN,
                storeDetails.getGstn());
        sharedPrefsUtils.setStringPreference(STORE_ADDRESS,
                storeDetails.getAddress());

        saveStoreLogo(storeDetails.getLogo());
    }

    public void saveStoreLogo(String storeDetailsLogo) {
        SharedPrefsUtils.loginProvider().setStringPreference(STORE_LOGO,
                storeDetailsLogo);
    }
}
