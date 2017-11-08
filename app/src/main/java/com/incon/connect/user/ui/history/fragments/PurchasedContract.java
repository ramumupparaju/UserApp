package com.incon.connect.user.ui.history.fragments;


import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created on 31 May 2017 11:18 AM.
 *
 */
public interface PurchasedContract {

    interface View extends BaseView {
        void loadPurchasedHistory(List<ProductInfoResponse> productInfoResponses);
    }

    interface Presenter {
        void purchased(int userId);
    }

}
