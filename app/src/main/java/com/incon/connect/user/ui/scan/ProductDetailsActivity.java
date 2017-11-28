package com.incon.connect.user.ui.scan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ActivityProductDetailsBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.scan.adapter.ProductListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 11/22/2017.
 */

public class ProductDetailsActivity extends BaseActivity {
    private ActivityProductDetailsBinding binding;
    private ProductListAdapter productListAdapter;
    private ProductInfoResponse response;
    List<String> listTitle;
    HashMap<String, List<String>> listDec;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_details;
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        initViews();
        productListAdapter = new ProductListAdapter(this, listTitle, listDec);
        binding.expandaleList.setAdapter(productListAdapter);
//        response = new ProductInfoResponse();

        response = getIntent().getExtras().getParcelable(BundleConstants.PRODUCT_INFO_RESPONSE);


    }

    public void onOkClick() {
        finish();
    }

    private void initViews() {

        listTitle = new ArrayList<String>();
        listDec = new HashMap<String, List<String>>();

        // Adding child data
        listTitle.add(getString(
                R.string.bottom_option_main_features));
       /* listDataHeader.add(getString(
                R.string.bottom_option_warranty));*/
        listTitle.add(getString(
                R.string.bottom_option_description));
        listTitle.add(getString(
                R.string.bottom_option_feedback));
        listTitle.add(getString(
                R.string.bottom_option_price));
        // Adding child data
        List<String> mainFeatures = new ArrayList<String>();
        mainFeatures.add(response.getInformation());
        //mainFeatures.add("mainFeatures");

        /*List<Integer> warrenty = new ArrayList<>();
        warrenty.add(response.getWarrantyDays());*/

        List<String> description = new ArrayList<String>();
        description.add(response.getInformation());
        //description.add("description");

        List<String> feedBack = new ArrayList<String>();
        feedBack.add(response.getInformation());
       // feedBack.add("feedBack");

        List<String> price = new ArrayList<String>();
        price.add(response.getPrice());
        //price.add("price");

        listDec.put(listTitle.get(0), mainFeatures);
        //   listDataChild.put(listDataHeader.get(1), warrenty);
        listDec.put(listTitle.get(1), description);
        listDec.put(listTitle.get(2), feedBack);
        listDec.put(listTitle.get(3), price);


    }
}
