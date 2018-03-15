package com.incon.connect.user.ui.history.adapter;

import android.databinding.DataBindingUtil;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ItemShowroomFragmentBinding;
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.AddressFromLatLngAddress;

/**
 * Created by govin on 22-01-2018.
 */

public class ShowRoomAdapter extends BaseRecyclerViewAdapter {

    private AddressFromLatLngAddress addressFromLatLngAddress;
    private ConnectApplication context;

    public ShowRoomAdapter() {
        addressFromLatLngAddress = new AddressFromLatLngAddress();
        context = ConnectApplication.getAppContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemShowroomFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_showroom_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductInfoResponse showRoomResponse = filteredList.get(position);
        ((ShowRoomAdapter.ViewHolder) holder).bind(showRoomResponse);
    }

    private void loadLocationDetailsFromGeocoder(LatLng latLng, ProductInfoResponse productInfoResponse) {
        addressFromLatLngAddress.getAddressFromLocation(context,
                latLng, AppConstants.RequestCodes.LOCATION_ADDRESS_FROM_LATLNG, new LocationHandler(productInfoResponse));
    }

    private class LocationHandler extends Handler {

        private final ProductInfoResponse productInfoResponse;

        public LocationHandler(ProductInfoResponse productInfoResponse) {
            this.productInfoResponse = productInfoResponse;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            Address locationAddress = bundle.getParcelable(AppConstants.BundleConstants
                    .LOCATION_ADDRESS);
            if (locationAddress != null) {
                switch (message.what) {
                    case AppConstants.RequestCodes.LOCATION_ADDRESS_FROM_LATLNG:
                        productInfoResponse.setAddress(locationAddress.getAddressLine(0));
                        break;
                    default:
                        //do nothing
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemShowroomFragmentBinding binding;

        public ViewHolder(ItemShowroomFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse productInfoResponse) {
            binding.setVariable(BR.productinforesponse
                    , productInfoResponse);
            AppUtils.loadImageFromApi(binding.storeImageview, productInfoResponse.getProductImageUrl());

            String[] location = productInfoResponse.getLocation().split(",");
            loadLocationDetailsFromGeocoder(new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1])), productInfoResponse);
            if (productInfoResponse.isSelected()) {
                binding.viewsLayout.setVisibility(View.VISIBLE);
            } else {
                binding.viewsLayout.setVisibility(View.GONE);
            }
            binding.executePendingBindings();

        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());

        }
    }

}

