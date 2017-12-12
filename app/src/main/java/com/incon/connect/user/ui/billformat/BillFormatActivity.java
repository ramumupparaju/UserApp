package com.incon.connect.user.ui.billformat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ActivityBillFormatBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.utils.DateUtils;


/**
 * Created by PC on 11/15/2017.
 */

public class BillFormatActivity extends BaseActivity implements BillFormatContract.View {
    private ActivityBillFormatBinding binding;
    private ProductInfoResponse productInfoResponse;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_bill_format;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setBillFormatActivity(this);
        Bundle bundle = getIntent().getExtras();
        productInfoResponse = bundle.getParcelable(BundleConstants.PRODUCT_INFO_RESPONSE);
        binding.setProductinforesponse(productInfoResponse);
      //  binding.textDopValues.setText(": " + DateUtils.convertMillisToStringFormat
                //(productInfoResponse.getPurchasedDate(), DateFormatterConstants.DD_MM_YYYY));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
