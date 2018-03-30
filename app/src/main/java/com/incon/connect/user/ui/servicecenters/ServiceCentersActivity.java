package com.incon.connect.user.ui.servicecenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.databinding.ActivityServiceCentersBinding;
import com.incon.connect.user.databinding.BottomSheetPurchasedBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by MY HOME on 29-Dec-17.
 */

public class ServiceCentersActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private ArrayList<ServiceCenterResponse> serviceCentersList;

    public BottomSheetDialog bottomSheetDialog;
    public BottomSheetPurchasedBinding bottomSheetPurchasedBinding;
    private int markerSelectedPosition;
    private AppAlertDialog detailsDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_service_centers;
    }

    @Override
    protected void initializePresenter() {
    }

    // load bottom sheet
    public void loadBottomSheet() {
        bottomSheetPurchasedBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_purchased, null, false);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetPurchasedBinding.getRoot());
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        ActivityServiceCentersBinding binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setServiceCentersActivity(this);

        Intent intent = getIntent();
        serviceCentersList = intent.getParcelableArrayListExtra(IntentConstants.SERVICE_CENTER_DATA);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map_monitor, mapFragment).commit();
        mapFragment.getMapAsync(this);

        loadBottomSheet();
    }

    public void onOkClick() {
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        for (int i = 0; i < serviceCentersList.size(); i++) {
            ServiceCenterResponse serviceCenterResponse = serviceCentersList.get(i);
            String[] location = serviceCenterResponse.getLocation().split(COMMA_SEPARATOR);
            displayMarker(i, new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1])),
                    serviceCenterResponse.getName() + " \n" + serviceCenterResponse.getContactNo(), i == 0 ? true : false);
        }

        mGoogleMap.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            markerSelectedPosition = (int) marker.getTag();
            ;
            createBottomSheetFirstRow();
            bottomSheetDialog.show();

        }
    };

    private void createBottomSheetFirstRow() {

        ArrayList<Integer> drawablesArray = new ArrayList<>();
        ArrayList<String> textArray = new ArrayList<>();
        ArrayList<Integer> tagsArray = new ArrayList<>();


        textArray.add(getString(R.string.bottom_option_details));
        tagsArray.add(R.id.SERVICE_CENTER_DETAILS);
        drawablesArray.add(R.drawable.ic_option_service_support);

        textArray.add(getString(R.string.bottom_option_service_request));
        tagsArray.add(R.id.SERVICE_CENTER_REQUEST);
        drawablesArray.add(R.drawable.ic_option_product);

        textArray.add(getString(R.string.bottom_option_track));
        tagsArray.add(R.id.SERVICE_CENTER_TRACK);
        drawablesArray.add(R.drawable.ic_option_customer);

        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(tagsArray.size());
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, textArray, drawablesArray, tagsArray, bottomSheetFirstRowClickListener);

    }

    private View.OnClickListener bottomSheetFirstRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Integer tag = (Integer) view.getTag();
            changeSelectedViews(bottomSheetPurchasedBinding.firstRow, tag);
            if (tag == R.id.SERVICE_CENTER_DETAILS) {
                showInformationDialog(getString(R.string.bottom_option_details), "");
            } else if (tag == R.id.SERVICE_CENTER_REQUEST) {
                Intent intent = new Intent();
                intent.putExtra(IntentConstants.POSITION, markerSelectedPosition);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else if (tag == R.id.SERVICE_CENTER_TRACK) {
                String[] location = serviceCentersList.get(markerSelectedPosition).getLocation().split(",");
                onClickNavigattion(new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1])));
            }

        }
    };

    public static String getFormattedWarrantyDataInString(ProductInfoResponse itemFromPosition, Context context) {
        String purchasedDate = DateUtils.convertMillisToStringFormat(
                itemFromPosition.getPurchasedDate(), AppConstants.DateFormatterConstants.DD_MM_YYYY);
        String warrantyEndDate = DateUtils.convertMillisToStringFormat(
                itemFromPosition.getWarrantyEndDate(), AppConstants.DateFormatterConstants.DD_MM_YYYY);
        long noOfDays = DateUtils.convertDifferenceDateIndays(
                itemFromPosition.getWarrantyEndDate(), System.currentTimeMillis());
        String warrantyConditions = itemFromPosition.getWarrantyConditions();

        StringBuilder stringBuilder = new StringBuilder();

        //warranty days
        stringBuilder.append(context.getString(R.string.purchased_warranty_status));
        stringBuilder.append(noOfDays <= 0 ? context.getString(R.string.label_expired) : noOfDays + " Days Left");

        if (!TextUtils.isEmpty(purchasedDate)) {
            stringBuilder.append("\n");
            stringBuilder.append(context.getString(R.string.purchased_purchased_date));
            stringBuilder.append(purchasedDate);
        }

        if (!TextUtils.isEmpty(warrantyConditions)) {
            stringBuilder.append("\n");
            stringBuilder.append(context.getString(R.string.purchased_warranty_covers_date));
            stringBuilder.append(warrantyConditions);
        }

        stringBuilder.append("\n");
        stringBuilder.append(context.getString(R.string.purchased_warranty_ends_on));
        stringBuilder.append(warrantyEndDate);

        return stringBuilder.toString();
    }


    public void showInformationDialog(String title, String messageInfo) {
        detailsDialog = new AppAlertDialog.AlertDialogBuilder(this, new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                detailsDialog.dismiss();
                                break;
                            case AlertDialogCallback.CANCEL:
                                detailsDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(title).content(messageInfo)
                .build();
        detailsDialog.showDialog();
        detailsDialog.setCancelable(true);
    }
    private void displayMarker(int markerTag, LatLng latLng, String name, boolean zoomMap) {
        if (latLng != null) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng);
            Marker marker = mGoogleMap.addMarker(options);
            marker.setTag(markerTag);
            marker.setTitle(name);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            if (zoomMap) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                        GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            }
            marker.showInfoWindow();
        }
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
