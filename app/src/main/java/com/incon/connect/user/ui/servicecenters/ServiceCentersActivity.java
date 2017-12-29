package com.incon.connect.user.ui.servicecenters;

import android.databinding.DataBindingUtil;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.callbacks.ILocationCallbacks;
import com.incon.connect.user.databinding.ActivityServiceCentersBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.history.fragments.PurchasedContract;
import com.incon.connect.user.ui.history.fragments.PurchasedPresenter;
import com.incon.connect.user.utils.AddressFromLatLngAddress;
import com.incon.connect.user.utils.DeviceLocation;

import java.util.List;

/**
 * Created by MY HOME on 29-Dec-17.
 */

public class ServiceCentersActivity extends BaseActivity implements OnMapReadyCallback, PurchasedContract.View {
    private ActivityServiceCentersBinding binding;
    public GoogleMap mGoogleMap;
    private Marker marker;
    private LatLng locationAddress;
    private AddressFromLatLngAddress addressFromLatLngAddress;
    private PurchasedPresenter purchasedPresenter;
    private int brandId;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_service_centers;
    }

    @Override
    protected void initializePresenter() {
        purchasedPresenter = new PurchasedPresenter();
        purchasedPresenter.setView(this);
        setBasePresenter(purchasedPresenter);
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map_monitor, mapFragment).commit();
        mapFragment.getMapAsync(this);
        purchasedPresenter.nearByServiceCenters(brandId);
    }

    public void onOkClick() {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap == null) {
            mGoogleMap = googleMap;
            if (locationAddress != null) {
                displayMarker(locationAddress, true);
            } else {
                new DeviceLocation(this, iLocationCallbacks);
            }
            //createGeofenceMarker();
            mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
        }

    }

    ILocationCallbacks iLocationCallbacks = new ILocationCallbacks() {
        @Override
        public void onLocationListener(LatLng latLng) {
            displayMarker(latLng, true);
        }
    };

    private GoogleMap.OnMapLongClickListener onMapLongClickListener = new
            GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    displayMarker(latLng, false);
                }
            };


    private void createGeofenceMarker(int markerTag) {
        /*Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(geofenceData.getLatLng())
                .snippet("Tap here to remove geofence")
                .title(geofenceData.getGeofenceRequestId())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        marker.setTag(markerTag);*/
    }

    private void displayMarker(LatLng latLng, boolean zoomMap) {

        if (latLng != null) {
            this.locationAddress = latLng;
            if (marker == null) {
                MarkerOptions options = new MarkerOptions()
                        .position(latLng);
                marker = mGoogleMap.addMarker(options);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                        GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            } else {
                marker.setPosition(latLng);
            }
            if (zoomMap) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                        GoogleMapConstants.DEFAULT_ZOOM_LEVEL));
            }
            loadLocationDetailsFromGeocoder();
        }
    }

    private void loadLocationDetailsFromGeocoder() {

        addressFromLatLngAddress.getAddressFromLocation(ServiceCentersActivity.this,
                locationAddress, RequestCodes.LOCATION_ADDRESS_FROM_LATLNG,new LocationHandler());
    }

    @Override
    public void loadPurchasedHistory(List<ProductInfoResponse> productInfoResponses) {

    }

    @Override
    public void loadAddresses(List<AddUserAddressResponse> favoritesResponseList) {

    }

    @Override
    public void addedToFavorite() {

    }

    @Override
    public void transferMobileNumber(Object response) {

    }

    @Override
    public void deleteProduct(Object response) {

    }

    @Override
    public void serviceRequest() {

    }

    @Override
    public void nearByServiceCenters() {

    }

    @Override
    public void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> listOfServiceCenters) {

    }


    private class LocationHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            Address locationAddress = bundle.getParcelable(BundleConstants
                    .LOCATION_ADDRESS);
            if (locationAddress != null) {
                switch (message.what) {
                    case RequestCodes.LOCATION_ADDRESS_FROM_LATLNG:
                        break;
                    case RequestCodes.LOCATION_LATLNG_FROM_ADDRESS:
                        if (locationAddress.hasLatitude() && locationAddress.hasLongitude()) {
                            displayMarker(new LatLng(locationAddress.getLatitude(),
                                            locationAddress.getLongitude()),
                                    true);
                        }
                        break;
                    default:
                        //do nothing
                }
            } else {
                showErrorMessage(getString(R.string.error_location));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        purchasedPresenter.disposeAll();
    }
}
