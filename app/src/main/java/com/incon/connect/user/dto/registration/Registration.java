package com.incon.connect.user.dto.registration;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.ValidationUtils;

import java.util.Calendar;

import static com.incon.connect.user.AppConstants.RegistrationValidation.DOB_FUTURE_DATE;
import static com.incon.connect.user.AppConstants.RegistrationValidation.DOB_PERSON_LIMIT;
import static com.incon.connect.user.AppConstants.VALIDATION_SUCCESS;

public class Registration extends BaseObservable {


    private String name;
    @SerializedName("mobileNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("userEmail")
    @Expose
    private String emailId;
    private String password;
    private transient String confirmPassword;
    @SerializedName("gender")
    @Expose
    private String genderType;
    private String dob;
    private transient String dateOfBirthToShow;
    @SerializedName("address")
    @Expose
    private String userAddress;
    @SerializedName("location")
    @Expose
    private String userLocation;

    //Store details
    private String storeName;
    private String storeEmail;
    private String storeCategoryNames;
    @SerializedName("storeCategory")
    @Expose
    private String storeCategoryIds;
    private String storeLocation;
    private String storeLogo;
    private String storeAddress;
    @SerializedName("gstn")
    @Expose
    private String storeGSTN;
    @SerializedName("contactNumber")
    @Expose
    private String storePhoneNumber;

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    @Bindable
    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String genderType) {
        this.genderType = genderType;
        notifyChange();
    }

