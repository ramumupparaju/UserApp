package com.incon.connect.user.ui.billformat;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

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
        // Make us non-modal, so that others can receive touch events.
        Window window = getWindow();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        Drawable drawable = getResources().getDrawable( R.drawable.bg_round_corners );
        InsetDrawable inset = new InsetDrawable(drawable, 0, 100, 0, 100);
        getWindow().setBackgroundDrawable(inset);


        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setBillFormatActivity(this);
        Bundle bundle = getIntent().getExtras();
        productInfoResponse = bundle.getParcelable(BundleConstants.PRODUCT_INFO_RESPONSE);
        binding.setProductinforesponse(productInfoResponse);


        binding.textDopValues.setText(DateUtils.convertMillisToStringFormat(productInfoResponse.getPurchasedDate(), DateFormatterConstants.DD_MM_YYYY));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

}
