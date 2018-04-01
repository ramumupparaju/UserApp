package com.incon.connect.user.apimodel.components.showroom;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PC on 2/12/2018.
 */

public class ShowRoomResponse {

    @SerializedName("storeId")
    @Expose
    private Integer storeId;

    @SerializedName("name")
    @Expose
    private String showRoomname;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("offers")
    @Expose
    private List<Object> offers = null;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getShowRoomname() {
        return showRoomname;
    }

    public void setShowRoomname(String showRoomname) {
        this.showRoomname = showRoomname;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Object> getOffers() {
        return offers;
    }

    public void setOffers(List<Object> offers) {
        this.offers = offers;
    }
}
