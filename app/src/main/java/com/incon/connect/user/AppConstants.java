package com.incon.connect.user;

import java.util.Locale;

public interface AppConstants {

    String TERMS_CONDITIONS_URL = "https://www.google.co.in"; //TODO have to change
    String WEB_IMAGE = "http";
    String MULTIPART_FORM_DATA = "image/*";
    String COMMA_SEPARATOR = ",";
    String HYPHEN_SEPARATOR = "-";
    String NEW_LINE = "\n";
    int DEFAULT_VALUE = Integer.MIN_VALUE;

    String CATEGORY_AUTOMOBILES = "Automobiles";

    String BUILD_FLAVOR = "moonz_dev";
    int VALIDATION_SUCCESS = 0;
    int VALIDATION_FAILURE = -1;
    int VALIDATION_ZIPCODE_LENGTH = 5;
    String DELIMITOR = "-";
    int DEFAULT_GOOGLE_MAP_ZOOM_LEVEL = 16;
    int DEAULT_VALUE = Integer.MAX_VALUE;

    String CUSTOM = "custom";

    interface StatusDrawables {
        int COMPLAINT_ID = 2;
        int COMPLAINT = R.drawable.ic_option_complaint;

        int RECEIVED = R.drawable.ic_option_received;
        int ATTENDING = R.drawable.ic_option_attending;
        int CHECKUP = R.drawable.ic_option_checkup;

        int MANUAL_APPROVAL_ID = 13;
        int APPROVAL = R.drawable.ic_option_approval;
    }

    interface StatusConstants {
        String BUY_REQUEST_ACCEPT = "A";
        String BUY_REQUEST_REJECT = "R";
        String PENDING = "PEN";

        int REJECTED = 10;
        int HOLD = 12;
        int ACCEPTED = 17;
        int APPROVED = 6;
        int WAIT_APPROVE = 15;

    }

    interface ServiceConstants {
        String TIME_10_12 = "10:00,12:00";
        String TIME_12_15 = "12:00,15:00";
        String TIME_15_17 = "15:00,17:00";

        String AUTHORIZED_TYPE = "authorized";
        String UNAUTHORIZED_TYPE = "unauthorized";
    }

    interface FilterConstants {
        String NONE = "none";
        String NAME = "name";
        String BRAND = "brand";
    }

    interface WarrantyRegistrationConstants {
        int WARRANTY_TYPE = 0;
        int MINIMUM_MODELNUMBER_TO_SEARCH = 2;
    }

    interface GoogleMapConstants {
        int GEOCODER_MAX_ADDRESS_RESULTS = 1;
        float DEFAULT_ZOOM_LEVEL = 12.0f;
    }

    interface HttpErrorCodeConstants {
        int ERROR_UNAUTHORIZED = 401;
        int ERROR_OTP_VALIDATION = 428; // pay load error
    }

    interface PushConstants {
        String BUNDLE_TEXT = "text";
        String BUNDLE_TITLE = "title";
        String BUNDLE_EXTRAS = "extras";
    }

    interface LoginValidation {
        int PHONE_NUMBER_REQ = 1;
        int PHONE_NUMBER_NOTVALID = 2;
        int PASSWORD_REQ = 3;
    }

    interface AgeConstants {
        int USER_DOB = 16;
    }

    interface DialogTypeConstants {
        int PRODUCT_FEEDBACK = 0;
        int PRODUCT_SUGGESTIONS = 1;
        int WARRANTY_TYPE = 2;
        int EXTENDED_WARRANTY = 3;
    }

    interface RegistrationValidation {
        int NAME_REQ = 1;
        int PHONE_REQ = 3;
        int PHONE_MIN_DIGITS = 10;
        int GENDER_REQ = 17;
        int DOB_REQ = 13;
        int EMAIL_REQ = 5;
        int EMAIL_NOTVALID = 6;
        int PASSWORD_REQ = 7;
        int PASSWORD_PATTERN_REQ = 11;
        int RE_ENTER_PASSWORD_REQ = 18;
        int RE_ENTER_PASSWORD_DOES_NOT_MATCH = 19;
        int DOB_FUTURE_DATE = 14;
        int DOB_PERSON_LIMIT = 15;


