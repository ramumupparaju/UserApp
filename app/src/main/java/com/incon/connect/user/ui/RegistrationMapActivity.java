package com.incon.connect.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.incon.connect.user.R;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.callbacks.ILocationCallbacks;
import com.incon.connect.user.databinding.ActivityRegistrationMapBinding;
import com.incon.connect.user.utils.DeviceLocation;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by LENOVO on 9/30/2017.
 */

public class RegistrationMapActivity extends BaseActivity implements OnMapReadyCallback {
    private ActivityRegistrationMapBinding binding;
    public GoogleMap mGoogleMap;
    private Marker marker;
    private String preZipCode;
    private LatLng locationAddress;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_registration_map;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setAddressActivity(this);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map_monitor, mapFragment).commit();
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent != null) {
            String stringExtra = intent.getStringExtra(IntentConstants.LOCATION_COMMA);
            if (!TextUtils.isEmpty(stringExtra)) {
                String[] splitLocation = stringExtra.split(COMMA_SEPARATOR);
                locationAddress = new LatLng(Double.parseDouble(
                        splitLocation[0]), Double.parseDouble(splitLocation[1]));
                binding.buttonOk.setText(getString(R.string.action_ok));
            }
        }
        addZipcodeWatcher();
    }

    public void onOkClick() {
        String zipCode = binding.edittextPincode.getText().toString();
        if (TextUtils.isEmpty(zipCode)) {
            showErrorMessage(getString(R.string.error_select_location));
            return;
        }

        Intent dataIntent = new Intent();
        StringBuilder addressBuilder = new StringBuilder(zipCode);
        addressBuilder.append(COMMA_SEPARATOR + binding.edittextCity.getText().toString());
        addressBuilder.append(COMMA_SEPARATOR + binding.edittextState.getText().toString());
        addressBuilder.append(COMMA_SEPARATOR + binding.edittextCountry.getText().toString());
        dataIntent.putExtra(IntentConstants.ADDRESS_COMMA, addressBuilder.toString());
        dataIntent.putExtra(IntentConstants.LOCATION_COMMA, locationAddress.latitude
                + COMMA_SEPARATOR + locationAddress.longitude);
        setResult(Activity.RESULT_OK, dataIntent);
        finish();
    }

    private void addZipcodeWatcher() {
        Observable<TextViewAfterTextChangeEvent> zipCodeChangeObserver =
                RxTextView.afterTextChangeEvents(binding.edittextPincode);
        DisposableObserver<TextViewAfterTextChangeEvent> observer =
                new DisposableObserver<TextViewAfterTextChangeEvent>() {

                    @Override
                    public void onNext(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                        String zipCode = textViewAfterTextChangeEvent.editable().toString();

                        if (!TextUtils.isEmpty(preZipCode)) {
                            if (zipCode.contains(preZipCode) && zipCode.length() > 5) {
                                AppUtils.hideSoftKeyboard(RegistrationMapActivity.this,
                                        binding.edittextPincode);
//                                registrationMapPresenter.doPostalCode(zipCode);
                                fetchLocationFromZipcode();
                            }
                        }
                        preZipCode = zipCode;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                };
        zipCodeChangeObserver.subscribe(observer);
    }

    private void fetchLocationFromZipcode() {

        if (!Geocoder.isPresent()) {
//todo error msg to fill manually
            return;
        }
        try {
            Geocoder geocoder = new Geocoder(RegistrationMapActivity.this, Locale.getDefault());
            List<Address> addressList = geocoder.
                    getFromLocationName(binding.edittextPincode.getText().toString(),
                            GoogleMapConstants.GEOCODER_MAX_ADDRESS_RESULTS);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                if (address.hasLatitude() && address.hasLongitude()) {
                    displayMarker(new LatLng(address.getLatitude(), address.getLongitude()),
                            true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    ILocationCallbacks iLocationCallbacks = new ILocationCallbacks() {
        @Override
        public void onLocationListener(LatLng latLng) {
            displayMarker(latLng, true);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap == null) {
            mGoogleMap = googleMap;
            if (locationAddress != null) {
                displayMarker(locationAddress, true);
            } else {
                new DeviceLocation(this, iLocationCallbacks);
            }
            /*UiSettings googlemapSettings = mGoogleMap.getUiSettings();
            googlemapSettings.setZoomControlsEnabled(true);
            googlemapSettings.setMyLocationButtonEnabled(true);
            googlemapSettings.setRotateGesturesEnabled(true);
            googlemapSettings.setMapToolbarEnabled(true);*/


            mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
        }
    }

    private GoogleMap.OnMapLongClickListener onMapLongClickListener = new
            GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    displayMarker(latLng, false);
                }
            };

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

        if (!Geocoder.isPresent()) {
//todo error msg to fill manually
            return;
        }

        //fetching address using geo coder
        Geocoder geocoder = new Geocoder(RegistrationMapActivity.this, Locale.getDefault());
        List<Address> list = null;

        LatLng latLng = locationAddress;
        try {
            list = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, GoogleMapConstants.GEOCODER_MAX_ADDRESS_RESULTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            Address address = list.get(0);
            binding.setAddress(address);
        } else {
            showErrorMessage(getString(R.string.error_location));
        }

    }
}
