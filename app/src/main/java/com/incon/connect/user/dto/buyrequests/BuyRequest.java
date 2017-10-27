package com.incon.connect.user.dto.buyrequests;

import android.databinding.BaseObservable;

/**
 * Created by PC on 10/10/2017.
 */

public class BuyRequest extends BaseObservable {
    private String customerId;
    private String merchantId;
    private String qrcodeid;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getQrcodeid() {
        return qrcodeid;
    }

    public void setQrcodeid(String qrcodeid) {
        this.qrcodeid = qrcodeid;
    }


}
