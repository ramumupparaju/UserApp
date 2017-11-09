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
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemInterestFragmentBinding;
import com.incon.connect.user.utils.DateUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public class InterestAdapter extends RecyclerView.Adapter
        <InterestAdapter.ViewHolder> {
    private List<ProductInfoResponse> interestHistoryResponseList = new ArrayList<>();
    private List<ProductInfoResponse> filteredInterestList = new ArrayList<>();
    private IClickCallback clickCallback;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemInterestFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_interest_fragment, parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductInfoResponse interestResponse = filteredInterestList.get(position);
        holder.bind(interestResponse);
    }

    private Comparator comparator = new Comparator<ProductInfoResponse>() {
        @Override
        public int compare(ProductInfoResponse o1, ProductInfoResponse o2) {
            try {
                /*Date a = DateUtils.convertStringToDate(o1.getCreatedDate(),
                        AppConstants.DateFormatterConstants.YYYY_MM_DD, TueoConstants
                                .DateFormatterConstants.YYYY_MM_DD);
                Date b = DateUtils.convertStringToDate(o2.getCreatedDate(),
                        AppConstants.DateFormatterConstants.YYYY_MM_DD, TueoConstants
                                .DateFormatterConstants.YYYY_MM_DD);
                return (a.compareTo(b));*/
            } catch (Exception e) {

            }

            return -1;

        }
    };
    @Override
    public int getItemCount() {
        return filteredInterestList.size();
    }
    public ProductInfoResponse getItemFromPosition(int position) {
        return filteredInterestList.get(position);
    }

    public ProductInfoResponse getInterestDateFromPosition(int position) {
        return filteredInterestList.get(position);
    }

    public void setData(List<ProductInfoResponse> interestHistoryResponseList) {
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

    public void clearSelection() {
        for (ProductInfoResponse interestHistoryResponse : filteredInterestList) {
            interestHistoryResponse.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void searchData(String searchableString, String searchType) {
        filteredInterestList.clear();
        if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.NAME)) {
            for (ProductInfoResponse interestHistoryResponse
                    : interestHistoryResponseList) {
                if (interestHistoryResponse.getProductName() != null
                        && interestHistoryResponse.getProductName().toLowerCase().startsWith(
                        searchableString.toLowerCase())) {
                    filteredInterestList.add(interestHistoryResponse);
                }
            }
        } else if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.BRAND)) {
            for (ProductInfoResponse purchasedHistoryResponse
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

        public void bind(ProductInfoResponse interestHistoryResponse) {
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
