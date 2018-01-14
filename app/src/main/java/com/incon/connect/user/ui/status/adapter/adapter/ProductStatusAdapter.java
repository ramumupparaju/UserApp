package com.incon.connect.user.ui.status.adapter.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.ServiceRequest;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;
import com.incon.connect.user.apimodel.components.status.StatusList;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.IStatusClickCallback;
import com.incon.connect.user.databinding.ItemProductStatusListBinding;
import com.incon.connect.user.databinding.ItemServiceStatusListBinding;
import com.incon.connect.user.databinding.StatusViewBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.incon.connect.user.AppUtils.getDrawableFromRequestId;
import static com.incon.connect.user.AppUtils.getStatusName;

/**
 * Created by INCON TECHNOLOGIES on 12/25/2017.
 */
public class ProductStatusAdapter extends RecyclerView.Adapter<ProductStatusAdapter.ViewHolder> {

    private List<ProductInfoResponse> productStatusList = new ArrayList<>();
    private IStatusClickCallback clickCallback;
    private Context context;

    public void setClickCallback(IStatusClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public ProductStatusAdapter(List<ProductInfoResponse> productsList) {
        this.productStatusList = productsList;
    }

    @Override
    public ProductStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductStatusListBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_product_status_list, parent, false);

        if (context == null) {
            context = binding.getRoot().getContext();
        }
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ProductStatusAdapter.ViewHolder holder, int position) {
        ProductInfoResponse productStatus = productStatusList.get(position);
        holder.bind(productStatus, position);

    }

    @Override
    public int getItemCount() {
        return productStatusList.size();
    }

    public void setData(List<ProductInfoResponse> productStatusList) {
        this.productStatusList = productStatusList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemProductStatusListBinding binding;

        public ViewHolder(ItemProductStatusListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse productStatus, int position) {
            binding.setVariable(BR.modelResponse, productStatus);

            //TODO remove hard coding
            binding.nameTv.setText("StoreName:" + productStatus.getStoreName() +
                    ", model name: " + productStatus.getName());

            createStatusView(binding, productStatus);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }

    private void createStatusView(ItemProductStatusListBinding binding, ProductInfoResponse productInfoResponse) {

        List<DefaultStatusData> statusList = productInfoResponse.getStatusList();
        int size = statusList.size();
        if (statusList == null || size == 0) {
            binding.statusLayout.setVisibility(View.GONE);
        } else {
            binding.statusLayout.setVisibility(View.VISIBLE);
            binding.statusLayout.removeAllViews();
            for (int i = 0; i < size; i++) {
                int statusId = statusList.get(i).getId();
                LinearLayout linearLayout = new LinearLayout(context);
                StatusViewBinding statusView = getStatusView();
                statusView.viewTv.setText(AppUtils.getStatusName(statusId));
                statusView.viewLogo.setImageResource(AppUtils.getDrawableFromRequestId(statusId));
                if (i == size - 1) {
                    statusView.viewLine.setVisibility(View.GONE);
                } else {
                    statusView.viewLine.setVisibility(View.VISIBLE);
                }
                View statusRootView = statusView.getRoot();
                statusRootView.setOnClickListener(onClickListener);
                statusRootView.setTag(productInfoResponse.getStoreContactNumber());
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
