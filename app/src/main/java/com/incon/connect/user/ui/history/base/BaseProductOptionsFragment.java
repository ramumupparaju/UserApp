package com.incon.connect.user.ui.history.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BuildConfig;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.BottomSheetPurchasedBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.utils.DeviceUtils;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

import static com.incon.connect.user.AppConstants.LoginPrefs.USER_NAME;


public abstract class BaseProductOptionsFragment extends BaseFragment {


    // share product details
    public void shareProductDetails(final ProductInfoResponse productInfoResponse) {
        showProgress("");
        final Context activity = getActivity();

        GlideUrl glideUrl = new GlideUrl(BuildConfig.SERVICE_ENDPOINT + productInfoResponse.getProductImageUrl(), new LazyHeaders.Builder()
                .addHeader(AppConstants.ApiRequestKeyConstants.HEADER_AUTHORIZATION, getActivity().getString(R.string.default_key))
                .build());

        Glide.with(this)
                .asBitmap()
                .load(glideUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, AppUtils.getLocalBitmapUri(resource, activity));
                        String contentToShare = getShareContent(productInfoResponse);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, contentToShare);
                        sharingIntent.setType("image/*");
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        hideProgress();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        String contentToShare = getShareContent(productInfoResponse);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, contentToShare);
                        sharingIntent.setType("text/html");
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        hideProgress();
                    }
                });
    }

    private String getShareContent(ProductInfoResponse productInfoResponse) {

        final String subjectToShare = String.format("Your friend %1$s wants to share details of this product. To know compete details install the connect application", SharedPrefsUtils.loginProvider().getStringPreference(USER_NAME));

//        Model num, product image, price, product name, store name, store contact number while sharing have to send
        StringBuilder stringBuilder = new StringBuilder(subjectToShare);
        stringBuilder.append(NEW_LINE);

        if (!TextUtils.isEmpty(productInfoResponse.getModelNumber())) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(String.format("Name: %1$s", productInfoResponse.getModelNumber()));
        }
        if (!TextUtils.isEmpty(productInfoResponse.getBrandName())) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(String.format("Brand: %1$s", productInfoResponse.getBrandName()));
        }
        if (!TextUtils.isEmpty(productInfoResponse.getName())) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(String.format("Model num: %1$s", productInfoResponse.getName()));
        }
        if (productInfoResponse.getMrp() != null) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(String.format("Price: %1$s", productInfoResponse.getMrp()));
        }

        if (!TextUtils.isEmpty(productInfoResponse.getStoreName())) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(String.format("Store Name: %1$s", productInfoResponse.getStoreName()));
        }
        if (!TextUtils.isEmpty(productInfoResponse.getStoreContactNumber())) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(String.format("Store num: %1$s", productInfoResponse.getStoreContactNumber()));
        }

        return stringBuilder.toString();
    }

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
    public void setBottomViewOptions(LinearLayout parentLayout, ArrayList<String> namesArray, ArrayList<Integer> imagesArray, ArrayList<Integer> tagsArray, View.OnClickListener onClickListener) {
        int length = namesArray.size();

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

            getBottomTextView(customBottomView).setText(namesArray.get(i));
            getBottomImageView(customBottomView).setImageResource(imagesArray.get(i));

            customBottomView.setTag(tagsArray.get(i));
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