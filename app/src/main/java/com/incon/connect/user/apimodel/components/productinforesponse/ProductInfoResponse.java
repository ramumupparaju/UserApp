package com.incon.connect.user.apimodel.components.productinforesponse;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.components.qrcodebaruser.Store;
import com.incon.connect.user.apimodel.components.qrcodebaruser.UserInfoResponse;

/**
 * Created by PC on 11/8/2017.
 */

public class ProductInfoResponse extends BaseObservable implements Parcelable {


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
    //    for interest
    @SerializedName("warrantyYears")
    @Expose
    private Integer warrantyYears;
    @SerializedName("warrantyMonths")
    @Expose
    private Integer warrantyMonths;
    @SerializedName("warrantyDays")
    @Expose
    private Integer warrantyDays;
    @SerializedName("warrantyConditions")
    @Expose
    private String warrantyConditions;
    private transient boolean isSelected;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("mobileNumber")
    @Expose
    private String msisdn;
    @SerializedName("usertype")
    @Expose
    private Integer usertype;
    @SerializedName("store")
    @Expose
    private Store store;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("gender")
    @Expose
    private String gender;

    public ProductInfoResponse() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Integer getUsertype() {
        return usertype;
    }

    public void setUsertype(Integer usertype) {
        this.usertype = usertype;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public ProductInfoResponse(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        email = in.readString();
        location = in.readString();
        msisdn = in.readString();
        usertype = in.readByte() == 0x00 ? null : in.readInt();
        store = (Store) in.readValue(Store.class.getClassLoader());
        uuid = in.readString();
        country = in.readString();
        dob = in.readString();
        gender = in.readString();
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
        dest.writeString(email);
        dest.writeString(location);
        dest.writeString(msisdn);
        if (usertype == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(usertype);
        }
        dest.writeValue(store);
        dest.writeString(uuid);
        dest.writeString(country);
        dest.writeString(dob);
        dest.writeString(gender);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductInfoResponse> CREATOR =
            new Parcelable.Creator<ProductInfoResponse>() {
                @Override
                public ProductInfoResponse createFromParcel(Parcel in) {
                    return new ProductInfoResponse(in);
                }

                @Override
                public ProductInfoResponse[] newArray(int size) {
                    return new ProductInfoResponse[size];
                }
            };

    public Integer getQrcodeId() {
        return qrcodeId;
    }

    public Integer getWarrantyYears() {
        return warrantyYears;
    }

    public void setWarrantyYears(Integer warrantyYears) {
        this.warrantyYears = warrantyYears;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Integer getWarrantyDays() {
        return warrantyDays;
    }

    public void setWarrantyDays(Integer warrantyDays) {
        this.warrantyDays = warrantyDays;
    }

    public String getWarrantyConditions() {
        return warrantyConditions;
    }

    public void setWarrantyConditions(String warrantyConditions) {
        this.warrantyConditions = warrantyConditions;
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
