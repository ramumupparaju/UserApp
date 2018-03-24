package com.incon.connect.user.ui.status.adapter.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.ServiceCenter;
import com.incon.connect.user.apimodel.components.ServiceRequest;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.apimodel.components.status.StatusList;
import com.incon.connect.user.callbacks.IStatusClickCallback;
import com.incon.connect.user.databinding.ItemServiceStatusListBinding;
import com.incon.connect.user.databinding.StatusViewBinding;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.utils.AddressFromLatLngAddress;
import com.incon.connect.user.utils.Logger;

import java.util.List;

import static com.incon.connect.user.AppUtils.getStatusName;

/**
 * Created by INCON TECHNOLOGIES on 12/25/2017.
 */

public class ServiceStatusAdapter extends RecyclerView.Adapter<ServiceStatusAdapter.ViewHolder>
        implements AppConstants.StatusDrawables {

    private List<ServiceStatus> serviceStatusList;
    private IStatusClickCallback clickCallback;
    private Context context;
    private AddressFromLatLngAddress addressFromLatLngAddress;

    public void setClickCallback(IStatusClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public ServiceStatusAdapter(Context context, List<ServiceStatus> serviceStatusList) {
        this.context = context;
        this.serviceStatusList = serviceStatusList;
        addressFromLatLngAddress = new AddressFromLatLngAddress();
    }

    @Override
    public ServiceStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemServiceStatusListBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_service_status_list, parent, false);
        return new ViewHolder(binding);
    }

    private void loadLocationDetailsFromGeocoder(LatLng locationAddress, int position) {
        addressFromLatLngAddress.getAddressFromLocation(context,
                locationAddress, AppConstants.RequestCodes.LOCATION_ADDRESS_FROM_LATLNG, new LocationHandler(position));
    }

    private class LocationHandler extends Handler {

        int position;

        public LocationHandler(int position) {
            this.position = position;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            Address locationAddress = bundle.getParcelable(AppConstants.BundleConstants
                    .LOCATION_ADDRESS);
            if (locationAddress != null) {
                switch (message.what) {
                    case AppConstants.RequestCodes.LOCATION_ADDRESS_FROM_LATLNG:
                        try {
                            serviceStatusList.get(position).getServiceCenter().setFormattedAddress(locationAddress.getAddressLine(0));
                        } catch (Exception e) {
                            Logger.e("LocationHandler class", "Address not found");
                        }
                        break;
                    default:
                        //do nothing
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ServiceStatusAdapter.ViewHolder holder, int position) {
        ServiceStatus serviceStatus = serviceStatusList.get(position);
        holder.bind(serviceStatus, position);

    }

    @Override
    public int getItemCount() {
        return serviceStatusList.size();
    }

    public void setData(List<ServiceStatus> serviceStatusList) {
        this.serviceStatusList = serviceStatusList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemServiceStatusListBinding binding;

        public ViewHolder(ItemServiceStatusListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.buttonAccept.setOnClickListener(this);
            binding.buttonReject.setOnClickListener(this);
            binding.buttonHold.setOnClickListener(this);
        }

        public void bind(ServiceStatus serviceStatus, int position) {
            binding.setVariable(BR.productinforesponse, serviceStatus);

            AppUtils.loadImageFromApi(binding.brandImageview, serviceStatus.getProduct().getLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, serviceStatus.getProduct().getImageUrl());

            List<StatusList> statusList = serviceStatus.getStatusList();
            if (statusList != null && statusList.size() > 0) {
                StatusList statusItem = statusList.get(statusList.size() - 1);
                if (statusItem.getRequest().getStatus() == AppConstants.StatusConstants.WAIT_APPROVE) {
                    binding.approvalViewsLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.approvalViewsLayout.setVisibility(View.GONE);
                }
            } else {
                binding.approvalViewsLayout.setVisibility(View.GONE);
            }
            createStatusView(binding, statusList, position);

            ServiceCenter serviceCenter = serviceStatus.getServiceCenter();
            if (TextUtils.isEmpty(serviceCenter.getFormattedAddress())) {
                String[] split = serviceCenter.getLocation().split(",");
                loadLocationDetailsFromGeocoder(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1])), position);
            }
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            int statusType = -1;
            if (view.getId() == R.id.button_accept) {
                statusType = AppConstants.StatusConstants.APPROVED;
            } else if (view.getId() == R.id.button_reject) {
                statusType = AppConstants.StatusConstants.REJECTED;
            } else if (view.getId() == R.id.button_hold) {
                statusType = AppConstants.StatusConstants.HOLD;
            }

            //checking whether clicked on status buttons or not
            if (statusType != -1) {
                clickCallback.onClickStatusButton(statusType);
            } else {
                clickCallback.onClickPosition(getAdapterPosition());
            }
        }
    }

    private void createStatusView(ItemServiceStatusListBinding binding, List<StatusList> statusList, int position) {

        int size = statusList.size();
        if (statusList == null || size == 0) {
            binding.statusLayout.setVisibility(View.GONE);
        } else {
            binding.statusLayout.setVisibility(View.VISIBLE);
            binding.statusLayout.removeAllViews();
            for (int i = 0; i < size; i++) {
                StatusList statusData = statusList.get(i);
                ServiceRequest serviceRequest = statusData.getRequest();
                LinearLayout linearLayout = new LinearLayout(context);
                StatusViewBinding statusView = getStatusView();
                statusView.viewTv.setText(getStatusName(serviceRequest.getStatus()));
                statusView.viewLeftLine.setVisibility(i == 0 ? View.INVISIBLE : View.VISIBLE);
                statusView.viewRightLine.setVisibility(i == size - 1 ? View.INVISIBLE : View.VISIBLE);
                View statusRootView = statusView.getRoot();
                statusRootView.setOnClickListener(onClickListener);
//                statusRootView.setTag(statusData.getServiceCenter().getContactNo());
                statusRootView.setTag(String.format("%1$s;%2$s", position, i));
                linearLayout.addView(statusRootView);
                binding.statusLayout.addView(linearLayout);
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            if (tag != null) {
                String[] positionsArray = ((String) tag).split(";");
                clickCallback.onClickStatus(Integer.parseInt(positionsArray[0]), Integer.parseInt(positionsArray[1]));
            }

        }
    };

    private StatusViewBinding getStatusView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.status_view, null, false);
    }
}
