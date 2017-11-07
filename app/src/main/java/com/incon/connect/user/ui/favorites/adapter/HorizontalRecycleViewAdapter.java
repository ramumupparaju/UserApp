package com.incon.connect.user.ui.favorites.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemFavoritesHorizontalRecyclviewBinding;
import com.incon.connect.user.ui.favorites.FavoritesFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 11/6/2017.
 */

public class HorizontalRecycleViewAdapter extends RecyclerView.Adapter
        <HorizontalRecycleViewAdapter.ViewHolder> {
    private List<FavoritesResponse> filteredFavoritesList = new ArrayList<>();
    private List<FavoritesResponse> favoritesList = new ArrayList<>();
    private IClickCallback clickCallback;
    ArrayList favoritesPlaces;
    ArrayList favoritesNames;
    FavoritesFragment context;
    @Override
    public HorizontalRecycleViewAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesHorizontalRecyclviewBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_favorites_horizontal_recyclview, parent, false);
        return new ViewHolder(binding);
    }
    public HorizontalRecycleViewAdapter(FavoritesFragment context, ArrayList favoritesPlaces,
                                        ArrayList favoritesNames) {
        this.context = context;
        this.favoritesPlaces = favoritesPlaces;
        this.favoritesNames = favoritesNames;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       holder.binding.homeImageview.setImageResource((Integer) favoritesPlaces.get(position));
       holder.binding.homeText.setText((String) favoritesNames.get(position));
    }

    public FavoritesResponse getItemFromPosition(int position) {
        return filteredFavoritesList.get(position);
    }
    @Override
    public int getItemCount() {
        return favoritesPlaces.size(); }

    public void setData(List<FavoritesResponse> filteredFavoritesList) {
        favoritesList = filteredFavoritesList;
        filteredFavoritesList.addAll(filteredFavoritesList);
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredFavoritesList.clear();
        notifyDataSetChanged();
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
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
