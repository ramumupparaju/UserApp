package com.incon.connect.user.dto.servicerequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MY HOME on 29-Dec-17.
 */

public class ServiceRequest {
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("complaint")
    @Expose
    private String complaint;
    @SerializedName("customerId")
    @Expose
    private Integer customerId;
    @SerializedName("preferredDateFrom")
    @Expose
    private String preferredDateFrom;
    @SerializedName("preferredDateTo")
    @Expose
    private String preferredDateTo;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("purchaseId")
    @Expose
    private Integer purchaseId;
    @SerializedName("serviceCenterId")
    @Expose
    private Integer serviceCenterId;
    private Integer preferredUser;

    public Integer getPreferredUser() {
        return preferredUser;
    }

    public void setPreferredUser(Integer preferredUser) {
        this.preferredUser = preferredUser;
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

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getPreferredDateFrom() {
        return preferredDateFrom;
    }

    public void setPreferredDateFrom(String preferredDateFrom) {
        this.preferredDateFrom = preferredDateFrom;
    }

    public String getPreferredDateTo() {
        return preferredDateTo;
    }

    public void setPreferredDateTo(String preferredDateTo) {
        this.preferredDateTo = preferredDateTo;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Integer purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Integer getServiceCenterId() {
        return serviceCenterId;
    }

    public void setServiceCenterId(Integer serviceCenterId) {
        this.serviceCenterId = serviceCenterId;
    }


}
