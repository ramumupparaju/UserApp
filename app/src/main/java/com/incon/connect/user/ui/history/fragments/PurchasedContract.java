package com.incon.connect.user.ui.history.fragments;


import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.review.ReviewData;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.BaseView;

import java.util.HashMap;
import java.util.List;

/**
 * Created on 31 May 2017 11:18 AM.
 */
public interface PurchasedContract {

    interface View extends BaseView {
        void loadPurchasedHistory(List<ProductInfoResponse> productInfoResponses);

        void loadAddresses(List<AddUserAddressResponse> favoritesResponseList);

        void transferMobileNumber(Object response);

        void deleteProduct(Object response);

        void loadServiceRequest();

        void loadNearByServiceCenters(List<ServiceCenterResponse> serviceCenterResponses);

        void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> listOfServiceCenters);

        void addedServiceEngineer(ProductInfoResponse productInfoResponse);

        void addedToFavorite();

        void productReviews(List<ReviewData> reviewDataList);

        void productSuggestions(List<ReviewData> reviewDataList);

        void saveReviews(Object saveReviews);

    }

    interface Presenter {
        void purchased(int userId);

        void doGetAddressApi(int userId);

        void doTransferProductApi(String phoneNumber, String warrantyId);

        void addToFavotites(HashMap<String, String> favoritesMap);

        void saveReviewsApi(HashMap<String, String> reviewsMap);

        void deleteProduct(int userId);

        void reviewToProduct(int productId);

        void doProductSuggestions(int userId, int productId);

        void serviceRequest(ServiceRequest serviceRequest);

        void nearByServiceCenters(String type, int brandId, int userId);

        void getUsersListOfServiceCenters(int serviceCenterId);

        void addServiceEngineer(AddServiceEngineer serviceEngineer, int userId);
    }

}
