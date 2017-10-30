package com.incon.connect.user.ui.history.fragments;

import com.incon.connect.user.apimodel.components.history.purchased.ReturnHistoryResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public interface ReturnContract {

    interface View extends BaseView {
        void loadReturnHistory(List<ReturnHistoryResponse> returnHistoryResponseList);
    }

    interface Presenter {
        void returnHistory(int userId);
    }



}
