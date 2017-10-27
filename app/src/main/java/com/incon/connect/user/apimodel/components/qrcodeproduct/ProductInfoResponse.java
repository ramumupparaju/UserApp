package com.incon.connect.user.apimodel.components.qrcodeproduct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductInfoResponse {

    @SerializedName("codeId")
    @Expose
    private Integer codeId;
    @SerializedName("serialNumber")
    @Expose
    private Object serialNumber;
    @SerializedName("batchCode")
    @Expose
    private Object batchCode;
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productModel")
    @Expose
    private String productModel;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("division")
    @Expose
    private String division;
    @SerializedName("divisionId")
    @Expose
    private Integer divisionId;

    public Integer getCodeId() {
        return codeId;
    }

    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
    }

    public Object getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Object serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Object getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(Object batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
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

}