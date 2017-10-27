package com.incon.connect.user.ui.buyrequets.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.apimodel.components.buyrequest.BuyRequestResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemBuyRequestFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class BuyRequestAdapter extends RecyclerView.Adapter
        <BuyRequestAdapter.ViewHolder> {
    private List<BuyRequestResponse> filteredBuyRequestList = new ArrayList<>();
    private List<BuyRequestResponse> buyRequestList = new ArrayList<>();
    private IClickCallback clickCallback;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemBuyRequestFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_buy_request_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BuyRequestResponse purchasedResponse = filteredBuyRequestList.get(position);
        holder.bind(purchasedResponse);
    }

    @Override
    public int getItemCount() {
        return filteredBuyRequestList.size();
    }


    public void setData(List<BuyRequestResponse> buyRequestResponseList) {
        buyRequestList = buyRequestResponseList;
        filteredBuyRequestList.addAll(buyRequestResponseList);
        notifyDataSetChanged();
    }

    public void searchData(String searchableString, String searchType) {
        filteredBuyRequestList.clear();
        switch (searchType) {
            case AppConstants.FilterConstants.NAME:
                for (BuyRequestResponse buyRequestResponse
                        : buyRequestList) {
                    if (buyRequestResponse.getProductName().toLowerCase().startsWith(
                            searchableString.toLowerCase())) {
                        filteredBuyRequestList.add(buyRequestResponse);
                    }
                }
                break;
            case AppConstants.FilterConstants.BRAND:
                for (BuyRequestResponse buyRequestResponse
                        : buyRequestList) {
                    if (buyRequestResponse.getBrandName().toLowerCase().startsWith(
                            searchableString.toLowerCase())) {
                        filteredBuyRequestList.add(buyRequestResponse);
                    }
                }
                break;
            default:
                filteredBuyRequestList.addAll(buyRequestList);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredBuyRequestList.clear();
        notifyDataSetChanged();
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemBuyRequestFragmentBinding binding;

        public ViewHolder(ItemBuyRequestFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }


        public void bind(BuyRequestResponse buyRequestResponse) {
            binding.setVariable(BR.buyRequestResponse, buyRequestResponse);
            AppUtils.loadImageFromApi(binding.brandImageview, buyRequestResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageImageview, buyRequestResponse
                    .getProductImageUrl());
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }

}
