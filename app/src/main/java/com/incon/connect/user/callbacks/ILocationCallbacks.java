package com.incon.connect.user.callbacks;

import com.google.android.gms.maps.model.LatLng;

public interface ILocationCallbacks {
    void onLocationListener(LatLng latLng);
}