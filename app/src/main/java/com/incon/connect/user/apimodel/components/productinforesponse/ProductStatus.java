package com.incon.connect.user.apimodel.components.productinforesponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;
import com.incon.connect.user.apimodel.components.status.StatusList;

import java.util.List;

public class ProductStatus {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("warrantyId")
@Expose
private Integer warrantyId;
@SerializedName("status")
@Expose
private DefaultStatusData status;
@SerializedName("isActive")
@Expose
private Integer isActive;

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

public DefaultStatusData getStatus() {
return status;
}

public void setStatus(DefaultStatusData status) {
this.status = status;
}

public Integer getIsActive() {
return isActive;
}

public void setIsActive(Integer isActive) {
this.isActive = isActive;
}

// todo have delete

  /*  @SerializedName("statusList")
    @Expose
    private List<StatusList> statusList = null;
    @SerializedName("serialNumber")
    @Expose
    private String serialNumber;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("modelNumber")
    @Expose
    private String modelNumber;
    @SerializedName("batchNumber")
    @Expose
    private String batchNumber;
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
    @SerializedName("categoryId")
    @Expose
    private String categoryId;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("divisionId")
    @Expose
    private String divisionId;
    @SerializedName("divisionName")
    @Expose
    private String divisionName;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("warrantyId")
    @Expose
    private Integer warrantyId;
    @SerializedName("invoiceNumber")
    @Expose
    private String invoiceNumber;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("warrantyEndDate")
    @Expose
    private Integer warrantyEndDate;
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
    @SerializedName("mainFeatures")
    @Expose
    private String mainFeatures;
    @SerializedName("qrcodeId")
    @Expose
    private Integer qrcodeId;
    @SerializedName("merchantId")
    @Expose
    private Integer merchantId;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("specialInstruction")
    @Expose
    private String specialInstruction;
    @SerializedName("returnPolicy")
    @Expose
    private String returnPolicy;
    @SerializedName("purchasedDate")
    @Expose
    private Integer purchasedDate;
    @SerializedName("warrantyConditions")
    @Expose
    private String warrantyConditions;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("productSpecification")
    @Expose
    private String productSpecification;
    @SerializedName("productDimensions")
    @Expose
    private String productDimensions;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("customerContact")
    @Expose
    private String customerContact;
    @SerializedName("storeLocation")
    @Expose
    private String storeLocation;
    @SerializedName("storeContactNumber")
    @Expose
    private String storeContactNumber;
    @SerializedName("storeName")
    @Expose
    private String storeName;
    @SerializedName("storeAddress")
    @Expose
    private String storeAddress;
    @SerializedName("installationRequired")
    @Expose
    private Integer installationRequired;

    public List<StatusList> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusList> statusList) {
        this.statusList = statusList;
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

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(Integer warrantyId) {
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

    public Integer getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(Integer warrantyEndDate) {
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

    public String getMainFeatures() {
        return mainFeatures;
    }

    public void setMainFeatures(String mainFeatures) {
        this.mainFeatures = mainFeatures;
    }

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Integer getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(Integer purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public String getWarrantyConditions() {
        return warrantyConditions;
    }

    public void setWarrantyConditions(String warrantyConditions) {
        this.warrantyConditions = warrantyConditions;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProductSpecification() {
        return productSpecification;
    }

    public void setProductSpecification(String productSpecification) {
        this.productSpecification = productSpecification;
    }

    public String getProductDimensions() {
        return productDimensions;
    }

    public void setProductDimensions(String productDimensions) {
        this.productDimensions = productDimensions;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public Integer getInstallationRequired() {
        return installationRequired;
    }

    public void setInstallationRequired(Integer installationRequired) {
        this.installationRequired = installationRequired;
    }
*/
}