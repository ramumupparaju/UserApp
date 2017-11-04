package com.incon.connect.user.dto.addfavorites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PC on 11/4/2017.
 */

public class Favorites {

    @SerializedName("addressId")
    @Expose
    private String addressId;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("warrantyId")
    @Expose
    private Integer warrantyId;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(Integer warrantyId) {
        this.warrantyId = warrantyId;
    }
}
