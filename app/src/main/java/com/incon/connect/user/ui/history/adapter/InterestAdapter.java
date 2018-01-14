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
        ((InterestAdapter.ViewHolder) holder).bind(interestResponse, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemInterestFragmentBinding binding;

        public ViewHolder(ItemInterestFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse interestHistoryResponse, int position) {
            binding.setVariable(BR.productinforesponse, interestHistoryResponse);

            binding.interestDate.setText(DateUtils.convertMillisToStringFormat(interestHistoryResponse.getRequestedDate()
                    , AppConstants.DateFormatterConstants.DD_MM_YYYY));

            //TODO have to move constant
            String status = interestHistoryResponse.getStatus();
            if (!TextUtils.isEmpty(status)) {
                if (status.equalsIgnoreCase(AppConstants.StatusConstants.PENDING)) {
                    status = "Request pending";
                } else if (status.equalsIgnoreCase(AppConstants.StatusConstants.BUY_REQUEST_ACCEPT)) {
                    status = "Request accepted";
                } else if (status.equalsIgnoreCase(AppConstants.StatusConstants.BUY_REQUEST_REJECT)) {
                    status = "Request rejected";
                } else {
                    status = "";
                }
            }
            if (TextUtils.isEmpty(status)) {
                binding.statusTv.setVisibility(View.GONE);
            } else {
                binding.statusTv.setVisibility(View.VISIBLE);
                binding.statusTv.setText("Status:" + status);
            }

            String merchantComments = interestHistoryResponse.getMerchantComments();
            if (TextUtils.isEmpty(merchantComments)) {
                binding.commentTv.setVisibility(View.INVISIBLE);
            } else {
                binding.commentTv.setVisibility(View.VISIBLE);
            }
            AppUtils.loadImageFromApi(binding.brandImageview, interestHistoryResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, interestHistoryResponse
                    .getProductImageUrl());


            binding.viewsLayout.setVisibility(interestHistoryResponse.isSelected() ? View.VISIBLE : View.GONE);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());

        }
    }

}
