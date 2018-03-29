package com.incon.connect.user.apimodel.components;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PreferredUser implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    protected PreferredUser(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
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
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PreferredUser> CREATOR = new Parcelable.Creator<PreferredUser>() {
        @Override
        public PreferredUser createFromParcel(Parcel in) {
            return new PreferredUser(in);
        }

        @Override
        public PreferredUser[] newArray(int size) {
            return new PreferredUser[size];
        }
    };
}