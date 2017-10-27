package com.incon.connect.user.ui.buyrequets;


import com.incon.connect.user.apimodel.components.buyrequest.BuyRequestResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created on 31 May 2017 11:18 AM.
 *
 */
public interface BuyRequestContract {

    interface View extends BaseView {
        void loadBuyRequest(List<BuyRequestResponse> buyRequestResponseList);
    }

    interface Presenter {
    }

}
