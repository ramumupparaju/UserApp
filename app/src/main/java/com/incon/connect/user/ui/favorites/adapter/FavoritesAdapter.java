package com.incon.connect.user.ui.favorites.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemFavoritesFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class FavoritesAdapter extends RecyclerView.Adapter
        <FavoritesAdapter.ViewHolder> {
    private List<ProductInfoResponse> favoritestResponseList = new ArrayList<>();
    private IClickCallback clickCallback;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_favorites_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductInfoResponse favoritesResponse = favoritestResponseList.get(position);
       holder.bind(favoritesResponse);
     //holder.binding.productImageImageview.setImageResource((Integer) favoritesImages.get
                //(position));
    }
    public ProductInfoResponse getItemFromPosition(int position) {
        return favoritestResponseList.get(position);
    }

    @Override
    public int getItemCount() {
        return favoritestResponseList.size();
    }

    public void setData(List<ProductInfoResponse> favoritestResponseList) {
        this.favoritestResponseList = favoritestResponseList;
        notifyDataSetChanged();
    }
    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }
    public void clearSelection() {
        for (ProductInfoResponse favoritesResponse : favoritestResponseList) {
            favoritesResponse.setSelected(false);
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemFavoritesFragmentBinding binding;

        public ViewHolder(ItemFavoritesFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(ProductInfoResponse favoritesResponse) {
            binding.setVariable(BR.favoritesResponse, favoritesResponse);
            AppUtils.loadImageFromApi(binding.productImageview, favoritesResponse
                    .getProductImageUrl());
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }

}
