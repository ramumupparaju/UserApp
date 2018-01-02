package com.incon.connect.user.ui.servicecenters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.databinding.ActivityServiceCentersBinding;
import com.incon.connect.user.ui.BaseActivity;

import java.util.ArrayList;

/**
 * Created by MY HOME on 29-Dec-17.
 */

public class ServiceCentersActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private ArrayList<ServiceCenterResponse> serviceCentersList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_service_centers;
    }

    @Override
    protected void initializePresenter() {
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
            displayMarker(new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1])),
                    serviceCenterResponse.getName() + " \n" + serviceCenterResponse.getContactNo(), i == 0 ? true : false);
        }
    }

    private void displayMarker(LatLng latLng, String name, boolean zoomMap) {

        if (latLng != null) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng);
            Marker marker = mGoogleMap.addMarker(options);
            marker.setTitle(name);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            if (zoomMap) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                        GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            }
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
