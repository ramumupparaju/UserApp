package com.incon.connect.user.ui.billformat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ActivityBillFormatBinding;
import com.incon.connect.user.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PC on 11/15/2017.
 */

public class BillFormatActivity extends BaseActivity implements BillFormatContract.View{
    private ActivityBillFormatBinding binding;
    private BillFormatPresenter billFormatPresenter ;
    private ProductInfoResponse productInfoResponse ;


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
         productInfoResponse = new ProductInfoResponse();
        binding.setProductinforesponse(productInfoResponse);
        binding.setBillFormatActivity(this);
      //  productInfoResponse.setPrice("Price :" + productInfoResponse.getPrice());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
