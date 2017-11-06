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
    ArrayList personImages;
    FavoritesFragment context;
    @Override
    public HorizontalRecycleViewAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesHorizontalRecyclviewBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_favorites_horizontal_recyclview, parent, false);
        return new ViewHolder(binding);
    }


    public HorizontalRecycleViewAdapter(FavoritesFragment context, ArrayList personImages) {
        this.context = context;
        this.personImages = personImages;
    }

    @Override
    public void onBindViewHolder(HorizontalRecycleViewAdapter.ViewHolder holder, int position) {

       holder.binding.homeImageview.setImageResource(R.drawable.ic_connect_logo_svg);
        holder.binding.officeImageImageview.setImageResource(R.drawable.ic_connect_logo_svg);
        holder.binding.farmHouseImageImageview.setImageResource(R.drawable.ic_connect_logo_svg);
        holder.binding.addImageImageview.setImageResource(R.drawable.ic_connect_logo_svg);

    }

    @Override
    public int getItemCount() {
        return personImages.size();
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
          /*  AppUtils.loadImageFromApi(binding.brandImageview, favoritesResponse
                    .getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageImageview, favoritesResponse
                    .getProductImageUrl());*/
            binding.homeImageview.setImageResource(R.drawable.ic_connect_logo_svg);
            binding.officeImageImageview.setImageResource(R.drawable.ic_connect_logo_svg);
            binding.farmHouseImageImageview.setImageResource(R.drawable.ic_connect_logo_svg);
            binding.addImageImageview.setImageResource(R.drawable.ic_connect_logo_svg);
            binding.executePendingBindings();
        }


        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());

        }
    }
}
