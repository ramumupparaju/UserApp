package com.incon.connect.user.ui.favorites;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.FragmentFavoritesBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.favorites.adapter.HorizontalRecycleViewAdapter;
import com.incon.connect.user.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PC on 11/4/2017.
 */

public class FavoritesFragment extends BaseFragment implements FavoritesContract.View {
    private FragmentFavoritesBinding binding;
    private FavoritesPresenter favoritesPresenter;
    private FavoritesAdapter favoritesAdapter;
    private HorizontalRecycleViewAdapter horizontalRecycleViewAdapter;
    private List<ProductInfoResponse> favoritesList;
    private View rootView;
    private FavoritesResponse favoritesResponse;
    private int userId;
    private int productSelectedPosition;
    ArrayList favoritesNames = new ArrayList<>(Arrays.asList("Home", "Office", "Farm House",
            "Add New"));
    ArrayList favoritesPlaces = new ArrayList<>(Arrays.asList(
            R.drawable.ic_connect_logo_svg, R.drawable.ic_option_call,
            R.drawable.ic_option_customer, R.drawable.ic_option_feedback));

    ArrayList favoritesImages = new ArrayList<>(Arrays.asList(
            R.drawable.ic_connect_logo_svg, R.drawable.ic_option_call,
            R.drawable.ic_option_customer, R.drawable.ic_option_feedback));

    @Override
    protected void initializePresenter() {
        favoritesPresenter = new FavoritesPresenter();
        favoritesPresenter.setView(this);
        setBasePresenter(favoritesPresenter);
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.title_favorites));
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_favorites, container, false);
            rootView = binding.getRoot();
            initViews();
        }
        setTitle();
        return rootView;
    }
    private void initViews() {
        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);
        //top recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecycleViewAdapter = new HorizontalRecycleViewAdapter(FavoritesFragment.this,
                favoritesPlaces, favoritesNames);
        horizontalRecycleViewAdapter.setClickCallback(iClickCallback);
        binding.favoritesHorizontalRecyclerview.setLayoutManager(linearLayoutManager);
        binding.favoritesHorizontalRecyclerview.setAdapter(horizontalRecycleViewAdapter);
        //bottom recyclerview
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), gridLayoutManager.getOrientation());
        favoritesAdapter = new FavoritesAdapter(FavoritesFragment.this, favoritesImages);
        // favoritesAdapter.setClickCallback(iClickCallback);
        binding.favoritesRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.favoritesRecyclerview.setAdapter(favoritesAdapter);
        binding.favoritesRecyclerview.setLayoutManager(gridLayoutManager);
    }

    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            favoritesAdapter.clearSelection();
            ProductInfoResponse favoritesResponse =
                    favoritesAdapter.
                            getItemFromPosition(position);
            favoritesResponse.setSelected(true);
            FavoritesResponse itemFromPosition = horizontalRecycleViewAdapter.getItemFromPosition(
                    productSelectedPosition);
            favoritesPresenter.doFavoritesProductApi(userId, itemFromPosition.getId());
        }
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    favoritesAdapter.clearData();
                }
            };

    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        favoritesPresenter.disposeAll();
    }

    @Override
    public void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList) {
        dismissSwipeRefresh();

    }
}
