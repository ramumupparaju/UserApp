package com.incon.connect.user.ui.favorites;

import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.review.ReviewData;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 11/4/2017.
 */

public interface FavoritesContract {
    interface View extends BaseView {
        void loadAddresses(List<AddUserAddressResponse> favoritesResponseList);

        void onLocationChanged();

        void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList);

        void loadServiceRequest();

        void loadNearByServiceCenters(List<ServiceCenterResponse> serviceCenterResponses);

        void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> listOfServiceCenters);

        void addedServiceEngineer(ProductInfoResponse productInfoResponse);

        void transferMobileNumber(Object response);

        void deleteProduct(Object response);

        void productReviews(List<ReviewData> reviewDataList);

        void productSuggestions(List<ReviewData> reviewDataList);

        void saveReviews(Object saveReviews);

        void onProductPastHistoryApi(ArrayList<ServiceStatus> statusListResponses);

    }

    interface Presenter {


        void doGetAddressApi(int userId);

        void reviewToproduct(int productId);

        void doProductSuggestions(int userId, int productId);

        void saveReviewsApi(HashMap<String, String> reviewsMap);

        void doFavoritesProductApi(int userId, int addressId);

        void doLocationChangeProductNameEditApi(HashMap<String, String> favoritesMap);

        void doAddAddressApi(AddUserAddress addUserAddress);

        void serviceRequest(ServiceRequest serviceRequest);

        void nearByServiceCenters(String type, int brandId, int userId);

        void getUsersListOfServiceCenters(int serviceCenterId);

        void addServiceEngineer(AddServiceEngineer serviceEngineer, int userId);

        void deleteFovoriteProduct(int favouriteId);

        void doTransferProductApi(String phoneNumber, String warrantyId);

        void doProductPastHistoryApi(int userId);
    }
}
