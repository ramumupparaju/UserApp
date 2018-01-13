package com.incon.connect.user.apimodel.components.addserviceengineer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddServiceEngineer {

    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("name")
    @Expose
    private String name;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}