package com.incon.connect.user.ui.status.adapter.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.ServiceRequest;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.apimodel.components.status.StatusList;
import com.incon.connect.user.callbacks.IStatusClickCallback;
import com.incon.connect.user.databinding.ItemServiceStatusListBinding;
import com.incon.connect.user.databinding.StatusViewBinding;

import java.util.List;

import static com.incon.connect.user.AppUtils.getDrawableFromRequestId;
import static com.incon.connect.user.AppUtils.getStatusName;

/**
 * Created by INCON TECHNOLOGIES on 12/25/2017.
 */

public class ServiceStatusAdapter extends RecyclerView.Adapter<ServiceStatusAdapter.ViewHolder>
        implements AppConstants.StatusDrawables {

    private Context context;
    private List<ServiceStatus> serviceStatusList;
    private IStatusClickCallback clickCallback;

    public void setClickCallback(IStatusClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public ServiceStatusAdapter(Context context, List<ServiceStatus> serviceStatusList) {
        this.context = context;
        this.serviceStatusList = serviceStatusList;
    }

    @Override
    public ServiceStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemServiceStatusListBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_service_status_list, parent, false);
        return new ViewHolder(binding);
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

            AppUtils.loadImageFromApi(binding.brandImageview, serviceStatus.getProduct().getName()); //TODO have to change from url
            AppUtils.loadImageFromApi(binding.productImageview, serviceStatus.getProduct().getName()); //TODO have to change from url

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
            createStatusView(binding, statusList);
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

    private void createStatusView(ItemServiceStatusListBinding binding, List<StatusList> statusList) {

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
                statusView.viewLogo.setImageResource(getDrawableFromRequestId(serviceRequest.getStatus()));
                if (i == size - 1) {
                    statusView.viewLine.setVisibility(View.GONE);
                } else {
                    statusView.viewLine.setVisibility(View.VISIBLE);
                }
                View statusRootView = statusView.getRoot();
                statusRootView.setOnClickListener(onClickListener);
                statusRootView.setTag(statusData.getServiceCenter().getContactNo());
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
                AppUtils.callPhoneNumber(context, (String) tag);
            }

        }
    };

    private StatusViewBinding getStatusView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.status_view, null, false);
    }
}
