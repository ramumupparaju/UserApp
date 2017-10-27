package com.incon.connect.user.ui.notifications;


import com.incon.connect.user.ui.BaseView;

/**
 * Created on 31 May 2017 11:18 AM.
 */
public interface PushContract {

    interface View extends BaseView {
    }

    interface Presenter {
        void pushRegisterApi();
    }

}
