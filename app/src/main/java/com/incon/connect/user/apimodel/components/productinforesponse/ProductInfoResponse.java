package com.incon.connect.user.apimodel.components.productinforesponse;

import android.databinding.BaseObservable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PC on 11/8/2017.
 */

public class ProductInfoResponse extends BaseObservable {
    @SerializedName("serialNumber")
    @Expose
    private String serialNumber;
    @SerializedName("codeId")
    @Expose
    private Integer codeId;
    @SerializedName("batchCode")
    @Expose
    private Object batchCode;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("batchNumber")
    @Expose
    private String batchNumber;
    @SerializedName("productModel")
    @Expose
    private String productModel;

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("division")
    @Expose
    private String division;
    @SerializedName("divisionId")
    @Expose
    private Integer divisionId;
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
    private String price;
    @SerializedName("warrantyId")
    @Expose
    private String warrantyId;
    @SerializedName("invoiceNumber")
    @Expose
    private String invoiceNumber;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("warrantyEndDate")
    @Expose
    private Long warrantyEndDate;
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
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("interestId")
    @Expose
    private Integer interestId;
    @SerializedName("qrcodeId")
    @Expose
    private Integer qrcodeId;
    @SerializedName("merchantId")
    @Expose
    private Integer merchantId;
    @SerializedName("requestedDate")
    @Expose
    private long requestedDate;

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("addressId")
    @Expose
    private String addressId;

    @SerializedName("specialInstruction")
    @Expose
    private String specialInstruction;
    @SerializedName("returnPolicy")
    @Expose
    private String returnPolicy;
    @SerializedName("purchasedDate")
    @Expose
    private long purchasedDate;
    @SerializedName("storeLocation")
    @Expose
    private String storeLocation;
    @SerializedName("storeContactNumber")
    @Expose
    private String storeContactNumber;
    private transient boolean isSelected;

    public Integer getQrcodeId() {
        return qrcodeId;
    }

    public void setQrcodeId(Integer qrcodeId) {
        this.qrcodeId = qrcodeId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public long getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(long requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
    public Integer getCodeId() {
        return codeId;
    }

    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
    }

    public Object getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(Object batchCode) {
        this.batchCode = batchCode;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getCategory() {
        return category;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public String getReturnPolicy() {
        return returnPolicy;
    }

    public void setReturnPolicy(String returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

    public long getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(long purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getStoreContactNumber() {
        return storeContactNumber;
    }

    public void setStoreContactNumber(String storeContactNumber) {
        this.storeContactNumber = storeContactNumber;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Integer getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Integer divisionId) {
        this.divisionId = divisionId;
    }


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public Integer getInterestId() {
        return interestId;
    }

    public void setInterestId(Integer interestId) {
        this.interestId = interestId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(String warrantyId) {
        this.warrantyId = warrantyId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(Long warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
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

}
