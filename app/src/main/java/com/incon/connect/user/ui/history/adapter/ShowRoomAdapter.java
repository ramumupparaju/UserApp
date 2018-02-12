package com.incon.connect.user.ui.history.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ItemInterestFragmentBinding;
import com.incon.connect.user.databinding.ItemShowroomFragmentBinding;
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.DateUtils;

/**
 * Created by govin on 22-01-2018.
 */

public class ShowRoomAdapter extends BaseRecyclerViewAdapter {

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
            AppUtils.loadImageFromApi(binding.brandImageview, productInfoResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, productInfoResponse
                    .getProductImageUrl());

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

