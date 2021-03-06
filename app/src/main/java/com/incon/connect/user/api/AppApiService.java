package com.incon.connect.user.api;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.BuildConfig;
import com.incon.connect.user.apimodel.base.ApiBaseResponse;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.defaults.DefaultsResponse;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.Brand;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.qrcodebaruser.UserInfoResponse;
import com.incon.connect.user.apimodel.components.registration.SendOtpResponse;
import com.incon.connect.user.apimodel.components.review.ReviewData;
import com.incon.connect.user.apimodel.components.search.Division;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.apimodel.components.validateotp.ValidateWarrantyOtpResponse;
import com.incon.connect.user.custom.exception.NoConnectivityException;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.dto.addnewmodel.AddCustomProductModel;
import com.incon.connect.user.dto.asignqrcode.AssignQrCode;
import com.incon.connect.user.dto.login.LoginUserData;
import com.incon.connect.user.dto.notifications.PushRegistrarBody;
import com.incon.connect.user.dto.registration.Registration;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.dto.update.UpDateUserProfile;
import com.incon.connect.user.dto.updatestatus.UpDateStatus;
import com.incon.connect.user.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;

public class AppApiService implements AppConstants {

    private static AppApiService apiDataService;
    private AppServiceObservable serviceInstance;

    private AppApiService() {
        ServiceGenerator serviceGenerator = new ServiceGenerator(BuildConfig.SERVICE_ENDPOINT);
        serviceInstance = serviceGenerator.getConnectService();
    }

    public static AppApiService getInstance() {
        if (apiDataService == null) {
            apiDataService = new AppApiService();
        }
        return apiDataService;
    }

    private <T> Observable<T> checkNetwork() {
        //Returns NetworkStatusException when there is no network
        //Otherwise return nothing
        if (NetworkUtil.isOnline()) {
            return Observable.never();
        } else {
            return Observable.error(new NoConnectivityException());
        }
    }

