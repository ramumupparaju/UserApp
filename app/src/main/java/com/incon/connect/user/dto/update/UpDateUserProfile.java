package com.incon.connect.user.dto.update;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.ValidationUtils;

/**
 * Created by PC on 10/13/2017.
 */

public class UpDateUserProfile extends BaseObservable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("name")
    @Expose
    private String name;
    private transient String genderType;

    public UpDateUserProfile() {
    }


    private transient String dateOfBirthToShow;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyChange();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Bindable
    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String gender) {
        this.genderType = gender;
        notifyChange();
    }

    @Bindable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        notifyChange();
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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
    public String getDateOfBirthToShow() {
        return dateOfBirthToShow;
    }

    public void setDateOfBirthToShow(String dateOfBirthToShow) {
        this.dateOfBirthToShow = dateOfBirthToShow;
        if (!TextUtils.isEmpty(dateOfBirthToShow)) {
            dob = DateUtils.convertDateToAnotherFormat(dateOfBirthToShow, AppConstants
                    .DateFormatterConstants.MM_DD_YYYY, AppConstants.DateFormatterConstants
                    .MM_DD_YYYY);
        }
        notifyChange();
    }

    public Pair<String, Integer> validateUpDateUserProfile(String tag) {

        int fieldId = AppConstants.VALIDATION_FAILURE;
        if (tag == null) {
            for (int i = 0; i <= 5; i++) {
                fieldId = validateFields(i, true);
                if (fieldId != AppConstants.VALIDATION_SUCCESS) {
                    tag = i + "";
                    break;
                }
            }
        } else {
            fieldId = validateFields(Integer.parseInt(tag), false);
        }

        return new Pair<>(tag, fieldId);
    }

    private int validateFields(int id, boolean emptyValidation) {
        switch (id) {
            case 0:
                boolean nameEmpty = TextUtils.isEmpty(name);
                if (emptyValidation && nameEmpty) {
                    return AppConstants.RegistrationValidation.NAME_REQ;
                }
                break;

            case 1:
                boolean phoneEmpty = TextUtils.isEmpty(mobileNumber);
                if (emptyValidation && phoneEmpty) {
                    return AppConstants.RegistrationValidation.PHONE_REQ;
                } else if (!phoneEmpty && !ValidationUtils.isPhoneNumberValid(getMobileNumber())) {
                    return AppConstants.RegistrationValidation.PHONE_MIN_DIGITS;
                }
                break;

            case 3:
                boolean dobEmpty = TextUtils.isEmpty(dob);
                if (!dobEmpty) {
                    return AppUtils.validateDob(dob);
                }
                break;
            case 4:
                boolean emailEmpty = TextUtils.isEmpty(getEmail());
                if (emptyValidation && emailEmpty) {
                    return AppConstants.RegistrationValidation.EMAIL_REQ;
                } else if (!emailEmpty && !ValidationUtils.isValidEmail(getEmail())) {
                    return AppConstants.RegistrationValidation.EMAIL_NOTVALID;
                }
                break;


            case 7:
                boolean addressEmpty = TextUtils.isEmpty(address);
                if (emptyValidation && addressEmpty) {
                    return AppConstants.RegistrationValidation.ADDRESS_REQ;
                }
                break;

            default:
                return AppConstants.VALIDATION_SUCCESS;
        }
        return AppConstants.VALIDATION_SUCCESS;
    }

}
