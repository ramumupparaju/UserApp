package com.incon.connect.user.ui.history.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.databinding.ItemPurchasedFragmentBinding;
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.DateUtils;

import java.util.Random;

import static com.incon.connect.user.AppConstants.UpDateUserProfileValidation.MIN_DAYS;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class PurchasedAdapter extends BaseRecyclerViewAdapter {

    @Override
    public PurchasedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPurchasedFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_purchased_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductInfoResponse purchasedHistoryResponse = filteredList.get(position);
        ((PurchasedAdapter.ViewHolder) holder).bind(purchasedHistoryResponse);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemPurchasedFragmentBinding binding;

        public ViewHolder(ItemPurchasedFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse purchasedHistoryResponse) {
            binding.setVariable(BR.productinforesponse, purchasedHistoryResponse);
            TextView statusInfo = binding.statusTv;

            //TODO have to change from api
            String [] statusArray = new String[3];
            statusArray[0] = AppConstants.StatusConstants.DISPATCHED;
            statusArray[1] = AppConstants.StatusConstants.INSTALLED;
            statusArray[2] = AppConstants.StatusConstants.DELIVERED;

            Random random = new Random();
            purchasedHistoryResponse.setStatus(statusArray[random.nextInt(3)]);
            String statusInfoString = "";
            if (purchasedHistoryResponse.getStatus().equals(AppConstants.StatusConstants.DISPATCHED)) {
                statusInfoString = "Dispatches on 15-11-2017";
            } else if (purchasedHistoryResponse.getStatus().equals(AppConstants.StatusConstants.INSTALLED)) {
                statusInfoString = "Waiting for installation";
            } else {
                statusInfoString = "Delivered";
            }

            statusInfo.setText(statusInfo.getContext().getString(R.string.info_purchased_status, statusInfoString));
            AppUtils.loadImageFromApi(binding.brandImageview, purchasedHistoryResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, purchasedHistoryResponse
                    .getProductImageUrl());
            binding.layoutPurchsedItem.setSelected(purchasedHistoryResponse.isSelected());
            if (purchasedHistoryResponse.getAddressId() != null) {
                binding.favouriteIcon.setVisibility(View.VISIBLE);
            } else {
                binding.favouriteIcon.setVisibility(View.GONE);
            }
            long noOfDays = DateUtils.convertDifferenceDateIndays(
                    purchasedHistoryResponse.getWarrantyEndDate()
                    , purchasedHistoryResponse.getPurchasedDate());
            if (noOfDays >= MIN_DAYS) {
                binding.warrentyIcon.setBackgroundColor(
                        binding.getRoot().getResources().getColor(R.color.green));
            } else if (noOfDays == 0) {
                binding.warrentyIcon.setBackgroundColor(
                        binding.getRoot().getResources().getColor(R.color.red));
            } else {
                binding.warrentyIcon.setBackgroundColor(
                        binding.getRoot().getResources().getColor(R.color.orange));
            }
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }
}