        int CATEGORY_REQ = 21;
        int ADDRESS_REQ = 22;
        int GSTN_REQ = 23;
        int STORE_LOGO = 24;

        int ZIPCODE_REQ = 9;
        int ZIPCODE_INVALID = 12;
        int TIMEZONE_REQ = 8;
    }

    interface PasswordValidation {
        int NEWPWD_REQ = 1;
        int NEWPWD_PATTERN_REQ = 11;
        int CONFIRMPWD_REQ = 2;
        int PWD_NOTMATCH = 3;
    }

    interface ChangeEmailValidation {
        int CURRENT_PWD_REQ = 1;
        int CURRENT_PWD_PATTERN_REQ = 2;
        int EMAIL_REQ = 3;
        int EMAIL_NOTVALID = 4;
    }

    interface ChangeCurrentPasswordValidation {
        int CURRENT_PWD_REQ = 1;
        int NEW_PWD_REQ = 2;
        int NEW_PWD_PATTERN_REQ = 3;
        int CONFIRM_PWD_REQ = 4;
        int CONFIRM_PWD_PATTERN_REQ = 5;
        int NEW_CONFIRM_PWD_NOT_MATCHED = 6;
    }

    interface IntentConstants {
        String MODEL_SEARCH_RESPONSE = "modelSearchResponse";

        String SCANNED_TITLE = "scannedTitle";
        String SCANNED_CODE = "scanedCode";
        String USER_PHONE_NUMBER = "userPhoneNumber";
        String IMAGE_PATH = "imagePath";
        String ADDRESS_COMMA = "addressDetails";
        String LOCATION_COMMA = "locationDetails";
        String FROM_FORGOT_PASSWORD_SCREEN = "fromForgotPasswordScreen";
        String SERVICE_CENTER_DATA = "serviceCenterResponse";
        String BUTTON_TEXT = "buttonText";
        String SERVICE_ENGINEER_DATA = "serviceEngineerData";
        String POSITION = "position";
    }


    interface ViewConstants {
        int PAST_HISTORY = 2;
    }

    interface UpDateUserProfileValidation {
        int NAME_REQ = 1;
        int PHONE_REQ = 3;
        int PHONE_MIN_DIGITS = 10;
        int GENDER_REQ = 17;
        int DOB_REQ = 13;
        int EMAIL_REQ = 5;
        int EMAIL_NOTVALID = 6;
        int PASSWORD_REQ = 7;
        int PASSWORD_PATTERN_REQ = 11;
        int RE_ENTER_PASSWORD_REQ = 18;
        int RE_ENTER_PASSWORD_DOES_NOT_MATCH = 19;
        int DOB_FUTURE_DATE = 14;
        int DOB_PERSON_LIMIT = 15;


        int CATEGORY_REQ = 21;
        int ADDRESS_REQ = 22;
        int GSTN_REQ = 23;
        int STORE_LOGO = 24;

        int ZIPCODE_REQ = 9;
        int ZIPCODE_INVALID = 12;
        int TIMEZONE_REQ = 8;
        int MIN_DAYS = 7;
    }


    interface BundleConstants {
        String QRCODE_DATA = "qrcodeData";
        String SCANNED_QRCODE = "scannedQrcode";
        String WARRANTY_DATA = "warrantyData";
        String ADD_NEW_MODEL_DATA = "addnewmodelData";
        String LOCATION_ADDRESS = "locationAddress";
        String PRODUCT_INFO_RESPONSE = "productInfoResponse";
        String SERVICE_STATUS_RESPONSE = "serviceStatusResponse";

        String FROM_FAVORITES = "fromFavorites";
        String ADDRESS_ID = "addressId";
    }

    interface PushSubTypeConstants {
        String CONNECT_PUSH = "tueoPush";
        String PUSH_DEVICE_TYPE = "android";
    }


    interface CachePrefs {
        String IS_ACCESS_CODE_VERIFIED = "isAccessCodeVerified";
        String IS_AAP_OFFLINE_IMAGE = "isAapOfflineImage";
        String EXTRACT_ZIP = "extractZip";
        String FILTER_NAME = "filterName";
        String IS_SCAN_FIRST = "isScanFirst";
        String TUTORIAL_SHOWED = "tutorialShowed";
    }

