package com.incon.connect.user.ui.history.fragments;


import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;
import java.util.List;

/**
 * Created on 31 May 2017 11:18 AM.
 *
 */
public interface PurchasedContract {

    interface View extends BaseView {
        void loadPurchasedHistory(List<ProductInfoResponse> productInfoResponses);
        void loadAddresses(List<AddUserAddressResponse> favoritesResponseList);
        void addedToFavorite();
        void transferMobileNumber(Object response);
        void deleteProduct(Object response);
        void serviceRequest();
    }

    interface Presenter {
        void purchased(int userId);
        void doGetAddressApi(int userId);
        void doTransferProductApi(String phoneNumber , int userId);
        void addToFavotites(HashMap<String, String> favoritesMap);
        void deleteProduct(int userId);
        void serviceRequest(int userId, ServiceRequest serviceRequest);
    }

}
