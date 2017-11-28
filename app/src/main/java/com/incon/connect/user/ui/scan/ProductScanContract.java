package com.incon.connect.user.ui.scan;


import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.ui.BaseView;

/**
 * Created on 31 May 2017 11:18 AM.
 *
 */
public interface ProductScanContract {

    interface View extends BaseView {
        void productInfo(ProductInfoResponse productInfoResponse);
    }

    interface Presenter {
        void productInfoUsingQrCode(String qrCode);
    }

}
