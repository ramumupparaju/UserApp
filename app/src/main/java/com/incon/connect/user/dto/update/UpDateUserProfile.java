package com.incon.connect.user.dto.update;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.utils.DateUtils;

/**
 * Created by PC on 10/13/2017.
 */

public class UpDateUserProfile  extends BaseObservable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("userEmail")
    @Expose
    private String userEmail;

    private String phoneNumber;

    private String password;
    private transient String confirmPassword;

    private transient String dateOfBirthToShow;

    public  UpDateUserProfile() {

    }
    @Bindable
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
    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyChange();
    }
    @Bindable
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        notifyChange();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

}
