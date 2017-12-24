package com.incon.connect.user.ui.history.base;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incon.connect.user.R;
import com.incon.connect.user.databinding.BottomSheetPurchasedBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.utils.DeviceUtils;


public abstract class BaseProductOptionsFragment extends BaseFragment {


    public BottomSheetDialog bottomSheetDialog;
    public BottomSheetPurchasedBinding bottomSheetPurchasedBinding;

    // load bottom sheet
    public void loadBottomSheet() {
        bottomSheetPurchasedBinding = DataBindingUtil.inflate(LayoutInflater.from(
                getActivity()), R.layout.bottom_sheet_purchased, null, false);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(bottomSheetPurchasedBinding.getRoot());
    }

    public void setBottomViewOptions(LinearLayout parentLayout, String[] namesArray, int[] imagesArray, View.OnClickListener onClickListener) {
        for (int i = 0; i < namesArray.length; i++) {
            LinearLayout customBottomView = getCustomBottomView();

            getBottomTextView(customBottomView).setText(namesArray[i]);
            getBottomImageView(customBottomView).setImageResource(imagesArray[i]);

            customBottomView.setTag(i);
            customBottomView.setOnClickListener(onClickListener);
            parentLayout.addView(customBottomView);
        }
    }

    public LinearLayout getCustomBottomView() {

        int dp5 = (int) DeviceUtils.convertDpToPx(5);
        Context context = getContext();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(dp5, dp5, dp5, dp5);
        linearLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        llp.weight = 1;
        linearLayout.setLayoutParams(llp);

        int dp24 = (int) DeviceUtils.convertDpToPx(24);
        ImageView imageView = new ImageView(context);
        imageView.setId(R.id.view_logo);
        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(dp24, dp24);
        imageView.setLayoutParams(imageViewLayoutParams);
        imageView.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
        linearLayout.addView(imageView);

        TextView textView = new TextView(context);
        textView.setId(R.id.view_tv);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        linearLayout.addView(textView);
        try {
            Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
            textView.setTypeface(type);
        } catch (Exception e) {

        }

        return linearLayout;
    }

    public ImageView getBottomImageView(LinearLayout linearLayout) {
        return ((ImageView) linearLayout.findViewById(R.id.view_logo));
    }

    public TextView getBottomTextView(LinearLayout linearLayout) {
        return ((TextView) linearLayout.findViewById(R.id.view_tv));
    }

}