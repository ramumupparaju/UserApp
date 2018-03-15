package com.incon.connect.user.ui.favorites;

import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.BaseView;

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

        void productReviews();

        void saveReviews(Object saveReviews);
    }

    interface Presenter {


        void doGetAddressApi(int userId);

        void reviewToproduct(int userId);

        void saveReviewsApi(HashMap<String, String> reviewsMap);

        void doFavoritesProductApi(int userId, int addressId);

        void doLocationChangeProductNameEditApi(HashMap<String, String> favoritesMap);

        void doAddAddressApi(AddUserAddress addUserAddress);

        void serviceRequest(ServiceRequest serviceRequest);

        void nearByServiceCenters(int type,int brandId, int userId);

        void getUsersListOfServiceCenters(int serviceCenterId);

        void addServiceEngineer(AddServiceEngineer serviceEngineer, int userId);

        void deleteFovoriteProduct(int favouriteId);

        void doTransferProductApi(String phoneNumber, String warrantyId);
    }
}
