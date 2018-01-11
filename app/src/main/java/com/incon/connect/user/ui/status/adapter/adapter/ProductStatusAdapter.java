package com.incon.connect.user.ui.status.adapter.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemProductStatusListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by INCON TECHNOLOGIES on 12/25/2017.
 */
public class ProductStatusAdapter extends RecyclerView.Adapter<ProductStatusAdapter.ViewHolder> {

    private List<ProductInfoResponse> productStatusList = new ArrayList<>();
    private IClickCallback clickCallback;

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public ProductStatusAdapter(List<ProductInfoResponse> usersList) {
        this.productStatusList = usersList;
    }

    @Override
    public ProductStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductStatusListBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_product_status_list, parent, false);
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
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }
}
