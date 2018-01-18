package com.incon.connect.user.apimodel.components.productinforesponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;

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

}