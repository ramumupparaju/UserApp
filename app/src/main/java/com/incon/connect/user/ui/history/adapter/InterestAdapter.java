package com.incon.connect.user.ui.history.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ItemInterestFragmentBinding;
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.DateUtils;

/**
 * Created by PC on 10/2/2017.
 */

public class InterestAdapter extends BaseRecyclerViewAdapter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemInterestFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_interest_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductInfoResponse interestResponse = filteredList.get(position);
        ((InterestAdapter.ViewHolder) holder).bind(interestResponse);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemInterestFragmentBinding binding;

        public ViewHolder(ItemInterestFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse interestHistoryResponse) {
            binding.interestDate.setText(DateUtils.convertMillisToStringFormat(interestHistoryResponse.getRequestedDate()
                    , AppConstants.DateFormatterConstants.LOCAL_DATE_DD_MM_YYYY_HH_MM));
            binding.setVariable(BR.productinforesponse, interestHistoryResponse);
            
            AppUtils.loadImageFromApi(binding.brandImageview, interestHistoryResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, interestHistoryResponse
                    .getProductImageUrl());
            binding.layoutInterestItem.setSelected(interestHistoryResponse.isSelected());
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());

        }
    }

}
