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
import com.incon.connect.user.databinding.ItemPurchasedFragmentBinding;
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.DeviceUtils;

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
        ((PurchasedAdapter.ViewHolder) holder).bind(purchasedHistoryResponse, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemPurchasedFragmentBinding binding;

        public ViewHolder(ItemPurchasedFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse purchasedHistoryResponse, int position) {
            binding.setVariable(BR.productinforesponse, purchasedHistoryResponse);
            View root = binding.getRoot();
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.cardView.getLayoutParams();
                int leftRightMargin = (int) DeviceUtils.convertPxToDp(8);
            if (position == 0) {
                int topMargin = (int) DeviceUtils.convertPxToDp(6);
                int bottomMargin = (int) DeviceUtils.convertPxToDp(3);
                layoutParams.setMargins(leftRightMargin, topMargin, leftRightMargin, bottomMargin);
            } else if (position == filteredList.size()) {
                int topMargin = (int) DeviceUtils.convertPxToDp(3);
                int bottomMargin = (int) DeviceUtils.convertPxToDp(6);
                layoutParams.setMargins(leftRightMargin, topMargin, leftRightMargin, bottomMargin);
            } else {
                int topBottomMargin = (int) DeviceUtils.convertPxToDp(3);
                layoutParams.setMargins(leftRightMargin, topBottomMargin, leftRightMargin, topBottomMargin);
            }

            //settings date from millis
            binding.purchasedDate.setText(DateUtils.convertMillisToStringFormat(purchasedHistoryResponse
                    .getPurchasedDate(), AppConstants.DateFormatterConstants.DD_MM_YYYY));

            AppUtils.loadImageFromApi(binding.brandImageview, purchasedHistoryResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, purchasedHistoryResponse
                    .getProductImageUrl());

            if (purchasedHistoryResponse.isSelected()) {
                binding.viewsLayout.setVisibility(View.VISIBLE);
            } else {
                binding.viewsLayout.setVisibility(View.GONE);
            }

            //sets favorite icon based on address id
            if (purchasedHistoryResponse.getAddressId() != null) {
                binding.favouriteIcon.setVisibility(View.VISIBLE);
            } else {
                binding.favouriteIcon.setVisibility(View.GONE);
            }

            //setting warranty colors based on purchased date
            long noOfDays = DateUtils.convertDifferenceDateIndays(
                    purchasedHistoryResponse.getWarrantyEndDate()
                    , purchasedHistoryResponse.getPurchasedDate());
            if (noOfDays >= MIN_DAYS) {
                binding.warrentyIcon.setBackgroundColor(
                        root.getResources().getColor(R.color.green));
            } else if (noOfDays == 0) {
                binding.warrentyIcon.setBackgroundColor(
                        root.getResources().getColor(R.color.red));
            } else {
                binding.warrentyIcon.setBackgroundColor(
                        root.getResources().getColor(R.color.orange));
            }
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }
}
