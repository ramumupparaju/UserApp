package com.incon.connect.user.ui.history.fragments;

import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.showroom.ShowRoomResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 1/23/2018.
 */

public interface ShowRoomContract {
    interface View extends BaseView {
        void loadStores(List<ProductInfoResponse>  showRoomResponseList);
    }

    interface Presenter {
        void storesList(int userId);
    }

}
