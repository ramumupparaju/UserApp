package com.incon.connect.user.ui;

import android.support.v7.widget.RecyclerView;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<ProductInfoResponse> allDataResponseList = new ArrayList<>();
    public List<ProductInfoResponse> filteredList = new ArrayList<>();
    public IClickCallback clickCallback;


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
        filteredList = AppUtils.searchData(allDataResponseList, searchableString,
                searchType);
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
