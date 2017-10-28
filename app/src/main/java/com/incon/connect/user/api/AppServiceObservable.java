package com.incon.connect.user.api;

import com.incon.connect.user.apimodel.base.ApiBaseResponse;
import com.incon.connect.user.apimodel.components.addoffer.AddOfferMerchantFragmentResponse;
import com.incon.connect.user.apimodel.components.buyrequest.BuyRequestResponse;
import com.incon.connect.user.apimodel.components.defaults.DefaultsResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.history.purchased.InterestHistoryResponse;
import com.incon.connect.user.apimodel.components.history.purchased.PurchasedHistoryResponse;
import com.incon.connect.user.apimodel.components.history.purchased.ReturnHistoryResponse;
import com.incon.connect.user.apimodel.components.login.LoginResponse;
import com.incon.connect.user.apimodel.components.qrcodebaruser.UserInfoResponse;
import com.incon.connect.user.apimodel.components.qrcodeproduct.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.registration.SendOtpResponse;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.apimodel.components.updatestoreprofile.UpDateStoreProfileResponce;
import com.incon.connect.user.apimodel.components.updateuserprofile.UpDateUserProfileResponce;
import com.incon.connect.user.apimodel.components.validateotp.ValidateWarrantyOtpResponse;
import com.incon.connect.user.dto.addnewmodel.AddNewModel;
import com.incon.connect.user.dto.addoffer.AddOfferRequest;
import com.incon.connect.user.dto.asignqrcode.AssignQrCode;
import com.incon.connect.user.dto.login.LoginUserData;
import com.incon.connect.user.dto.notifications.PushRegistrarBody;
import com.incon.connect.user.dto.registration.Registration;
import com.incon.connect.user.dto.update.UpDateStoreProfile;
import com.incon.connect.user.dto.update.UpDateUserProfile;
import com.incon.connect.user.dto.warrantyregistration.WarrantyRegistration;

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

    @GET("defaults")
    Observable<DefaultsResponse> defaultsApi();

    @POST("login")
    Observable<LoginResponse> login(@Body LoginUserData loginUserData);

    @POST("merchant/register")
    Observable<LoginResponse> register(@Body Registration registrationBody);

    @POST("merchant/updatemerchant/{merchantId}")
    Observable<UpDateUserProfileResponce> upDateUserProfile(@Path(
            "merchantId") int merchantId, @Body UpDateUserProfile upDateUserProfile);

    @POST("merchant/updatestoredetails/{merchantId}")
    Observable<UpDateStoreProfileResponce> upDateStoreProfile(@Path(
            "merchantId") int merchantId, @Body UpDateStoreProfile dateStoreProfile);

    @GET("user/requestotp/{phoneNumber}/register")
    Observable<Object> registerRequestOtp(@Path("phoneNumber") String phoneNumber);

    @GET("user/requestotp/{phoneNumber}/password")
    Observable<Object> registerRequestPasswordOtp(@Path("phoneNumber") String phoneNumber);

    @Multipart
    @POST("merchant/logoupdate/{storeId}")
    Observable<Object> uploadStoreLogo(@Path("storeId") String storeId,
                                       @Part MultipartBody.Part storeLogo);

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

    //    TODO Change purchased to interest
//    user/history/interested/78
    @GET("user/history/interested/{userId}")
    Observable<List<InterestHistoryResponse>> interestApi(@Path("userId") int userId);

//user/history/deleteinterested/63
    @GET("user/history/deleteinterested/{userId}")
    Observable<Object> deleteApi(@Path("userId") int userId);


    //    TODO Change purchased to return
    @GET("merchant/history/purchased/{userId}")
    Observable<List<ReturnHistoryResponse>> returnApi(@Path("userId") int userId);

    @GET("merchant/buy-requests/{userId}")
    Observable<List<BuyRequestResponse>> buyRequestApi(@Path("userId") int userId);

    @POST("offers/addoffers")
    Observable<AddOfferMerchantFragmentResponse> addOffer(@Body AddOfferRequest addOfferRequest);

    @GET("user/warranty/getuser/{phoneNumber}")
    Observable<UserInfoResponse> userInfoUsingPhoneNumber(@Path("phoneNumber") String phoneNumber);

    @GET("user/getuser/scan/{qrCode}/")
    Observable<UserInfoResponse> userInfoUsingQrCode(@Path("qrCode") String qrCode);

    @GET("user/interested/{qrCode}/")
    Observable<Object> userInterestedUsingQrCode(@Path("qrCode") String qrCode);

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
