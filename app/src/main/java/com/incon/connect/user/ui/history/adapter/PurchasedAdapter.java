package com.incon.connect.user.ui.history.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.apimodel.components.history.purchased.PurchasedHistoryResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemPurchasedFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class PurchasedAdapter extends RecyclerView.Adapter
        <PurchasedAdapter.ViewHolder> {
    private List<PurchasedHistoryResponse> purchasedHistoryResponseList = new ArrayList<>();
    private List<PurchasedHistoryResponse> filteredPurchasedList = new ArrayList<>();
    private IClickCallback clickCallback;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPurchasedFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_purchased_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PurchasedHistoryResponse purchasedHistoryResponse = filteredPurchasedList.get(position);
        holder.bind(purchasedHistoryResponse);
    }

    @Override
    public int getItemCount() {
        return filteredPurchasedList.size();
    }

    public PurchasedHistoryResponse getItemFromPosition(int position) {
        return filteredPurchasedList.get(position);
    }


    public void setData(List<PurchasedHistoryResponse> purchasedHistoryResponseList) {
        this.purchasedHistoryResponseList = purchasedHistoryResponseList;
        filteredPurchasedList.clear();
        filteredPurchasedList.addAll(purchasedHistoryResponseList);
        notifyDataSetChanged();
    }

    public void searchData(String searchableString, String searchType) {
        filteredPurchasedList.clear();
        if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.NAME)) {
            for (PurchasedHistoryResponse purchasedHistoryResponse
                    : purchasedHistoryResponseList) {
                if (purchasedHistoryResponse.getProductName() != null
                        && purchasedHistoryResponse.getProductName().toLowerCase().startsWith(
                        searchableString.toLowerCase())) {
                    filteredPurchasedList.add(purchasedHistoryResponse);
                }
            }
        } else if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.BRAND)) {
            for (PurchasedHistoryResponse purchasedHistoryResponse
                    : purchasedHistoryResponseList) {
                if (purchasedHistoryResponse.getBrandName() != null && purchasedHistoryResponse
                        .getBrandName().toLowerCase().startsWith(
                                searchableString.toLowerCase())) {
                    filteredPurchasedList.add(purchasedHistoryResponse);
                }
            }
        } else {
            filteredPurchasedList.addAll(purchasedHistoryResponseList);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredPurchasedList.clear();
        notifyDataSetChanged();
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void clearSelection() {
        for (PurchasedHistoryResponse purchasedHistoryResponse : filteredPurchasedList) {
            purchasedHistoryResponse.setSelected(false);
        }
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemPurchasedFragmentBinding binding;

        public ViewHolder(ItemPurchasedFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }


        public void bind(PurchasedHistoryResponse purchasedHistoryResponse) {
            binding.setVariable(BR.purchasedHistoryResponse, purchasedHistoryResponse);
            AppUtils.loadImageFromApi(binding.brandImageview, purchasedHistoryResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageImageview, purchasedHistoryResponse
                    .getProductImageUrl());
            binding.layoutPurchsedItem.setSelected(purchasedHistoryResponse.isSelected());
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }
}
