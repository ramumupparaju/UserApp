
package com.incon.connect.user.apimodel.components.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.base.ApiBaseResponse;

import java.util.List;

public class RegistrationResponse extends ApiBaseResponse {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("errors")
    @Expose
    private List<String> errors = null;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }


}
