package com.incon.connect.user.ui.history.fragments;

import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 1/23/2018.
 */

public interface ShowRoomContract {
    interface View extends BaseView {
        void loadReturnHistory(List<ProductInfoResponse> returnHistoryResponseList);
    }

    interface Presenter {
        void returnHistory(int userId);
    }

}
