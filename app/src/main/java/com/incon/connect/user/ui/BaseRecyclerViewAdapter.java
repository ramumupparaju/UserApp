package com.incon.connect.user.ui;

import android.support.v7.widget.RecyclerView;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.ui.history.adapter.InterestAdapter;
import com.incon.connect.user.ui.history.adapter.PurchasedAdapter;
import com.incon.connect.user.ui.history.adapter.ReturnAdapter;
import com.incon.connect.user.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<ProductInfoResponse> allDataResponseList = new ArrayList<>();
    public List<ProductInfoResponse> filteredList = new ArrayList<>();
    public IClickCallback clickCallback;

    public Comparator comparator = new Comparator<ProductInfoResponse>() {
        @Override
        public int compare(ProductInfoResponse o1, ProductInfoResponse o2) {
            try {
                Date a = null;
                Date b = null;
                if (BaseRecyclerViewAdapter.this instanceof PurchasedAdapter) {
                    a = DateUtils.convertMillsToDate(o1.getPurchasedDate());
                    b = DateUtils.convertMillsToDate(o2.getPurchasedDate());
                } else if (BaseRecyclerViewAdapter.this instanceof InterestAdapter) {
                    a = DateUtils.convertMillsToDate(o1.getRequestedDate());
                    b = DateUtils.convertMillsToDate(o2.getRequestedDate());
                } else if (BaseRecyclerViewAdapter.this instanceof ReturnAdapter) {
                    a = DateUtils.convertMillsToDate(o1.getReturnDate());
                    b = DateUtils.convertMillsToDate(o2.getReturnDate());
                }
                return (b.compareTo(a));
            } catch (Exception e) {

            }

            return -1;

        }
    };

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public ProductInfoResponse getItemFromPosition(int position) {
        return filteredList.get(position);
    }

    public void setData(List<ProductInfoResponse> returnHistoryResponseList) {
        this.allDataResponseList = returnHistoryResponseList;
        filteredList.clear();
        filteredList.addAll(returnHistoryResponseList);
        Collections.sort(filteredList, comparator);
        notifyDataSetChanged();
    }


    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void searchData(String searchableString, String searchType) {
        filteredList.clear();
        if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.NAME)) {
            for (ProductInfoResponse returnHistoryResponse
                    : allDataResponseList) {
                if (returnHistoryResponse.getProductName() != null
                        && returnHistoryResponse.getProductName().toLowerCase().startsWith(
                        searchableString.toLowerCase())) {
                    filteredList.add(returnHistoryResponse);
                }
            }
        } else if (searchType.equalsIgnoreCase(AppConstants.FilterConstants.BRAND)) {
            for (ProductInfoResponse returnHistoryResponse
                    : allDataResponseList) {
                if (returnHistoryResponse.getBrandName() != null && returnHistoryResponse
                        .getBrandName().toLowerCase().startsWith(
                                searchableString.toLowerCase())) {
                    filteredList.add(returnHistoryResponse);
                }
            }
        } else {
            filteredList.addAll(allDataResponseList);
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        for (ProductInfoResponse returnHistoryResponse : filteredList) {
            returnHistoryResponse.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        allDataResponseList.clear();
        notifyDataSetChanged();
    }

}
