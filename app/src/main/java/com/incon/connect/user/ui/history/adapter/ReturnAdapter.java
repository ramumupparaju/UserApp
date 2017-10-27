package com.incon.connect.user.ui.history.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.history.purchased.ReturnHistoryResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemReturnFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public class ReturnAdapter extends  RecyclerView.Adapter
        <ReturnAdapter.ViewHolder>  {
    private List<ReturnHistoryResponse> returnList = new ArrayList<>();
    private IClickCallback clickCallback;

    @Override
    public ReturnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemReturnFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_return_fragment, parent, false);
        return  new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ReturnAdapter.ViewHolder holder, int position) {
        ReturnHistoryResponse returnHistoryResponse = returnList.get(position);
        holder.bind(returnHistoryResponse);
    }

    @Override
    public int getItemCount() {
        return returnList.size();
    }


    public void setData(List<ReturnHistoryResponse> returnHistoryResponseList) {
        returnList = returnHistoryResponseList;
        notifyDataSetChanged();
    }

    public void clearData() {
        returnList.clear();
        notifyDataSetChanged();
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemReturnFragmentBinding binding;

        public ViewHolder(ItemReturnFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }


        public void bind(ReturnHistoryResponse returnHistoryResponse) {
            binding.setVariable(BR.returnHistoryResponse
                    , returnHistoryResponse);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }

}
