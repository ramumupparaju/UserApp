package com.incon.connect.user.ui.history.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.databinding.BottomSheetPurchasedBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.utils.DeviceUtils;
import com.incon.connect.user.utils.Logger;

import java.util.List;


public abstract class BaseProductOptionsFragment extends BaseFragment {


    @Override
    public void showErrorMessage(String errorMessage) {
        if (bottomSheetDialog.isShowing()) {
            AppUtils.shortToast(getActivity(), errorMessage);
        } else {
            super.showErrorMessage(errorMessage);
        }
    }

    public BottomSheetDialog bottomSheetDialog;
    public BottomSheetPurchasedBinding bottomSheetPurchasedBinding;
    public int productSelectedPosition = -1;

    // load bottom sheet
    public void loadBottomSheet() {
        bottomSheetPurchasedBinding = DataBindingUtil.inflate(LayoutInflater.from(
                getActivity()), R.layout.bottom_sheet_purchased, null, false);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(bottomSheetPurchasedBinding.getRoot());
    }

    /**
     * chnaged selected views with primary and remaining as gray
     */
    public void changeSelectedViews(LinearLayout parentLayout, int selectedTag) {

        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childAt = parentLayout.getChildAt(i);

            LinearLayout linearLayout;
            if (childAt instanceof LinearLayout) {
                linearLayout = (LinearLayout) childAt;
            } else {
                HorizontalScrollView horizontalScrollView = (HorizontalScrollView) childAt;
                LinearLayout childAt1 = (LinearLayout) horizontalScrollView.getChildAt(i);
                changeSelectedViews(childAt1, selectedTag);
                return;
            }
            int tag = (Integer) linearLayout.getTag();
            boolean isSelectedView = tag == selectedTag;
            (getBottomImageView(linearLayout)).setColorFilter(getResources().getColor(isSelectedView ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            (getBottomTextView(linearLayout)).setTextColor(ContextCompat.getColor(getActivity(), isSelectedView ? R.color.colorPrimary : R.color.colorAccent));
        }
    }

    public void callPhoneNumber(String phoneNumber) {
        AppUtils.callPhoneNumber(getActivity(), phoneNumber);
    }

    public void showPhoneNumberList(final List<AddServiceEngineer> serviceEngineerList) {
        //if size is 1 we are calling directly else showing list popup
        int size = serviceEngineerList.size();
        if (size == 1) {
            callPhoneNumber(serviceEngineerList.get(serviceEngineerList.size() - 1).getMobileNumber());
            return;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        for (AddServiceEngineer addServiceEngineer : serviceEngineerList) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TextUtils.isEmpty(addServiceEngineer.getName()) ? "" : addServiceEngineer.getName() + " - ");
            stringBuilder.append(addServiceEngineer.getMobileNumber());
            arrayAdapter.add(stringBuilder.toString());
        }
        builderSingle.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber = serviceEngineerList.get(which).getMobileNumber();
                callPhoneNumber(phoneNumber);
            }
        });
        builderSingle.show();
    }

    /**
     * if options is grater than 5 we are adding horizontall scrollview else adding in linear layout
     *
     * @param parentLayout
     * @param namesArray
     * @param imagesArray
     * @param onClickListener
     */
    public void setBottomViewOptions(LinearLayout parentLayout, String[] namesArray, int[] imagesArray,int[] tagsArray, View.OnClickListener onClickListener) {
        int length = namesArray.length;

        boolean isScrollAdded = length > 5 ? true : false;
        HorizontalScrollView horizontalScrollView = null;
        LinearLayout linearLayout = null;
        //Implemented in scroll view
        if (isScrollAdded) {
            horizontalScrollView = getcustomHorizontalScroll();
            linearLayout = new LinearLayout(getActivity());
            horizontalScrollView.addView(linearLayout);
            parentLayout.addView(horizontalScrollView);
        }

        for (int i = 0; i < length; i++) {
            LinearLayout customBottomView = getCustomBottomView(isScrollAdded);

            getBottomTextView(customBottomView).setText(namesArray[i]);
            getBottomImageView(customBottomView).setImageResource(imagesArray[i]);

            customBottomView.setTag(tagsArray[i]);
            customBottomView.setOnClickListener(onClickListener);
            if (horizontalScrollView != null) {
                linearLayout.addView(customBottomView);
            } else {
                parentLayout.addView(customBottomView);
            }


            /*String finalTag;
            if (tag.equalsIgnoreCase("-1")) {
                finalTag = String.valueOf(i);
            } else {
                finalTag = tag + COMMA_SEPARATOR + i;
            }
            Logger.e("Test tag", finalTag + " , tag1");
            customBottomView.setTag(finalTag);
            customBottomView.setOnClickListener(onClickListener);*/
            /*customBottomView.setTag(tagsArray[i]);
            customBottomView.setOnClickListener(onClickListener);

            if (horizontalScrollView != null) {
                linearLayout.addView(customBottomView);
            } else {
                parentLayout.addView(customBottomView);
            }*/
        }
    }

    private HorizontalScrollView getcustomHorizontalScroll() {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        return horizontalScrollView;
    }

    public LinearLayout getCustomBottomView(boolean isScroll) {

        int dp5 = (int) DeviceUtils.convertPxToDp(5);
        Context context = getContext();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(dp5, dp5, dp5, dp5);
        linearLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llp;

        if (isScroll) {
            llp = new LinearLayout.LayoutParams((int) DeviceUtils.convertPxToDp(80), ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            llp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.weight = 1;
        }
        linearLayout.setLayoutParams(llp);
        int dp24 = (int) DeviceUtils.convertPxToDp(20);
        AppCompatImageView imageView = new AppCompatImageView(context);
        imageView.setId(R.id.view_logo);
        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(dp24, dp24);
        imageView.setLayoutParams(imageViewLayoutParams);
        imageView.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
        linearLayout.addView(imageView);

        TextView textView = new TextView(context);
        textView.setId(R.id.view_tv);
        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(DeviceUtils.convertSpToPixels(4, getActivity()));
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

    public AppCompatImageView getBottomImageView(LinearLayout linearLayout) {
        return ((AppCompatImageView) linearLayout.findViewById(R.id.view_logo));
    }

    public TextView getBottomTextView(LinearLayout linearLayout) {
        return ((TextView) linearLayout.findViewById(R.id.view_tv));
    }

}