    interface LoginPrefs {
        //User details
        String USER_ID = "userId";
        String USER_NAME = "userName";
        String USER_EMAIL_ID = "userEmailId";
        String USER_PHONE_NUMBER = "userPhoneNumber";
        String USER_DOB = "userDob";
        String USER_LOCATION = "location";
        String USER_GENDER = "userGender";
        String USER_ADDRESS = "userAddress";
        String USER_CITY = "userCity";
        String USER_STATE = "userState";
        String USER_POSTAL_CODE = "userPostalCode";
        String USER_PASSWORD = "userPassword";
        String USER_CONFIRM_PASSWORD = "userConfirmPassword";
        String USER_UUID = "uuid";
        String USER_PIN = "user_pin";
        //store details
        String STORE_ID = "storeId";

        String IS_REGISTERED = "isRegistered";
        String IS_FORGOT_PASSWORD = "isForgotPassword";
        String LOGGED_IN = "isLoggedIn";
        String PIN_PROMPT = "pin_prompt";
        String PUSH_TOKEN_STATUS = "pushTokenStatus";
        String ACCESS_TOKEN = "accesstoken";
    }

    interface ApiRequestKeyConstants {
        String HEADER_AUTHORIZATION = "Authorization";
        String BODY_EMAIL = "email";
        String BODY_CUSTOMER_ID = "customerId";
        String BODY_MERCHANT_ID = "merchantId";
        String BODY_INTEREST_ID = "interestId";
        String BODY_QRCODE_ID = "qrCodeId";
        String BODY_COMMENTS = "comments";
        String BODY_PRODUCT_EDIT = "productedit";
        String BODY_USER_ID = "userId";
        String BODY_ADDRESS_ID = "addressId";
        String BODY_ID = "id";
        String BODY_NICK_NAME = "name";
        String BODY_WARRANTY_ID = "warrantyId";
        String BODY_PRODUCT_CODE = "code";
        String BODY_OTP = "otp";
        String BODY_MOBILE_NUMBER = "mobileNumber";
        String STORE_LOGO = "logo";
        String BODY_PASSWORD = "password";
        String BODY_PRODUCT_ID = "productId";
        String BODY_PRODUCT_RATING = "rating";
        String BODY_PRODUCT_REVIEW = "review";
        String BODY_SUGGESTIONS = "suggestions";


        String BODY_PIN = "pin";
    }

    interface RequestCodes {
        int TAKE_PHOTO = 100;
        int PICK_FROM_GALLERY = 101;
        int SEND_IMAGE_PATH = 102;
        int FORGOT_PASSWORD = 104;
        int CHANGE_EMAIL = 110;
        int TERMS_AND_CONDITIONS = 111;
        int ADDRESS_LOCATION = 112;
        int USER_PROFILE_SCAN = 113;
        int PRODUCT_WARRANTY_SCAN = 114;
        int PRODUCT_ASSIGN_SCAN = 115;
        int SERIAL_NO_SCAN = 116;
        int BATCH_NO_SCAN = 117;
        int ADD_NEW_MODEL = 118;
        int LOCATION_LATLNG_FROM_ADDRESS = 119;
        int LOCATION_ADDRESS_FROM_LATLNG = 120;
        int PRODUCT_ADD_FRAGMENT = 121;
        int ADD_CUSTOM_PRODUCT_FRAGMENT = 122;
        int REFRESH_SE_LIST = 123;
        int PIN_PROMPT = 124;
        int DELETE_PRODUCT = 125;
        int LOCATION_CHANGED = 126;
        int BUY_REQUEST = 127;
        int SERVICE_REQUEST = 128;
        int FROM_NEARBY_SERVICE_CENTER = 129;
        int BILL_ACTIVITY = 130;
    }

    interface TimeConstants {
        String AM_LOWER_CASE = "am";
        String AM_UPPER_CASE = "AM";
        String PM_LOWER_CASE = "pm";
        String PM_UPPER_CASE = "PM";
    }

