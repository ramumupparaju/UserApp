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

public class ProductDetails extends BaseActivity {
    private ActivityProductDetailsBinding binding;
    private ProductListAdapter productListAdapter;
    private ProductInfoResponse response;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

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
        productListAdapter = new ProductListAdapter(this, listDataHeader, listDataChild);
        binding.expandaleList.setAdapter(productListAdapter);
        response = new ProductInfoResponse();
        binding.setProductinforesponse(response);

    }

    public void onOkClick() {
        finish();
    }

    private void initViews() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add(getString(
                R.string.bottom_option_main_features));
       /* listDataHeader.add(getString(
                R.string.bottom_option_warranty));*/
        listDataHeader.add(getString(
                R.string.bottom_option_description));
        listDataHeader.add(getString(
                R.string.bottom_option_feedback));
        listDataHeader.add(getString(
                R.string.bottom_option_price));
        // Adding child data
        List<String> mainFeatures = new ArrayList<String>();
      //  mainFeatures.add(response.getInformation());
        mainFeatures.add("mainFeatures");

        /*List<Integer> warrenty = new ArrayList<>();
        warrenty.add(response.getWarrantyDays());*/

        List<String> description = new ArrayList<String>();
       // description.add(response.getInformation());
        description.add("description");

        List<String> feedBack = new ArrayList<String>();
       // feedBack.add(response.getInformation());
        feedBack.add("feedBack");

        List<String> price = new ArrayList<String>();
       // price.add(response.getPrice());
        price.add("price");

        listDataChild.put(listDataHeader.get(0), mainFeatures);
        //   listDataChild.put(listDataHeader.get(1), warrenty);
        listDataChild.put(listDataHeader.get(1), description);
        listDataChild.put(listDataHeader.get(2), feedBack);
        listDataChild.put(listDataHeader.get(3), price);


    }
}
