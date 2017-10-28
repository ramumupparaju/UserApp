package com.incon.connect.user.ui.history.fragments;

import com.incon.connect.user.apimodel.components.history.purchased.InterestHistoryResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public interface InterestContract {

    interface View extends BaseView {
        void loadInterestHistory(List<InterestHistoryResponse> interestHistoryResponseList);
        void loadInterestDeleteHistory(Object interestHistoryResponseList);

    }

    interface Presenter {
    }

}