    interface DateFormatterConstants {
        String FROM_API_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String FROM_API = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        /* String LOCAL_DATE_DD_MM_YYYY_HH_MM_SS =
                 "dd-MM-yyyy HH:mm:ss"; //14-08-1987 18:30:00*/
        String LOCAL_DATE_DD_MM_YYYY_HH_MM =
                "dd-MM-yyyy HH:mm"; //14-08-1987 18:30
        String MMMM_YYYY = "MMMM - yyyy"; //June - 2017
        String DD_E_MMMM_YYYY = "dd-E-MMMM-yyyy"; //14-Thursday-Jun-1987
        String MM_DD_YYYY = "MM/dd/yyyy"; //14-Thursday-Jun-1987
        String DD_MM_YYYY = "dd-MM-yyyy"; //14-08-1987
        String MMMM_DD = "MMMM-dd"; //14-08-1987
        String DD_MMMM = "dd MMMM"; //14 July
        String DD_SLASH_MM_SLASH_YYYY = "dd/MM/yyyy"; //14/07/2017
        //Mon, 10 Jul 2017 10:08:20 GM
        String DDMMMM_H_MMA = "ddMMMM, h:mma"; //14July, 2:30PM
        String DD_MM_YYYY_HH_MMA = "dd-MM-yyyy hh:mma"; //14July, 2:30PM
        String YYYY_MM_DD_SLASH = "yyyy/MM/dd"; //2017/01/15
        String YYYY_MM_DD = "yyyy-MM-dd"; //2017-01-15
        String MMMM_SPACE_DD = "MMMM dd"; // March 24 at 11:26am
        String HH_MM_A = "hh:mma"; // March 24 at 11:26am
        String HH = "HH"; // 14
        String YYYY_MMMM_DD = "yyyy-MMMM-dd"; //2017-01-15
        String YYYY_MMM_DD = "yyyy-MMM-dd"; //2017-Jan-15
        String MMM_SPACE_DD = "MMM dd"; //Mar 28
        String MMMM_DD_YYYY = "MMMM dd, yyyy"; //Oct-21-2017
        String DD_NOSPACE_MMMM = "ddMMMM"; //28March

        String TIME_HH_MM = "HH:mm"; //16:20

        String UTC = "UTC";
        Locale DATE_FORMAT_LOCALE = Locale.getDefault();
    }


    interface DateDialogConstants {
        int ADD_OFFER_START_DATE = 0;
        int ADD_OFFER_END_DATE = 1;
        int ADD_OFFER_SCAN_START_DATE = 2;
        int ADD_OFFER_SCAN_END_DATE = 3;
    }

    interface ErrorCodes {
        int UNKNOWN = 0;
        int NETWORK_ERROR = 1;
        int TIMEOUT = 2;
        int NO_NETWORK = 3;
        String NEW_USER = "User not found for provided details";
    }

    interface MenuConstants {
        int PROFILE = 0;
        int CHANGE_PWD = 1;
        int CHANGE_PIN = 2;
        int MANAGE_UNAUTHORIZE_NUMBERS = 3;
        int LOGOUT = 4;


    }

    interface ProductAssignValidation {
        int MODEL = 1;
        int INVALID_MODEL = 2;
        int DESCRIPTION = 3;
        int MRP_PRICE = 4;
        int PRICE = 5;

    }

    interface WarrantyregistationValidation {
        int MODEL = 1;
        int INVALID_MODEL = 2;
        int DESCRIPTION = 3;
        int SERIAL_NO = 4;
        int BATCH_NO = 5;
        int MRP_PRICE = 6;
        int PRICE = 7;
        int INVOICENUMBER = 8;
    }

    interface AddNewModelValidation {
        int MODEL = 1;
        int INVALID_MODEL = 2;
        int DESCRIPTION = 3;
        int CATEGORY = 4;
        int DIVISION = 5;
        int BRAND = 6;
        int MRP_PRICE = 7;
        int PRICE = 8;
        int NOTE = 9;
        int NAME = 10;
    }


    interface Favorites {
        String ADDRESS_TYPE_ONE = "1";
    }

    interface AddOfferValidation {
        int MODEL = 1;
        int INVALID_MODEL = 2;
        int CATEGORY = 3;
        int DIVISION = 4;
        int BRAND = 5;
        int OFFER_START_ON = 6;
        int OFFER_EXPIRE_ON = 7;
        int SCAN_START_DATE = 8;
        int SCAN_END_DATE = 9;
        int OFFER_PRICE = 10;

    }

    public class PushIntentConstants {
        public static final String PUSH_PAYLOAD = "pushPayload";
    }
}