    @Bindable
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        notifyChange();
    }

    @Bindable
    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
        notifyChange();
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyChange();
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyChange();
    }

    @Bindable
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
        notifyChange();
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyChange();
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Bindable
    public String getDateOfBirthToShow() {
        return dateOfBirthToShow;
    }

    public void setDateOfBirthToShow(String dateOfBirthToShow) {
        this.dateOfBirthToShow = dateOfBirthToShow;
        dob = DateUtils.convertDateToAnotherFormat(dateOfBirthToShow, AppConstants
                .DateFormatterConstants.MM_DD_YYYY, AppConstants.DateFormatterConstants
                .MM_DD_YYYY);
        notifyChange();
    }


    public Pair<String, Integer> validateUserInfo(String tag) {

        int fieldId = AppConstants.VALIDATION_FAILURE;
        if (tag == null) {
            for (int i = 0; i <= 7; i++) {
                fieldId = validateUserFields(i, true);
                if (fieldId != VALIDATION_SUCCESS) {
                    tag = i + "";
                    break;
                }
            }
        } else {
            fieldId = validateUserFields(Integer.parseInt(tag), false);
        }

        return new Pair<>(tag, fieldId);
    }

    private int validateUserFields(int id, boolean emptyValidation) {
        switch (id) {
            case 0:
                if (emptyValidation && TextUtils.isEmpty(getName())) {
                    return AppConstants.RegistrationValidation.NAME_REQ;
                }
                break;

            case 1:
                boolean phoneEmpty = TextUtils.isEmpty(getPhoneNumber());
                if (emptyValidation && phoneEmpty) {
                    return AppConstants.RegistrationValidation.PHONE_REQ;
                } else if (!phoneEmpty && !ValidationUtils.isPhoneNumberValid(getPhoneNumber())) {
                    return AppConstants.RegistrationValidation.PHONE_MIN_DIGITS;
                }
                break;

            case 2:
                boolean genderTypeEmpty = TextUtils.isEmpty(getGenderType());
                if (emptyValidation && genderTypeEmpty) {
                    return AppConstants.RegistrationValidation.GENDER_REQ;
                }
                break;

            case 3:
                boolean dobEmpty = TextUtils.isEmpty(getDob());
                if (emptyValidation && dobEmpty) {
                    return AppConstants.RegistrationValidation.DOB_REQ;
                } else if (!dobEmpty) {
                    return validateDob();
                }
                break;

            case 4:
                boolean emailEmpty = TextUtils.isEmpty(getEmailId());
                if (emptyValidation && emailEmpty) {
                    return AppConstants.RegistrationValidation.EMAIL_REQ;
                } else if (!emailEmpty && !ValidationUtils.isValidEmail(getEmailId())) {
                    return AppConstants.RegistrationValidation.EMAIL_NOTVALID;
                }
                break;

            case 5:
                boolean passwordEmpty = TextUtils.isEmpty(getPassword());
                if (emptyValidation && passwordEmpty) {
                    return AppConstants.RegistrationValidation.PASSWORD_REQ;
                } else if (!passwordEmpty && !ValidationUtils
                        .isPasswordValid(getPassword())) {
                    return AppConstants.RegistrationValidation.PASSWORD_PATTERN_REQ;
                }
                break;

            case 6:
                boolean reEnterPasswordEmpty = TextUtils.isEmpty(getConfirmPassword());
                if (emptyValidation && reEnterPasswordEmpty) {
                    return AppConstants.RegistrationValidation.RE_ENTER_PASSWORD_REQ;
                } else if (!reEnterPasswordEmpty) {
                    boolean passwordEmpty1 = TextUtils.isEmpty(getPassword());
                    if (passwordEmpty1 || (!getPassword()
                            .equals(getConfirmPassword()))) {
                        return AppConstants.RegistrationValidation
                                .RE_ENTER_PASSWORD_DOES_NOT_MATCH;
                    }

                }
                break;

            case 7:
                boolean userAddress = TextUtils.isEmpty(getUserAddress());
                if (emptyValidation && userAddress) {
                    return AppConstants.RegistrationValidation.ADDRESS_REQ;
                }
                break;

            default:
                return VALIDATION_SUCCESS;
        }
        return VALIDATION_SUCCESS;
    }

    private int validateDob() {
        Calendar dobDate = Calendar.getInstance();
        long dobInMillis = DateUtils.convertStringFormatToMillis(
                getDob(), AppConstants.DateFormatterConstants.YYYY_MM_DD_SLASH);
        dobDate.setTimeInMillis(dobInMillis);
        // futurde date check
        if (ValidationUtils.isFutureDate(dobDate)) {
            return DOB_FUTURE_DATE;
        }

        int returnedYear = ValidationUtils.calculateAge(dobDate);
        if (returnedYear < AppConstants.AgeConstants.USER_DOB) {
            return DOB_PERSON_LIMIT;
        }
        return VALIDATION_SUCCESS;
    }

    // store details
    @Bindable
    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
        notifyChange();
    }

    @Bindable
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Bindable
    public String getStoreCategoryNames() {
        return storeCategoryNames;
    }

    public void setStoreCategoryNames(String storeCategoryNames) {
        this.storeCategoryNames = storeCategoryNames;
    }

    public String getStoreCategoryIds() {
        return storeCategoryIds;
    }

    public void setStoreCategoryIds(String storeCategoryIds) {
        this.storeCategoryIds = storeCategoryIds;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    @Bindable
    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    @Bindable
    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        notifyChange();
    }

    @Bindable
    public String getStoreGSTN() {
        return storeGSTN;
    }

    public void setStoreGSTN(String storeGSTN) {
        this.storeGSTN = storeGSTN;
    }

    @Bindable
    public String getStorePhoneNumber() {
        return storePhoneNumber;
    }

    public void setStorePhoneNumber(String storePhoneNumber) {
        this.storePhoneNumber = storePhoneNumber;
    }


    public Pair<String, Integer> validateStoreInfo(String tag) {

        int fieldId = AppConstants.VALIDATION_FAILURE;
        if (tag == null) {
            for (int i = 0; i <= 5; i++) {
                fieldId = validateStoreFields(i, true);
                if (fieldId != VALIDATION_SUCCESS) {
                    tag = i + "";
                    break;
                }
            }
        } else {
            fieldId = validateStoreFields(Integer.parseInt(tag), false);
        }

        return new Pair<>(tag, fieldId);
    }

    private int validateStoreFields(int id, boolean emptyValidation) {
        switch (id) {
            case 0:
                if (emptyValidation && TextUtils.isEmpty(getStoreName())) {
                    return AppConstants.RegistrationValidation.NAME_REQ;
                }
                break;

            case 1:
                boolean phoneEmpty = TextUtils.isEmpty(getStorePhoneNumber());
                if (emptyValidation && phoneEmpty) {
                    return AppConstants.RegistrationValidation.PHONE_REQ;
                } else if (!phoneEmpty && !ValidationUtils.isPhoneNumberValid(
                        getStorePhoneNumber())) {
                    return AppConstants.RegistrationValidation.PHONE_MIN_DIGITS;
                }
                break;

            case 2:
                boolean storeCategory = TextUtils.isEmpty(getStoreCategoryNames());
                if (emptyValidation && storeCategory) {
                    return AppConstants.RegistrationValidation.CATEGORY_REQ;
                }
                break;

            case 3:
                boolean storeAddress = TextUtils.isEmpty(getStoreAddress());
                if (emptyValidation && storeAddress) {
                    return AppConstants.RegistrationValidation.ADDRESS_REQ;
                }
                break;

            case 4:
                boolean emailEmpty = TextUtils.isEmpty(getStoreEmail());
                if (emptyValidation && emailEmpty) {
                    return AppConstants.RegistrationValidation.EMAIL_REQ;
                } else if (!emailEmpty && !ValidationUtils.isValidEmail(getStoreEmail())) {
                    return AppConstants.RegistrationValidation.EMAIL_NOTVALID;
                }
                break;

            case 5:
                boolean storeGstn = TextUtils.isEmpty(getStoreGSTN());
                if (emptyValidation && storeGstn) {
                    return AppConstants.RegistrationValidation.GSTN_REQ;
                }
                break;

            default:
                return VALIDATION_SUCCESS;
        }
        return VALIDATION_SUCCESS;
    }

}
