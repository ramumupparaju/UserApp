package com.incon.connect.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AppUtils {

    public static int getViewWidth(View view) {
        WindowManager wm =
                (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
        }

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredWidth(); //        view.getMeasuredHeight();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

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

        Context context = imageView.getContext();
        GlideUrl glideUrl = new GlideUrl(BuildConfig.SERVICE_ENDPOINT + url, new LazyHeaders.Builder()
                .addHeader(AppConstants.ApiRequestKeyConstants.HEADER_AUTHORIZATION, context.getString(R.string.default_key))
                .build());

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(glideUrl)
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
