package com.incon.connect.user.apimodel.components;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceRequest implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("warrantyId")
    @Expose
    private Integer warrantyId;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("complaint")
    @Expose
    private String complaint;
    @SerializedName("preferredDateFrom")
    @Expose
    private String preferredDateFrom;
    @SerializedName("createdDate")
    @Expose
    private Long createdDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(Integer warrantyId) {
        this.warrantyId = warrantyId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getPreferredDateFrom() {
        return preferredDateFrom;
    }

    public void setPreferredDateFrom(String preferredDateFrom) {
        this.preferredDateFrom = preferredDateFrom;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    protected ServiceRequest(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        warrantyId = in.readByte() == 0x00 ? null : in.readInt();
        status = in.readByte() == 0x00 ? null : in.readInt();
        comments = in.readString();
        complaint = in.readString();
        preferredDateFrom = in.readString();
        createdDate = in.readByte() == 0x00 ? null : in.readLong();
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
        if (warrantyId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(warrantyId);
        }
        if (status == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(status);
        }
        dest.writeString(comments);
        dest.writeString(complaint);
        dest.writeString(preferredDateFrom);
        if (createdDate == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(createdDate);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ServiceRequest> CREATOR = new Parcelable.Creator<ServiceRequest>() {
        @Override
        public ServiceRequest createFromParcel(Parcel in) {
            return new ServiceRequest(in);
        }

        @Override
        public ServiceRequest[] newArray(int size) {
            return new ServiceRequest[size];
        }
    };
}