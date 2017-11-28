package com.incon.connect.user.ui.scan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import com.incon.connect.user.AppConstants;
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
    private List<String> listTitle;
    private HashMap<String, List<String>> listDec;

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
        binding.setProductDetails(this);
        initViews();
    }

    public void onOkClick() {
        finish();
    }

    private void initViews() {
        ProductInfoResponse response = getIntent().getExtras().getParcelable(BundleConstants.PRODUCT_INFO_RESPONSE);

        listTitle = new ArrayList<>();
        listDec = new HashMap<>();

        // Adding Parents data
        /*listTitle.add(getString(
                R.string.bottom_option_feedback));*/ //TODO have to add in 2nd version

        // Adding child data
        String mainFeaturesString = response.getInformation();
        if (!TextUtils.isEmpty(mainFeaturesString)) {
            String titleMainFeaturesString = getString(R.string.bottom_option_main_features);
            listTitle.add(titleMainFeaturesString);
            List<String> mainFeatures = new ArrayList<String>();
            mainFeatures.add(mainFeaturesString);
            listDec.put(titleMainFeaturesString, mainFeatures);
        }

        String warrantyString = response.getInformation();
        if (!TextUtils.isEmpty(warrantyString)) {
            String titleWarrantyString = getString(R.string.bottom_option_warranty);
            listTitle.add(titleWarrantyString);
            List<String> warranty = new ArrayList<>();
            warranty.add(warrantyString);
            listDec.put(titleWarrantyString, warranty);
        }

        String descString = response.getInformation();
        if (!TextUtils.isEmpty(descString)) {
            String titleDescString = getString(R.string.bottom_option_description);
            listTitle.add(titleDescString);
            List<String> description = new ArrayList<String>();
            description.add(descString);
            listDec.put(titleDescString, description);
        }

/*
        List<String> feedBack = new ArrayList<String>();
        feedBack.add(response.getInformation());
*/

        String priceString = response.getPrice();
        if (!TextUtils.isEmpty(priceString)) {
            String titlePricestring = getString(R.string.bottom_option_price);
            listTitle.add(titlePricestring);
            List<String> price = new ArrayList<String>();
            price.add(priceString);
            listDec.put(titlePricestring, price);
        }

//        listDec.put(listTitle.get(2), feedBack); //TODO have to add in 2nd ver

        ProductListAdapter productListAdapter = new ProductListAdapter(this, listTitle, listDec);
        binding.expandaleList.setAdapter(productListAdapter);

    }
}
