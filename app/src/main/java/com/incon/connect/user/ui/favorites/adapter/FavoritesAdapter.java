package com.incon.connect.user.ui.favorites.adapter;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemFavoritesFragmentBinding;
import com.incon.connect.user.ui.BaseRecyclerViewAdapter;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.DeviceUtils;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class FavoritesAdapter extends BaseRecyclerViewAdapter {
    private int warrantyLayoutWidth = -1;

    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_favorites_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductInfoResponse favoritesResponse = filteredList.get(position);
        ((FavoritesAdapter.ViewHolder) holder).bind(favoritesResponse, position);

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
            final long totalWarrantyDays = DateUtils.convertDifferenceDateIndays(favoritesResponse.getWarrantyEndDate(),
                    favoritesResponse.getPurchasedDate());
            // binding.layoutFavoriteItem.setSelected(favoritesResponse.isSelected());
            final long warrantyRemainingDays = DateUtils.convertDifferenceDateIndays(favoritesResponse.getWarrantyEndDate(),
                    System.currentTimeMillis());

            //if warranty expires we are showing red dot in corner else showing in a bar with text
            final RelativeLayout warrantyLayout = binding.warrantyLayout;
            if (warrantyRemainingDays <= 0) {
                binding.warrentyIcon.setBackgroundColor(
                        binding.getRoot().getResources().getColor(R.color.red));
                binding.warrentyIcon.setVisibility(View.VISIBLE);
                warrantyLayout.setVisibility(View.GONE);
                binding.warrantyPeriod.setVisibility(View.GONE);

            } else {
                binding.warrentyIcon.setVisibility(View.GONE);
                warrantyLayout.setVisibility(View.VISIBLE);
                binding.warrantyPeriod.setVisibility(View.VISIBLE);
                binding.warrantyPeriod.setText(binding.warrantyPeriod.getContext().getString(R.string.label_expires_dollar, warrantyRemainingDays));
                if (warrantyLayoutWidth == -1) {
                    //Displaying progress bar
                    ViewTreeObserver vto = warrantyLayout.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                warrantyLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                warrantyLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            warrantyLayoutWidth = warrantyLayout.getMeasuredWidth();
                            FavoritesAdapter.this.notifyDataSetChanged();
                        }
                    });
                } else {
                    RelativeLayout.LayoutParams progressViewParams = (RelativeLayout.LayoutParams) binding.progressView.getLayoutParams();

                    progressViewParams.width = warrantyLayoutWidth;
                    RelativeLayout.LayoutParams progressStatusParams = (RelativeLayout.LayoutParams) binding.progressStatusView.getLayoutParams();
                    int warrantyExpiredWidth = warrantyLayoutWidth - (int) ((warrantyLayoutWidth * warrantyRemainingDays) / totalWarrantyDays);
                    progressStatusParams.width = warrantyExpiredWidth;
                }

            }
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            clickCallback.onClickPosition(getAdapterPosition());
        }

    }

}
