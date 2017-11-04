package com.incon.connect.user.ui.favorites.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemFavoritesFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class FavoritesAdapter extends RecyclerView.Adapter
        <FavoritesAdapter.ViewHolder> {
    private List<FavoritesResponse> filteredFavoritesList = new ArrayList<>();
    private List<FavoritesResponse> favoritesList = new ArrayList<>();
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
        FavoritesResponse favoritesResponse = filteredFavoritesList.get(position);
        holder.bind(favoritesResponse);
    }


    public FavoritesResponse getItemFromPosition(int position) {
        return filteredFavoritesList.get(position);
    }

    @Override
    public int getItemCount() {
        return filteredFavoritesList.size();
    }


    public void setData(List<FavoritesResponse> filteredFavoritesList) {
        favoritesList = filteredFavoritesList;
        filteredFavoritesList.addAll(filteredFavoritesList);
        notifyDataSetChanged();
    }

    public void searchData(String searchableString, String searchType) {
        filteredFavoritesList.clear();
        switch (searchType) {
            case AppConstants.FilterConstants.NAME:
                for (FavoritesResponse favoritesResponse
                        : favoritesList) {
                    if (favoritesResponse.getProductName().toLowerCase().startsWith(
                            searchableString.toLowerCase())) {
                        filteredFavoritesList.add(favoritesResponse);
                    }
                }
                break;
            case AppConstants.FilterConstants.BRAND:
                for (FavoritesResponse favoritesResponse
                        : favoritesList) {
                    if (favoritesResponse.getBrandName().toLowerCase().startsWith(
                            searchableString.toLowerCase())) {
                        filteredFavoritesList.add(favoritesResponse);
                    }
                }
                break;
            default:
                filteredFavoritesList.addAll(favoritesList);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredFavoritesList.clear();
        notifyDataSetChanged();
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemFavoritesFragmentBinding binding;

        public ViewHolder(ItemFavoritesFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }


        public void bind(FavoritesResponse favoritesResponse) {
            binding.setVariable(BR.favoritesResponse, favoritesResponse);
            AppUtils.loadImageFromApi(binding.brandImageview, favoritesResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageImageview, favoritesResponse
                    .getProductImageUrl());
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }

}
