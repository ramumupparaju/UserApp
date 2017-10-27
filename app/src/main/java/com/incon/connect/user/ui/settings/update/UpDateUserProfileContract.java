package com.incon.connect.user.ui.settings.update;

import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.updateuserprofile.UpDateUserProfileResponce;
import com.incon.connect.user.dto.update.UpDateUserProfile;
import com.incon.connect.user.ui.BaseView;

/**
 * Created by PC on 10/13/2017.
 */

public interface UpDateUserProfileContract {

    interface View extends BaseView {
        void loadUpDateUserProfileResponce(UpDateUserProfileResponce upDateUserProfileResponce);

    }
    interface Presenter {
        void upDateUserProfile(int merchantId, UpDateUserProfile upDateUserProfile);
        void saveUserData(LoginResponse loginResponse);

    }
}
