package com.incon.connect.user.ui.servicecenters;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.incon.connect.user.R;
import com.incon.connect.user.ui.BaseActivity;

/**
 * Created by MY HOME on 29-Dec-17.
 */

public class ServiceCentersActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_service_centers;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        DataBindingUtil.setContentView(this, getLayoutId());
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map_monitor, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    public void onOkClick() {
        return;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        //TODO load markers
    }

    private void displayMarker(LatLng latLng, boolean zoomMap) {

        if (latLng != null) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng);
            mGoogleMap.addMarker(options);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            if (zoomMap) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                        GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            }
        }
    }
}
