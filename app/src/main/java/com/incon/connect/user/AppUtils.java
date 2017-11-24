package com.incon.connect.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppUtils {

    public static List<ProductInfoResponse> searchData(List<ProductInfoResponse> sourceList, String
            searchableString, String
            searchType) {
        List<ProductInfoResponse> filteredInterestList = new ArrayList<>();
        filteredInterestList.clear();
        if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.NAME)) {
            for (ProductInfoResponse interestHistoryResponse
                    : sourceList) {
                if (interestHistoryResponse.getProductName() != null
                        && interestHistoryResponse.getProductName().toLowerCase().startsWith(
                        searchableString.toLowerCase())) {
                    filteredInterestList.add(interestHistoryResponse);
                }
            }
        } else if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.BRAND)) {
            for (ProductInfoResponse purchasedHistoryResponse
                    : sourceList) {
                if (purchasedHistoryResponse.getBrandName() != null && purchasedHistoryResponse
                        .getBrandName().toLowerCase().startsWith(
                                searchableString.toLowerCase())) {
                    filteredInterestList.add(purchasedHistoryResponse);
                }
            }
        } else {
            filteredInterestList.addAll(sourceList);
        }
        return filteredInterestList;
    }

    private Comparator comparator = new Comparator<ProductInfoResponse>() {
        @Override
        public int compare(ProductInfoResponse o1, ProductInfoResponse o2) {
            /*try {
                Date a = DateUtils.convertStringToDate(o1.getCreatedDate(),
                        AppConstants.DateFormatterConstants.YYYY_MM_DD, TueoConstants
                                .DateFormatterConstants.YYYY_MM_DD);
                Date b = DateUtils.convertStringToDate(o2.getCreatedDate(),
                        AppConstants.DateFormatterConstants.YYYY_MM_DD, TueoConstants
                                .DateFormatterConstants.YYYY_MM_DD);
                return (a.compareTo(b));
            } catch (Exception e) {

            }*/

            return -1;

        }
    };

    public static void shortToast(Context context, String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
    }

    public static void callPhoneNumber(Context context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            Uri number = Uri.parse("tel:" + phoneNumber);
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            context.startActivity(callIntent);
        }
    }

    public static void showSnackBar(View view, String message) {

        final Snackbar snackbar = Snackbar.make(view, message, 6000);
        snackbar.setAction("dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public static void loadImageFromApi(ImageView imageView, String url) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_placeholder);
        requestOptions.error(R.drawable.ic_placeholder);
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(BuildConfig.SERVICE_ENDPOINT + url)
                .into(imageView);
    }

    public static void showSoftKeyboard(Context ctx, View v) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideSoftKeyboard(Context ctx, View v) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static String pojoToJson(Object response) {

        Gson gson = new GsonBuilder().create();
        return gson.toJson(response);
    }

    public static <T> T jsonToPojo(Class<T> aClass, String json) {

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, aClass);
    }


}
