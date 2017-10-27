package com.incon.connect.user.ui.settings.update;

import com.incon.connect.user.apimodel.components.updatestoreprofile.UpDateStoreProfileResponce;
import com.incon.connect.user.ui.BaseView;

/**
 * Created by PC on 10/13/2017.
 */

public interface UpDateStoreProfileContract {
    interface View extends BaseView {
        void loadUpDateStoreProfileResponce(UpDateStoreProfileResponce upDateStoreProfileResponce);

    }
    interface Presenter {

    }
}
