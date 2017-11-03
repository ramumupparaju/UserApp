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
import com.incon.connect.user.apimodel.components.history.purchased.InterestHistoryResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemInterestFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public class InterestAdapter extends RecyclerView.Adapter
        <InterestAdapter.ViewHolder> {
    private List<InterestHistoryResponse> interestHistoryResponseList = new ArrayList<>();
    private List<InterestHistoryResponse> filteredInterestList = new ArrayList<>();
    private IClickCallback clickCallback;

    public InterestHistoryResponse getInterestDateFromPosition(int position) {
        return filteredInterestList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemInterestFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_interest_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InterestHistoryResponse interestResponse = filteredInterestList.get(position);
        holder.bind(interestResponse);

    }

    @Override
    public int getItemCount() {
        return filteredInterestList.size();
    }
    public InterestHistoryResponse getItemFromPosition(int position) {
        return filteredInterestList.get(position);
    }

    public void setData(List<InterestHistoryResponse> interestHistoryResponseList) {
       this.interestHistoryResponseList = interestHistoryResponseList;
        filteredInterestList.clear();
        filteredInterestList.addAll(interestHistoryResponseList);
        notifyDataSetChanged();

    }

    public void clearData() {
        interestHistoryResponseList.clear();
        notifyDataSetChanged();
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void searchData(String searchableString, String searchType) {
        filteredInterestList.clear();
        if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.NAME)) {
            for (InterestHistoryResponse interestHistoryResponse
                    : interestHistoryResponseList) {
                if (interestHistoryResponse.getProductName() != null
                        && interestHistoryResponse.getProductName().toLowerCase().startsWith(
                        searchableString.toLowerCase())) {
                    filteredInterestList.add(interestHistoryResponse);
                }
            }
        } else if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.BRAND)) {
            for (InterestHistoryResponse purchasedHistoryResponse
                    : interestHistoryResponseList) {
                if (purchasedHistoryResponse.getBrandName() != null && purchasedHistoryResponse
                        .getBrandName().toLowerCase().startsWith(
                                searchableString.toLowerCase())) {
                    filteredInterestList.add(purchasedHistoryResponse);
                }
            }
        } else {
            filteredInterestList.addAll(interestHistoryResponseList);
        }
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemInterestFragmentBinding binding;

        public ViewHolder(ItemInterestFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(InterestHistoryResponse interestHistoryResponse) {
            binding.setVariable(BR.interestHistoryResponse, interestHistoryResponse);
            AppUtils.loadImageFromApi(binding.brandImageview, interestHistoryResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageImageview, interestHistoryResponse
                    .getProductImageUrl());
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());

        }
    }


}
