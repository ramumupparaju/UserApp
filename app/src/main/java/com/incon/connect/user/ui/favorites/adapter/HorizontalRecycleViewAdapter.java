package com.incon.connect.user.ui.favorites.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemFavoritesHorizontalRecyclviewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 11/6/2017.
 */

public class HorizontalRecycleViewAdapter extends RecyclerView.Adapter
        <HorizontalRecycleViewAdapter.ViewHolder> {
    private List<AddUserAddressResponse> addressResponsesList = new ArrayList<>();
    private IClickCallback clickCallback;

    public void setData(List<AddUserAddressResponse> addressResponses) {
        this.addressResponsesList = addressResponses;
        notifyDataSetChanged();
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @Override
    public HorizontalRecycleViewAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesHorizontalRecyclviewBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_favorites_horizontal_recyclview, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AddUserAddressResponse singleAddressResponse = addressResponsesList.get(position);
        holder.binding.homeText.setText(singleAddressResponse.getName());
    }

    public AddUserAddressResponse getItemFromPosition(int position) {
        return addressResponsesList.get(position);
    }

    @Override
    public int getItemCount() {
        return addressResponsesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemFavoritesHorizontalRecyclviewBinding binding;

        public ViewHolder(ItemFavoritesHorizontalRecyclviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(FavoritesResponse favoritesResponse) {
            binding.setVariable(BR.favoritesResponse, favoritesResponse);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }
    }
}
