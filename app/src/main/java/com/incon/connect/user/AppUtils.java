package com.incon.connect.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.ValidationUtils;

import java.util.Calendar;

import static com.incon.connect.user.AppConstants.RegistrationValidation.DOB_FUTURE_DATE;
import static com.incon.connect.user.AppConstants.RegistrationValidation.DOB_PERSON_LIMIT;
import static com.incon.connect.user.AppConstants.VALIDATION_SUCCESS;

public class AppUtils {

    public static void focusOnView(final ScrollView scrollView, final View view) {
        final Rect rect = new Rect(0, 0, view.getWidth(), view.getHeight());
        view.requestRectangleOnScreen(rect, false);
        /*scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, editTextView.getBottom());
            }
        });*/
    }

    /**
     * parsing warranty string
     * @param productInfoResponse
     * @return
     */
    public static String getWarrantyInformation(ProductInfoResponse productInfoResponse) {
        String days = "days";
        String months = "months";
        StringBuffer stringBuffer = new StringBuffer();
        Integer warrantyDays = productInfoResponse.getWarrantyDays();
        if (warrantyDays != null && warrantyDays != 0) {
            stringBuffer.append(warrantyDays + days);
        }

        Integer warrantyMonths = productInfoResponse.getWarrantyMonths();
        if (warrantyMonths != null && warrantyMonths != 0) {

            if (stringBuffer.toString().contains(days)) {
                stringBuffer.append(",");
            }
            stringBuffer.append(warrantyMonths + months);

        }

        Integer warrantyYears = productInfoResponse.getWarrantyYears();
        if (warrantyYears != null && warrantyYears != 0) {
            if (stringBuffer.toString().contains(days) || stringBuffer.toString().contains(months)) {
                stringBuffer.append(",");
            }
            stringBuffer.append(warrantyYears + "years");
        }

        return stringBuffer.toString();
    }

    public static int validateDob(String dob) {
        Calendar dobDate = Calendar.getInstance();
        long dobInMillis = DateUtils.convertStringFormatToMillis(
                dob, AppConstants.DateFormatterConstants.YYYY_MM_DD_SLASH);
        dobDate.setTimeInMillis(dobInMillis);
        // futurde date check
        if (ValidationUtils.isFutureDate(dobDate)) {
            return DOB_FUTURE_DATE;
        }

        int returnedYear = ValidationUtils.calculateAge(dobDate);
        if (returnedYear < AppConstants.AgeConstants.USER_DOB) {
            return DOB_PERSON_LIMIT;
        }
        return VALIDATION_SUCCESS;
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
