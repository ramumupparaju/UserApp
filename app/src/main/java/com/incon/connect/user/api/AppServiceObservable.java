package com.incon.connect.user.api;

import com.incon.connect.user.apimodel.base.ApiBaseResponse;
import com.incon.connect.user.apimodel.components.addoffer.AddOfferMerchantFragmentResponse;
import com.incon.connect.user.apimodel.components.defaults.DefaultsResponse;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.history.purchased.InterestHistoryResponse;
import com.incon.connect.user.apimodel.components.history.purchased.PurchasedHistoryResponse;
import com.incon.connect.user.apimodel.components.history.purchased.ReturnHistoryResponse;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.qrcodebaruser.UserInfoResponse;
import com.incon.connect.user.apimodel.components.qrcodeproduct.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.registration.SendOtpResponse;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.apimodel.components.validateotp.ValidateWarrantyOtpResponse;
import com.incon.connect.user.dto.addfavorites.Favorites;
import com.incon.connect.user.dto.addnewmodel.AddNewModel;
import com.incon.connect.user.dto.addoffer.AddOfferRequest;
import com.incon.connect.user.dto.asignqrcode.AssignQrCode;
import com.incon.connect.user.dto.login.LoginUserData;
import com.incon.connect.user.dto.notifications.PushRegistrarBody;
import com.incon.connect.user.dto.registration.Registration;
import com.incon.connect.user.dto.update.UpDateUserProfile;
import com.incon.connect.user.dto.warrantyregistration.WarrantyRegistration;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AppServiceObservable {

    @GET("defaults")
    Observable<DefaultsResponse> defaultsApi();

    @POST("user/login")
    Observable<LoginResponse> login(@Body LoginUserData loginUserData);

    @POST("user/register")
    Observable<LoginResponse> register(@Body Registration registrationBody);

    @GET("user/requestotp/{phoneNumber}/register")
    Observable<Object> registerRequestOtp(@Path("phoneNumber") String phoneNumber);

    @GET("user/requestotp/{phoneNumber}/password")
    Observable<Object> registerRequestPasswordOtp(@Path("phoneNumber") String phoneNumber);

    @POST("user/updateuser/{userId}")
    Observable<LoginResponse> upDateUserProfile(@Path(
            "userId") int userId, @Body UpDateUserProfile upDateUserProfile);

    @POST("account/sendOtp")
    Observable<SendOtpResponse> sendOtp(@Body HashMap<String, String> email);

    @POST("user/validateotp")
    Observable<LoginResponse> validateOtp(@Body HashMap<String, String> verify);

    @POST("merchant/forgotpassword")
    Observable<ApiBaseResponse> forgotPassword(@Body HashMap<String, String> phoneNumber);

    @POST("merchant/changepassword")
    Observable<LoginResponse> changePassword(@Body HashMap<String, String> password);

    @GET("product/checkqropnestatus/{qrCode}")
    Observable<Object> checkQrCodestatus(@Path("qrCode") String qrCode);
//connect/user/history/purchased/45
    @GET("user/history/purchased/{userId}")
    Observable<List<PurchasedHistoryResponse>> purchasedApi(@Path("userId") int userId);

    @POST("product/assign")
    Observable<Object> assignQrCodeToProduct(@Body AssignQrCode qrCode);

//    user/history/interested/78
    @GET("user/history/interested/{userId}")
    Observable<List<InterestHistoryResponse>> interestApi(@Path("userId") int userId);

//user/history/deleteinterested/63
    @GET("user/history/deleteinterested/{interestId}")
    Observable<Object> deleteApi(@Path("interestId") int interestId);

    @GET("user/history/return/{userId}")
    Observable<List<ReturnHistoryResponse>> returnApi(@Path("userId") int userId);

    @GET("user/favourites/{purchasedId}/{userId}")
    Observable<List<FavoritesResponse>> favouritesProductApi(
            @Path("userId") int userId, @Path("purchasedId") int purchasedId);

    @POST("user/addtofavourites")
    Observable<FavoritesResponse> addtofavourites(@Body Favorites favorites);

    @POST("offers/addoffers")
    Observable<AddOfferMerchantFragmentResponse> addOffer(@Body AddOfferRequest addOfferRequest);

    @GET("user/warranty/getuser/{phoneNumber}")
    Observable<UserInfoResponse> userInfoUsingPhoneNumber(@Path("phoneNumber") String phoneNumber);

    @GET("user/getuser/scan/{qrCode}/")
    Observable<UserInfoResponse> userInfoUsingQrCode(@Path("qrCode") String qrCode);

    @POST("user/interested/{customerId}/")
    Observable<Object> userInterestedUsingQrCode(@Path("customerId") int customerId, @Body
            HashMap<String,
            String> qrCode);

    @POST("product/getproduct")
    Observable<ProductInfoResponse> productInfoUsingQrCode(@Body HashMap<String, String> qrCode);

    @GET("product/search/{modelNumber}")
    Observable<List<ModelSearchResponse>> modelNumberSearch(@Path("modelNumber")
                                                                    String modelNumber);

    @GET("merchant/getcategories/{merchantId}")
    Observable<List<FetchCategories>> getCategories(@Path("merchantId") int merchantId);

    @POST("product/addnew/{merchantId}")
    Observable<ModelSearchResponse> addingNewModel(@Path("merchantId") int merchantId,
                                                   @Body AddNewModel addNewModelBody);

    @POST("warranty/register")
    Observable<Object> warrantyRegisterApi(@Body WarrantyRegistration warrantyRegistration);

    @POST("warranty/validateotp")
    Observable<ValidateWarrantyOtpResponse> validateWarrantyOtp(@Body HashMap<String, String>
                                                                        verify);

    @GET("warranty/requestotp/{phoneNumber}/password")
    Observable<Object> warrantyRequestOtp(@Path("phoneNumber") String phoneNumber);

    @POST("user/newuser/{phoneNumber}")
    Observable<UserInfoResponse> newUserRegistation(@Path("phoneNumber")
                                                            String phoneNumber);

    @POST("registerPush")
    Observable<Object> pushTokenApi(@Body PushRegistrarBody pushRegistrarBody);


}