    private <T> Observable<T> addNetworkCheck(Observable<T> observable) {
        Observable<T> network = checkNetwork();
        return network
                .ambWith(observable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //default data api
    public Observable<DefaultsResponse> defaultsApi() {
        return addNetworkCheck(serviceInstance.defaultsApi());
    }

    //login api
    public Observable<LoginResponse> login(LoginUserData loginUserData) {
        return addNetworkCheck(serviceInstance
                .login(loginUserData));
    }

    // change password api
    public Observable<LoginResponse> changePassword(HashMap<String, String> password) {
        return addNetworkCheck(serviceInstance.changePassword(password));
    }

    // forgot password api
    public Observable<ApiBaseResponse> forgotPassword(HashMap<String, String> email) {
        return addNetworkCheck(serviceInstance.forgotPassword(email));
    }

    //update pin api using map
    public Observable<Object> updateUserApi(HashMap<String, String> userMap, int userId) {
        return addNetworkCheck(serviceInstance.updateUserApi(userMap, userId));
    }

    //registration api
    public Observable<LoginResponse> register(Registration registrationBody) {
        return addNetworkCheck(serviceInstance.register(registrationBody));
    }

    // user profile update api
    public Observable<LoginResponse> upDateUserProfile(
            int userId, UpDateUserProfile upDateUserProfile) {
        return addNetworkCheck(serviceInstance.upDateUserProfile(userId, upDateUserProfile));
    }

    //  update status api
    public Observable<Object> serviceRequest(ServiceRequest serviceRequest) {
        return addNetworkCheck(serviceInstance.serviceRequest(serviceRequest));
    }

    //registration request otp
    public Observable<Object> registerRequestOtp(String phoneNumber) {
        return addNetworkCheck(serviceInstance.registerRequestOtp(phoneNumber));
    }

    //registration request password otp
    public Observable<Object> registerRequestPasswordOtp(String phoneNumber) {
        return addNetworkCheck(serviceInstance.registerRequestPasswordOtp(phoneNumber));
    }

    public Observable<SendOtpResponse> sendOtp(HashMap<String, String> email) {
        return addNetworkCheck(serviceInstance.sendOtp(email));
    }

    // validate otp api
    public Observable<LoginResponse> validateOtp(HashMap<String, String> verify) {
        return addNetworkCheck(serviceInstance.validateOtp(verify));
    }

    //assign qr code to product api
    public Observable<Object> assignQrCodeToProduct(AssignQrCode qrCode) {
        return addNetworkCheck(serviceInstance.assignQrCodeToProduct(qrCode));
    }

    // check qr Codestatus  api
    public Observable<Object> checkQrCodestatus(String qrCode) {
        return addNetworkCheck(serviceInstance.checkQrCodestatus(qrCode));
    }

    // purchased history  api
    public Observable<List<ProductInfoResponse>> purchasedApi(int userId) {
        return addNetworkCheck(serviceInstance.purchasedApi(userId));
    }

    // get users list of service centers api
    public Observable<List<UsersListOfServiceCenters>> getUsersListOfServiceCenters(int serviceCenterId) {
        return addNetworkCheck(serviceInstance.getUsersListOfServiceCenters(serviceCenterId));
    }

    // add favourites  api
    public Observable<Object> addToFavotites(HashMap<String, String> favoriteMap) {
        return addNetworkCheck(serviceInstance.addToFavotites(favoriteMap));
    }

    // product location change Api
    public Observable<Object> productChangeLocationProductNameEditApi(HashMap<String, String> changeLocationMap) {
        return addNetworkCheck(serviceInstance.productChangeLocationProductNameEditApi(changeLocationMap));
    }

    // delete product api
    public Observable<Object> deleteProduct(int warrantyId) {
        return addNetworkCheck(serviceInstance.deleteProduct(warrantyId));
    }

    // delete favorites Product api
    public Observable<Object> deleteFavoritesProduct(int favouriteId) {
        return addNetworkCheck(serviceInstance.deleteFavoritesProduct(favouriteId));
    }

    //add product to interest api
    public Observable<List<ProductInfoResponse>> interestApi(int userId) {
        return addNetworkCheck(serviceInstance.interestApi(userId));
    }

    public Observable<List<ProductInfoResponse>> storesApi(int userId) {
        return addNetworkCheck(serviceInstance.storesApi(userId));
    }

    //product delete api
    public Observable<Object> deleteApi(int interestId) {
        return addNetworkCheck(serviceInstance.deleteApi(interestId));
    }

    //buy requests api
    public Observable<Object> buyRequestApi(HashMap<String, String> buyRequestBody) {
        return addNetworkCheck(serviceInstance.buyRequestApi(buyRequestBody));
    }

    // product return api
    public Observable<List<ProductInfoResponse>> returnApi(int userId) {
        return addNetworkCheck(serviceInstance.returnApi(userId));
    }

        // product review api
    public Observable<List<ReviewData>> reviewsApi(int productId) {
        return addNetworkCheck(serviceInstance.reviewsApi(productId));
    }

    public Observable<List<ReviewData>> productSuggestionsApi(int userId, int productId) {
        return addNetworkCheck(serviceInstance.productSuggestionsApi(userId, productId));
    }

    public Observable<Object> saveReviewsApi(HashMap<String, String> saveReviewsBody) {
        return addNetworkCheck(serviceInstance.saveReviewsApi(saveReviewsBody));
    }

    // fetch nearby service centers
    public Observable<List<ServiceCenterResponse>> findNearByServiceCenters(String type, int brandId, int userId) {
        return addNetworkCheck(serviceInstance.findNearByServiceCenters(type,brandId, userId));
    }

    //  user addresses api
    public Observable<Object> addProductAddress(AddUserAddress addUserAddress) {
        return addNetworkCheck(serviceInstance.addProductAddress(addUserAddress));
    }

    //  favourites addresse api
    public Observable<List<ProductInfoResponse>> favouritesProductApi(int userId, int customerId) {
        return addNetworkCheck(serviceInstance.favouritesProductApi(userId, customerId));
    }

    // getting user addresses api
    public Observable<List<AddUserAddressResponse>> getAddressesApi(int userId) {
        return addNetworkCheck(serviceInstance.getAddressesApi(userId));
    }

    public Observable<UserInfoResponse> userInfoUsingQrCode(String qrCode) {
        return addNetworkCheck(serviceInstance.userInfoUsingQrCode(qrCode));
    }

    // user intereste api
    public Observable<ProductInfoResponse> userInterestedUsingQrCode(int customerId,
                                                                     HashMap<String, String> qrCode) {
        return addNetworkCheck(serviceInstance.userInterestedUsingQrCode(customerId, qrCode));
    }

    // new user registation  api
    public Observable<UserInfoResponse> newUserRegistation(String phoneNumber) {
        return addNetworkCheck(serviceInstance.newUserRegistation(phoneNumber));
    }

    // getting product details from qr code
    public Observable<ProductInfoResponse> productInfoUsingQrCode(HashMap<String, String> qrCode) {
        return addNetworkCheck(serviceInstance.productInfoUsingQrCode(qrCode));
    }

    //search modelNumber  api
    public Observable<List<ModelSearchResponse>> modelNumberSearch(String modelNumber) {
        return addNetworkCheck(serviceInstance.modelNumberSearch(modelNumber));
    }

    //FetchCategories api
    public Observable<List<FetchCategories>> getCategories(int merchantId) {
        return addNetworkCheck(serviceInstance.getCategories(merchantId));
    }

    //warranty registration validate otp api
    public Observable<ValidateWarrantyOtpResponse> validateWarrantyOtp(HashMap<String, String>
                                                                               verify) {
        return addNetworkCheck(serviceInstance.validateWarrantyOtp(verify));
    }

    //warranty registration request otp api
    public Observable<Object> warrantyRequestOtp(String phoneNumber) {
        return addNetworkCheck(serviceInstance.warrantyRequestOtp(phoneNumber));
    }

    //transfer  api
    public Observable<Object> transferRequest(String phoneNumber, String warrantyId) {
        return addNetworkCheck(serviceInstance.transferRequest(phoneNumber, warrantyId));
    }

    //transfer request otp api
    public Observable<ArrayList<ServiceStatus>> fetchProductPastHistory(int userId, int warrantyId) {
        return addNetworkCheck(serviceInstance.fetchProductPastHistory(userId, warrantyId));
    }

    //transfer request otp api
    public Observable<ArrayList<ServiceStatus>> fetchUserRequests(int userId) {
        return addNetworkCheck(serviceInstance.fetchUserRequests(userId));
    }

    // get status list api
    public Observable<List<DefaultStatusData>> getStatusList() {
        return addNetworkCheck(serviceInstance.getStatusList());
    }

    //add new model api
    public Observable<ModelSearchResponse> addingNewModel(int merchantId,
                                                          AddCustomProductModel addCustomProductModelBody) {
        return addNetworkCheck(serviceInstance.addingNewModel(merchantId, addCustomProductModelBody));
    }

    /* public Observable<Object> assignQrCodeToProduct(AssignQrCode qrCode) {
        return addNetworkCheck(serviceInstance.assignQrCodeToProduct(qrCode));
    }*/
// push token  api
    public Observable<Object> pushTokenApi(int userId, PushRegistrarBody pushRegistrarBody) {
        return addNetworkCheck(serviceInstance.pushTokenApi(userId, pushRegistrarBody));
    }

    public Observable<ProductInfoResponse> addServiceEngineer(AddServiceEngineer serviceEngineer, int userId) {
        return addNetworkCheck(serviceInstance.addServiceEngineer(serviceEngineer, userId));
    }

    public Observable<List<Division>> divisionsFromCategoryId(int categoryId) {
        return addNetworkCheck(serviceInstance.divisionsFromCategoryId(categoryId));
    }

    public Observable<List<Brand>> brandsFromDivisionId(int divisionId) {
        return addNetworkCheck(serviceInstance.brandsFromDivisionId(divisionId));
    }

    public Observable<Object> addingCustomProduct(AddCustomProductModel addCustomProductModel) {
        return addNetworkCheck(serviceInstance.addingCustomProduct(addCustomProductModel));
    }

    public Observable<List<ProductInfoResponse>> purchasedStatus(int userId) {
        return addNetworkCheck(serviceInstance.purchasedStatus(userId));
    }

    // update status api
    public Observable<Object> upDateStatus(int userId, UpDateStatus upDateStatus) {
        return addNetworkCheck(serviceInstance.upDateStatus(userId, upDateStatus));
    }

    // bill  api
    public Observable<Object> uploadBill(int purchaseId, MultipartBody.Part billPrev) {
        return addNetworkCheck(serviceInstance.uploadBill(purchaseId, billPrev));
    }

}
