package com.incon.connect.user.apimodel.components.history.purchased;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PC on 10/2/2017.
 */

public class InterestHistoryResponse {
    @SerializedName("serialNumber")
    @Expose
    private String serialNumber;
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("information")
    @Expose
    private String information;
    @SerializedName("brandId")
    @Expose
    private String brandId;
    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("productLogoUrl")
    @Expose
    private String productLogoUrl;
    @SerializedName("productImageUrl")
    @Expose
    private String productImageUrl;
    @SerializedName("productQrCode")
    @Expose
    private String productQrCode;
    @SerializedName("mrp")
    @Expose
    private Integer mrp;
    @SerializedName("interestId")
    @Expose
    private Integer interestId;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("location")
    @Expose
    private String location;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getProductLogoUrl() {
        return productLogoUrl;
    }

    public void setProductLogoUrl(String productLogoUrl) {
        this.productLogoUrl = productLogoUrl;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductQrCode() {
        return productQrCode;
    }

    public void setProductQrCode(String productQrCode) {
        this.productQrCode = productQrCode;
    }

    public Integer getMrp() {
        return mrp;
    }

    public void setMrp(Integer mrp) {
        this.mrp = mrp;
    }

    public Integer getInterestId() {
        return interestId;
    }

    public void setInterestId(Integer interestId) {
        this.interestId = interestId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}