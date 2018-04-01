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
import com.incon.connect.user.databinding.ItemReturnFragmentBinding;
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.DateUtils;

/**
 * Created by PC on 10/2/2017.
 */

public class ReturnAdapter extends BaseRecyclerViewAdapter {

    @Override
    public ReturnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemReturnFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_return_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductInfoResponse returnHistoryResponse = filteredList.get(position);
        ((ReturnAdapter.ViewHolder) holder).bind(returnHistoryResponse);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemReturnFragmentBinding binding;

        public ViewHolder(ItemReturnFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse returnHistoryResponse) {
            binding.setVariable(BR.productinforesponse
                    , returnHistoryResponse);
            binding.returnDate.setText(DateUtils.convertMillisToStringFormat(returnHistoryResponse
                    .getReturnDate(), AppConstants.DateFormatterConstants.LOCAL_DATE_DD_MM_YYYY_HH_MM));
            AppUtils.loadImageFromApi(binding.brandImageview, returnHistoryResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, returnHistoryResponse
                    .getProductImageUrl());

            if (returnHistoryResponse.isSelected()) {
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
