package com.incon.connect.user.ui;

import android.support.v7.widget.RecyclerView;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;

import java.util.ArrayList;
import java.util.Comparator;
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
            /*try {
                Date a = DateUtils.convertStringToDate(o1.getCreatedDate(),
                        AppConstants.DateFormatterConstants.YYYY_MM_DD, TueoConstants
                                .DateFormatterConstants.YYYY_MM_DD);
                Date b = DateUtils.convertStringToDate(o2.getCreatedDate(),
                        AppConstants.DateFormatterConstants.YYYY_MM_DD, TueoConstants
                                .DateFormatterConstants.YYYY_MM_DD);
                return (a.compareTo(b));
            } catch (Exception e) {

            }*/

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
