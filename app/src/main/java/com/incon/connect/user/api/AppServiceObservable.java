package com.incon.connect.user.api;

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
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.dto.addnewmodel.AddCustomProductModel;
import com.incon.connect.user.dto.asignqrcode.AssignQrCode;
import com.incon.connect.user.dto.login.LoginUserData;
import com.incon.connect.user.dto.notifications.PushRegistrarBody;
import com.incon.connect.user.dto.registration.Registration;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.dto.update.UpDateUserProfile;
import com.incon.connect.user.dto.updatestatus.UpDateStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AppServiceObservable {


    @GET("service/getstatuslist")
    Observable<List<DefaultStatusData>> getStatusList();

    //default data api
    @GET("defaults")
    Observable<DefaultsResponse> defaultsApi();

    //login api
    @POST("user/login")
    Observable<LoginResponse> login(@Body LoginUserData loginUserData);

    //registration api
    @POST("user/register")
    Observable<LoginResponse> register(@Body Registration registrationBody);

    //registration request otp
    @GET("user/requestotp/{phoneNumber}/register")
    Observable<Object> registerRequestOtp(@Path("phoneNumber") String phoneNumber);

    //registration request password otp
    @GET("user/requestotp/{phoneNumber}/password")
    Observable<Object> registerRequestPasswordOtp(@Path("phoneNumber") String phoneNumber);

    // user profile update api
    @POST("user/updateuser/{userId}")
    Observable<LoginResponse> upDateUserProfile(@Path(
            "userId") int userId, @Body UpDateUserProfile upDateUserProfile);

    //  service request api
    @POST("service/addservicerequest")
    Observable<Object> serviceRequest(@Body ServiceRequest serviceRequest);

    @POST("account/sendOtp")
    Observable<SendOtpResponse> sendOtp(@Body HashMap<String, String> email);

    @POST("user/validateotp")
    Observable<LoginResponse> validateOtp(@Body HashMap<String, String> verify);

    // forgotpassword api
    @POST("merchant/forgotpassword")
    Observable<ApiBaseResponse> forgotPassword(@Body HashMap<String, String> phoneNumber);

    // change password api
    @POST("merchant/changepassword")
    Observable<LoginResponse> changePassword(@Body HashMap<String, String> password);

    // check qr Codestatus  api
    @GET("product/checkqropnestatus/{qrCode}")
    Observable<Object> checkQrCodestatus(@Path("qrCode") String qrCode);

    // purchased history  api
    @GET("user/history/purchased/{userId}")
    Observable<List<ProductInfoResponse>> purchasedApi(@Path("userId") int userId);

    // get users list of service centers
    @GET("service/getuserslist/{serviceCenterId}")
    Observable<List<UsersListOfServiceCenters>> getUsersListOfServiceCenters(@Path("serviceCenterId") int serviceCenterId);


    // add favourites  api
    @POST("user/addtofavourites")
    Observable<Object> addToFavotites(@Body HashMap<String, String> favoriteMap);

    // product change location  api
    @POST("user/modifyfavourites")
    Observable<Object> productChangeLocationProductNameEditApi(@Body HashMap<String, String> changeLocationMap);

    // fetch near by service centers  api
    @POST("service/nearbycenters/{type}/{brandId}/{userId}")
    Observable<List<ServiceCenterResponse>> findNearByServiceCenters(@Path("type") String type,@Path("brandId") int brandId, @Path("userId") int userId);


    // delete purchased product api
    @GET("user/history/deletepurchased/{warrantyId}")
    Observable<Object> deleteProduct(@Path("warrantyId") int warrantyId);


    // delete Favorites product api
    @GET("user/deletefavourites/{favouriteId}")
    Observable<Object> deleteFavoritesProduct(@Path("favouriteId") int favouriteId);

    //assign qr code to product api
    @POST("product/assign")
    Observable<Object> assignQrCodeToProduct(@Body AssignQrCode qrCode);

    //interested history  api
    @GET("user/history/interested/{userId}")
    Observable<List<ProductInfoResponse>> interestApi(@Path("userId") int userId);

    //get stores   api
    @GET("user/getstores/{userId}")
    Observable<List<ProductInfoResponse>> storesApi(@Path("userId") int userId);

    //delete interest product api
    @GET("user/history/deleteinterested/{interestId}")
    Observable<Object> deleteApi(@Path("interestId") int interestId);

    //buy requests api
    @POST("user/buyrequest")
    Observable<Object> buyRequestApi(@Body HashMap<String, String> buyRequestBody);


    @POST("user/updateuser/{userId}")
    Observable<Object> updateUserApi(@Body HashMap<String, String> pinMap, @Path("userId") int userId);

    //return history  api
    @GET("user/history/return/{userId}")
    Observable<List<ProductInfoResponse>> returnApi(@Path("userId") int userId);

    // getting user addresses api
    @GET("user/getaddresses/{userId}")
    Observable<List<AddUserAddressResponse>> getAddressesApi(@Path("userId") int userId);

    //  user addresses api
    @POST("user/addaddress")
    Observable<Object> addProductAddress(@Body AddUserAddress addUserAddress);

    //  favourites addresse api
    @GET("user/favourites/{userId}/{addressId}")
    Observable<List<ProductInfoResponse>> favouritesProductApi(
            @Path("userId") int userId, @Path("addressId") int addressId);

    @GET("user/getuser/scan/{qrCode}/")
    Observable<UserInfoResponse> userInfoUsingQrCode(@Path("qrCode") String qrCode);

    // user intereste api
    @POST("user/interested/{customerId}")
    Observable<ProductInfoResponse> userInterestedUsingQrCode(@Path("customerId") int customerId, @Body
            HashMap<String,
                    String> qrCode);

    @POST("product/getproduct")
    Observable<ProductInfoResponse> productInfoUsingQrCode(@Body HashMap<String, String> qrCode);

    //search modelNumber  api
    @GET("product/search/{modelNumber}")
    Observable<List<ModelSearchResponse>> modelNumberSearch(@Path("modelNumber")
                                                                    String modelNumber);

    //FetchCategories api
    @GET("merchant/getcategories/{merchantId}")
    Observable<List<FetchCategories>> getCategories(@Path("merchantId") int merchantId);

    //add new model api
    @POST("product/addnew/{merchantId}")
    Observable<ModelSearchResponse> addingNewModel(@Path("merchantId") int merchantId,
                                                   @Body AddCustomProductModel addCustomProductModelBody);

    //warranty registration validateotp api
    @POST("warranty/validateotp")
    Observable<ValidateWarrantyOtpResponse> validateWarrantyOtp(@Body HashMap<String, String>
                                                                        verify);

    //warranty registration requestotp api
    @GET("warranty/requestotp/{phoneNumber}/password")
    Observable<Object> warrantyRequestOtp(@Path("phoneNumber") String phoneNumber);

    //transfer product api
    @GET("user/transfer/{phoneNumber}/{warrantyId}")
    Observable<Object> transferRequest(@Path("phoneNumber") String phoneNumber,
                                       @Path("warrantyId") String warrantyId);

    //transfer product api
    @GET("user/servicerequests/{userId}")
    Observable<ArrayList<ServiceStatus>> fetchUserRequests(@Path("userId") int userId);


    // product review api
    @GET("product/reviews/{productId}")
    Observable<List<ReviewData>> reviewsApi(@Path("productId") int productId);

    @POST("product/savereviews")
    Observable<Object> saveReviewsApi(@Body HashMap<String, String> savereviewsBody);


    // new user registation  api
    @POST("user/newuser/{phoneNumber}")
    Observable<UserInfoResponse> newUserRegistation(@Path("phoneNumber")
                                                            String phoneNumber);

    // push token  api
    @POST("user/updatefcmtoken/{userId}")
    Observable<Object> pushTokenApi(@Path("userId") int userId, @Body PushRegistrarBody
            pushRegistrarBody);


    @POST("service/addserviceengineer/{userId}")
    Observable<ProductInfoResponse> addServiceEngineer(@Body AddServiceEngineer serviceEngineer, @Path("userId") int userId);

    @GET("defaults/divisions/{categoryId}")
    Observable<List<Division>> divisionsFromCategoryId(@Path("categoryId") int categoryId);

    @GET("defaults/brands/{divisionId}")
    Observable<List<Brand>> brandsFromDivisionId(@Path("divisionId") int divisionId);

    @POST("product/user/addproduct")
    Observable<Object> addingCustomProduct(@Body AddCustomProductModel addCustomProductModel);

    @GET("user/productstatus/{userId}")
    Observable<List<ProductInfoResponse>> purchasedStatus(@Path("userId") int userId);

    //  update status api
    @POST("service/updateStatus/{userId}")
    Observable<Object> upDateStatus(@Path("userId") int userId, @Body UpDateStatus upDateStatus);

    // service center logo  api
    @Multipart
    @POST("warranty/updatebill/{purchaseId}")
    Observable<Object> uploadBill(@Path("purchaseId") int purchaseId, @Part MultipartBody.Part billPrev);
}
