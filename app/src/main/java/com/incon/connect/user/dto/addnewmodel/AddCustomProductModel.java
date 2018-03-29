package com.incon.connect.user.dto.addnewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.utils.DateUtils;

public class AddCustomProductModel extends BaseObservable {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("addressId")
    @Expose
    private Integer addressId;
    @SerializedName("batchNo")
    @Expose
    private String batchNo;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("divisionId")
    @Expose
    private Integer divisionId;
    @SerializedName("brandId")
    @Expose
    private Integer brandId;
    @SerializedName("customerId")
    @Expose
    private Integer customerId;
    @SerializedName("serialNo")
    @Expose
    private String serialNo;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("productModel")
    @Expose
    private String productModel;
    @SerializedName("information")
    @Expose
    private String information;
    @SerializedName("purchaseDate")
    @Expose
    private Long purchaseDate;
    @SerializedName("warrantyDays")
    @Expose
    private String warrantyDays;
    @SerializedName("warrantyMonths")
    @Expose
    private String warrantyMonths;
    @SerializedName("warrantyYears")
    @Expose
    private String warrantyYears;
    private Integer productId;
    private transient String categoryName;
    private transient String divisionName;
    private transient String brandName;
    private transient String dateOfPurchased;
    private transient String warrantyShow;
    private transient String extendedWarrantyShow;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Bindable
    public String getExtendedWarrantyShow() {
        return extendedWarrantyShow;
    }

    public void setExtendedWarrantyShow(String extendedWarrantyShow) {
        this.extendedWarrantyShow = extendedWarrantyShow;
        notifyChange();
    }


    @Bindable
    public String getWarrantyShow() {
        return warrantyShow;
    }

    public void setWarrantyShow(String warrantyShow) {
        this.warrantyShow = warrantyShow;
        notifyChange();
    }

    public String getWarrantyDays() {
        return warrantyDays;
    }

    public void setWarrantyDays(String warrantyDays) {
        this.warrantyDays = warrantyDays;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyChange();
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    @Bindable
    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
        notifyChange();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Integer divisionId) {
        this.divisionId = divisionId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Bindable
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        notifyChange();
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyChange();
    }

    @Bindable
    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
        notifyChange();
    }

    @Bindable
    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
        notifyChange();
    }

    public Long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(String warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    @Bindable
    public String getWarrantyYears() {
        return warrantyYears;
    }

    public void setWarrantyYears(String warrantyYears) {
        this.warrantyYears = warrantyYears;
        notifyChange();
    }

    @Bindable
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        notifyChange();
    }

    @Bindable
    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
        notifyChange();
    }

    @Bindable
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
        notifyChange();
    }

    @Bindable
    public String getDateOfPurchased() {
        return dateOfPurchased;
    }

    public void setDateOfPurchased(String dateOfPurchased) {
        this.dateOfPurchased = dateOfPurchased;
        purchaseDate = DateUtils.convertStringFormatToMillis(dateOfPurchased, AppConstants
                .DateFormatterConstants.MM_DD_YYYY);
        notifyChange();
    }


    public Pair<String, Integer> validateAddNewModel(String tag) {

        int fieldId = AppConstants.VALIDATION_FAILURE;
        if (tag == null) {
            for (int i = 0; i <= 11; i++) {
                fieldId = validateFields(i, true);
                if (fieldId != AppConstants.VALIDATION_SUCCESS) {
                    tag = i + "";
                    break;
                }
            }
        } else {
            fieldId = validateFields(Integer.parseInt(tag), false);
        }

        return new Pair<>(tag, fieldId);
    }

    private int validateFields(int id, boolean emptyValidation) {
        switch (id) {
           /* case 0:
                boolean modelEmpty = TextUtils.isEmpty(productModel);
                if (emptyValidation && modelEmpty) {
                    return AppConstants.AddNewModelValidation.MODEL;
                }
                break;*/

            case 1:
                boolean nameEmpty = TextUtils.isEmpty(name);
                if (emptyValidation && nameEmpty) {
                    return AppConstants.AddNewModelValidation.NAME;
                }
                break;

            case 2:
                boolean categoryEmpty = TextUtils.isEmpty(categoryName);
                if (emptyValidation && categoryEmpty) {
                    return AppConstants.AddNewModelValidation.CATEGORY;
                }
                break;

            case 3:
                boolean divisionEmpty = TextUtils.isEmpty(divisionName);
                if (emptyValidation && divisionEmpty) {
                    return AppConstants.AddNewModelValidation.DIVISION;
                }
                break;

            case 4:
                boolean brandEmpty = TextUtils.isEmpty(brandName);
                if (emptyValidation && brandEmpty) {
                    return AppConstants.AddNewModelValidation.BRAND;
                }
                break;

            /*case 5:
                boolean mrpPriceEmpty = TextUtils.isEmpty(mrpPrice);
                if (emptyValidation && mrpPriceEmpty) {
                    return AppConstants.AddNewModelValidation.MRP_PRICE;
                }
                break;


            case 6:
                boolean priceEmpty = TextUtils.isEmpty(price);
                if (emptyValidation && priceEmpty) {
                    return AppConstants.AddNewModelValidation.PRICE;
                }
                break;
            case 7:
                boolean notesEmpty = TextUtils.isEmpty(notes);
                if (emptyValidation && notesEmpty) {
                    return AppConstants.AddNewModelValidation.NOTE;
                }
                break;
*/

            default:
                return AppConstants.VALIDATION_SUCCESS;
        }
        return AppConstants.VALIDATION_SUCCESS;
    }
}