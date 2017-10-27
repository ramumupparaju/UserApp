package com.incon.connect.user.ui.warrantyregistration;

import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.dto.warrantyregistration.WarrantyRegistration;
import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 9/26/2017.
 */
public interface WarrantRegistrationContract {
    interface View extends BaseView {
        void loadModelNumberData(List<ModelSearchResponse> modelSearchResponseList);
        void warrantyRegistered(Object warrantyRegisteredResponse);
        void validateUserOTP();
    }

    interface Presenter {
        void doModelSearchApi(String modelNumberToSearch);
        void doWarrantyRegistrationApi(WarrantyRegistration warrantyRegistration);
        void validateUserOTP(HashMap<String, String> verifyOTP);
        void resendUserOTP(String phoneNumber);
    }
}
