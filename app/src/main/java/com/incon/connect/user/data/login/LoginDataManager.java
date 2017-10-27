package com.incon.connect.user.data.login;


import com.incon.connect.user.apimodel.components.login.LoginResponse;

public interface LoginDataManager {

    void saveLoginDataToPrefs(LoginResponse loginResponse);
}
