package com.incon.connect.user.ui.status;

import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by PC on 12/1/2017.
 */

public interface StatusContract {
    interface View extends BaseView {
        void loadServiceRequests(ArrayList<ProductInfoResponse> productStatusArrayList,
                            ArrayList<ServiceStatus> serviceStatusArrayList);
    }
    interface Presenter {
        void fetchUserRequests(int userId);
        void getDefaultStatusData(int userId);
    }
}
