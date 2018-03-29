package com.incon.connect.user.apimodel.components;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceCenter extends BaseObservable implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("contactNo")
    @Expose
    private String contactNo;

    private transient String formattedAddress;

    @Bindable
    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
        notifyChange();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyChange();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }


    protected ServiceCenter(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        location = in.readString();
        contactNo = in.readString();
        formattedAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(contactNo);
        dest.writeString(formattedAddress);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ServiceCenter> CREATOR = new Parcelable.Creator<ServiceCenter>() {
        @Override
        public ServiceCenter createFromParcel(Parcel in) {
            return new ServiceCenter(in);
        }

        @Override
        public ServiceCenter[] newArray(int size) {
            return new ServiceCenter[size];
        }
    };
}