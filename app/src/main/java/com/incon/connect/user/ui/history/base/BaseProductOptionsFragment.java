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
import com.incon.connect.user.ui.BaseActivity;
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
        ((BaseActivity) getActivity()).changeSelectedViews(parentLayout, selectedTag);
    }

    public void setBottomViewOptions(LinearLayout parentLayout, ArrayList<String> namesArray, ArrayList<Integer> imagesArray, ArrayList<Integer> tagsArray, View.OnClickListener onClickListener) {
        ((BaseActivity) getActivity()).setBottomViewOptions(parentLayout, namesArray, imagesArray, tagsArray, onClickListener);
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





}