package com.incon.connect.user.apimodel.components.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.base.ApiBaseResponse;

public class BaseGenderContactNumberTypeData extends ApiBaseResponse {

    @SerializedName("data")
    @Expose
    private RegistrationDefaultsData data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RegistrationDefaultsData getData() {
        return data;
    }

    public void setData(RegistrationDefaultsData data) {
        this.data = data;
    }

}