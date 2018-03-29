package com.incon.connect.user.apimodel.components;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssignedUser implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("designation")
    @Expose
    private String designation;


    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


    protected AssignedUser(Parcel in) {
        name = in.readString();
        mobileNumber = in.readString();
        designation = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mobileNumber);
        dest.writeString(designation);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AssignedUser> CREATOR = new Parcelable.Creator<AssignedUser>() {
        @Override
        public AssignedUser createFromParcel(Parcel in) {
            return new AssignedUser(in);
        }

        @Override
        public AssignedUser[] newArray(int size) {
            return new AssignedUser[size];
        }
    };
}