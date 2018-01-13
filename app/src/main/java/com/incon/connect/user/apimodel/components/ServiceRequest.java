package com.incon.connect.user.apimodel.components;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceRequest {

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

}