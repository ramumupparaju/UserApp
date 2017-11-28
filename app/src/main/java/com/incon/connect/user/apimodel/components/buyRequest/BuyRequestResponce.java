package com.incon.connect.user.apimodel.components.buyRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PC on 11/9/2017.
 */

public class BuyRequestResponce {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("merchantId")
    @Expose
    private Integer merchantId;
    @SerializedName("qrcodeid")
    @Expose
    private Integer qrcodeid;
    @SerializedName("customerId")
    @Expose
    private Integer customerId;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("createdDate")
    @Expose
    private Object createdDate;
    @SerializedName("createdBy")
    @Expose
    private Integer createdBy;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("merchantComments")
    @Expose
    private Object merchantComments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getQrcodeid() {
        return qrcodeid;
    }

    public void setQrcodeid(Integer qrcodeid) {
        this.qrcodeid = qrcodeid;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Object createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Object getMerchantComments() {
        return merchantComments;
    }

    public void setMerchantComments(Object merchantComments) {
        this.merchantComments = merchantComments;
    }
}
