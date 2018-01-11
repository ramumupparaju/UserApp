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
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.incon.connect.user.AppConstants.UpDateUserProfileValidation.MIN_DAYS;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class FavoritesAdapter extends BaseRecyclerViewAdapter {
    private List<ProductInfoResponse> favoritestResponseList = new ArrayList<>();
    private IClickCallback clickCallback;

    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_favorites_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductInfoResponse favoritesResponse = favoritestResponseList.get(position);
        ((FavoritesAdapter.ViewHolder) holder).bind(favoritesResponse, position);

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

        public void bind(ProductInfoResponse favoritesResponse, int position) {
            binding.setVariable(BR.favoritesResponse, favoritesResponse);

            AppUtils.loadImageFromApi(binding.brandImageview, favoritesResponse.getProductLogoUrl());
            AppUtils.loadImageFromApi(binding.productImageview, favoritesResponse.getProductImageUrl());
            // view layout used for changed colore
            if (favoritesResponse.isSelected()) {
                binding.viewsLayout.setVisibility(View.VISIBLE);
            } else {
                binding.viewsLayout.setVisibility(View.GONE);
            }
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.cardView.getLayoutParams();
            int leftRightMargin = (int) DeviceUtils.convertPxToDp(8);
            if (position == 0) {
                int topMargin = (int) DeviceUtils.convertPxToDp(6);
                int bottomMargin = (int) DeviceUtils.convertPxToDp(3);
                layoutParams.setMargins(leftRightMargin, topMargin, leftRightMargin, bottomMargin);
            } else if (position == filteredList.size()) {
                int topMargin = (int) DeviceUtils.convertPxToDp(3);
                int bottomMargin = (int) DeviceUtils.convertPxToDp(6);
                layoutParams.setMargins(leftRightMargin, topMargin, leftRightMargin, bottomMargin);
            } else {
                int topBottomMargin = (int) DeviceUtils.convertPxToDp(3);
                layoutParams.setMargins(leftRightMargin, topBottomMargin, leftRightMargin, topBottomMargin);
            }
            binding.productName.setText(favoritesResponse.getProductName());
           // binding.layoutFavoriteItem.setSelected(favoritesResponse.isSelected());
            long noOfDays = DateUtils.convertDifferenceDateIndays(favoritesResponse.getWarrantyEndDate(),
                    System.currentTimeMillis());
            if (noOfDays != 0) {
                binding.warrantyPeriod.setVisibility(View.VISIBLE);
                binding.warrantyPeriod.setText(binding.warrantyPeriod.getContext().getString(R.string.hint_warranty_period, noOfDays));
            } else {
                binding.warrantyPeriod.setVisibility(View.GONE);
            }
            if (noOfDays >= MIN_DAYS) {
                binding.warrentyIcon.setBackgroundColor(
                        binding.getRoot().getResources().getColor(R.color.green));
            } else if (noOfDays == 0) {
                binding.warrentyIcon.setBackgroundColor(
                        binding.getRoot().getResources().getColor(R.color.red));
            } else {
                binding.warrentyIcon.setBackgroundColor(
                        binding.getRoot().getResources().getColor(R.color.orange));
            }
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }

}
