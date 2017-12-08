package com.incon.connect.user.ui.settings.billformat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ActivitySettingsBillFormatBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.billformat.BillFormatContract;
import com.incon.connect.user.ui.billformat.BillFormatPresenter;


/**
 * Created by PC on 11/15/2017.
 */

public class SettingsBillFormatActivity extends BaseActivity implements BillFormatContract.View{
    private ActivitySettingsBillFormatBinding binding;
    private ProductInfoResponse productInfoResponse ;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings_bill_format;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
         productInfoResponse = new ProductInfoResponse();
        binding.setBillFormatActivity(this);
        binding.setProductinforesponse(productInfoResponse);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
