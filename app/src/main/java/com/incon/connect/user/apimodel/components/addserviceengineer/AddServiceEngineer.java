package com.incon.connect.user.apimodel.components.addserviceengineer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddServiceEngineer implements Parcelable {

    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("name")
    @Expose
    private String name;

    public AddServiceEngineer() {
    }

    public AddServiceEngineer(String mobileNumber, String name) {
        this.mobileNumber = mobileNumber;
        this.name = name;
    }

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


    protected AddServiceEngineer(Parcel in) {
        location = in.readString();
        mobileNumber = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(mobileNumber);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AddServiceEngineer> CREATOR = new Parcelable.Creator<AddServiceEngineer>() {
        @Override
        public AddServiceEngineer createFromParcel(Parcel in) {
            return new AddServiceEngineer(in);
        }

        @Override
        public AddServiceEngineer[] newArray(int size) {
            return new AddServiceEngineer[size];
        }
    };
}