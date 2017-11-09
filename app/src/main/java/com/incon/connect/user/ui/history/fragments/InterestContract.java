package com.incon.connect.user.ui.history.fragments;

import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public interface InterestContract {

    interface View extends BaseView {
        void loadInterestHistory(List<ProductInfoResponse> interestHistoryResponseList);
        void loadInterestDeleteHistory(Object interestHistoryResponseList);
        void loadBuyRequestResponce(Object buyRequestResponceList);

    }

    interface Presenter {
        void interestApi(int userId);
        void buyrequestApi(int userId);
        void deleteApi(int interestId);
    }

}
