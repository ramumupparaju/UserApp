package com.incon.connect.user.ui.billformat;

import com.incon.connect.user.ui.BaseView;

import okhttp3.MultipartBody;

/**
 * Created by PC on 11/16/2017.
 */

public interface BillFormatContract {
    interface View extends BaseView {

        void onBillUpload();
    }
    interface Presenter {

        void uploadBill(int purchaseId, MultipartBody.Part serviceCenterLogo);
    }
}
