package com.incon.connect.user.dto.warrantyregistration;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.AppConstants;

public class WarrantyRegistration extends BaseObservable implements Parcelable {

    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("modelNumber")
    @Expose
    private String modelNumber;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("divisionId")
    @Expose
    private Integer divisionId;

    @SerializedName("brandId")
    @Expose
    private String brandId;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("mrpPrice")
    @Expose
    private String mrpPrice;
    @SerializedName("batchNumber")
    @Expose
    private String batchNumber;
    @SerializedName("serialNumber")
    @Expose
    private String serialNumber;
    @SerializedName("merchantId")
    @Expose
    private Integer merchantId;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("customerId")
    @Expose
    private String customerId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("invoiceNumber")
    @Expose
    private String invoiceNumber;
    @SerializedName("codeId")
    @Expose
    private int codeId;
    @SerializedName("information")
    @Expose
    private String description;
    private transient String categoryName;
    private transient String divisionName;
    private transient boolean isFromProductScan = false;

    public String getMrpPrice() {
        return mrpPrice;
    }

    public void setMrpPrice(String mrpPrice) {
        this.mrpPrice = mrpPrice;
    }


    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyChange();
    }

    @Bindable
    public boolean isFromProductScan() {
        return isFromProductScan;
    }

    public void setFromProductScan(boolean fromProductScan) {
        isFromProductScan = fromProductScan;
    }

    public int getCodeId() {
        return codeId;
    }

    public void setCodeId(int codeId) {
        this.codeId = codeId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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
    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
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

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyChange();
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        notifyChange();
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        notifyChange();
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public WarrantyRegistration() {
    }

    protected WarrantyRegistration(Parcel in) {
        mobileNumber = in.readString();
        modelNumber = in.readString();
        categoryId = in.readByte() == 0x00 ? null : in.readInt();
        divisionId = in.readByte() == 0x00 ? null : in.readInt();
        brandId = in.readString();
        price = in.readString();
        batchNumber = in.readString();
        serialNumber = in.readString();
        merchantId = in.readByte() == 0x00 ? null : in.readInt();
        productId = in.readByte() == 0x00 ? null : in.readString();
        customerId = in.readByte() == 0x00 ? null : in.readString();
        status = in.readString();
        invoiceNumber = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobileNumber);
        dest.writeString(modelNumber);
        if (categoryId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(categoryId);
        }
        if (divisionId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(divisionId);
        }
        dest.writeString(brandId);
        dest.writeString(price);
        dest.writeString(batchNumber);
        dest.writeString(serialNumber);
        if (merchantId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(merchantId);
        }
        if (productId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(productId);
        }
        if (customerId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(customerId);
        }
        dest.writeString(status);
        dest.writeString(invoiceNumber);
    }

    @SuppressWarnings("unused")
    public static final Creator<WarrantyRegistration> CREATOR = new
            Creator<WarrantyRegistration>() {
                @Override
                public WarrantyRegistration createFromParcel(Parcel in) {
                    return new WarrantyRegistration(in);
                }

                @Override
                public WarrantyRegistration[] newArray(int size) {
                    return new WarrantyRegistration[size];
                }
            };

    public Pair<String, Integer> validateWarrantyRegistration(String tag) {

        int fieldId = AppConstants.VALIDATION_FAILURE;
        if (tag == null) {
            for (int i = 0; i <= 6; i++) {
                fieldId = validateFields(i, true);
                if (fieldId != AppConstants.VALIDATION_SUCCESS) {
                    tag = i + "";
                    break;
                }
            }
        }
        else {
            fieldId =  validateFields(Integer.parseInt(tag), false);
        }

        return  new Pair<>(tag, fieldId);
    }

    private int validateFields(int id, boolean emptyValidation) {
        switch (id) {
            case 0:
                boolean modelEmpty = TextUtils.isEmpty(modelNumber);
                if (emptyValidation && modelEmpty) {
                    return AppConstants.WarrantyregistationValidation.MODEL;
                } else if (!modelEmpty && TextUtils.isEmpty(productId)) {
                    return AppConstants.ProductAssignValidation.INVALID_MODEL;
                }
                break;
            case 1:
                boolean descEmpty = TextUtils.isEmpty(description);
                if (emptyValidation && descEmpty) {
                    return AppConstants.WarrantyregistationValidation.DESCRIPTION;
                }
                break;
            case 2:
                boolean serialEmpty = TextUtils.isEmpty(serialNumber);
                if (emptyValidation && serialEmpty) {
                    return AppConstants.WarrantyregistationValidation.SERIAL_NO;
                }
                break;
            case 3:
                boolean batchEmpty = TextUtils.isEmpty(batchNumber);
                if (emptyValidation && batchEmpty) {
                    return AppConstants.WarrantyregistationValidation.BATCH_NO;
                }
                break;

            case 4:
                boolean mprPriceEmpty = TextUtils.isEmpty(mrpPrice);
                if (emptyValidation && mprPriceEmpty) {
                    return AppConstants.WarrantyregistationValidation.MRP_PRICE;
                }
                break;

            case 5:
                boolean priceEmpty = TextUtils.isEmpty(price);
                if (emptyValidation && priceEmpty) {
                    return AppConstants.WarrantyregistationValidation.PRICE;
                }
                break;


            case 6:
                boolean invoiceNumberEmpty = TextUtils.isEmpty(invoiceNumber);
                if (emptyValidation && invoiceNumberEmpty) {
                    return AppConstants.WarrantyregistationValidation.INVOICENUMBER;
                }
                break;


            default:
                return AppConstants.VALIDATION_SUCCESS;
        }
        return AppConstants.VALIDATION_SUCCESS;
    }
}