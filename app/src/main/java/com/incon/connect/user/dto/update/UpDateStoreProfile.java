package com.incon.connect.user.dto.update;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PC on 10/24/2017.
 */

public class UpDateStoreProfile  extends BaseObservable {


    @SerializedName("contactNumber")
    @Expose
    private String contactNumber;
    @SerializedName("gstn")
    @Expose
    private String gstn;
    @SerializedName("storeAddress")
    @Expose
    private String storeAddress;
    @SerializedName("storeEmail")
    @Expose
    private String storeEmail;
    @SerializedName("storeLocation")
    @Expose
    private String storeLocation;
    @SerializedName("storeName")
    @Expose
    private String storeName;

    private String storeCategoryNames;

    public UpDateStoreProfile() {

    }

    public String getStoreCategoryNames() {
        return storeCategoryNames;
    }

    public void setStoreCategoryNames(String storeCategoryNames) {
        this.storeCategoryNames = storeCategoryNames;
    }
      @Bindable
    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        notifyChange();
    }
    @Bindable
    public String getGstn() {
        return gstn;
    }

    public void setGstn(String gstn) {
        this.gstn = gstn;
        notifyChange();
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
    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
        notifyChange();
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

}
