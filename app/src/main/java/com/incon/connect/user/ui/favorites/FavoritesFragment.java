package com.incon.connect.user.ui.favorites;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.FragmentFavoritesBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 11/4/2017.
 */

public class FavoritesFragment extends BaseFragment implements FavoritesContract.View {
    private FragmentFavoritesBinding binding;
    private  FavoritesPresenter favoritesPresenter;
    private  FavoritesAdapter favoritesAdapter;
    private List<FavoritesResponse> favoritesList;
    private View rootView;
    private FavoritesResponse favoritesResponse;
    private int userId;
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

        favoritesList = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter();
        favoritesAdapter.setData(favoritesList);
        favoritesAdapter.setClickCallback(iClickCallback);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), linearLayoutManager.getOrientation());
        binding.favoritesRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.favoritesRecyclerview.setAdapter(favoritesAdapter);
        binding.favoritesRecyclerview.setLayoutManager(linearLayoutManager);
        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        favoritesPresenter.doFavoritesProductApi(userId, getId());
    }
    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
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
    public void loadFavoritesProducts(List<FavoritesResponse> favoritesResponseList) {
        if (favoritesResponseList == null) {
            favoritesResponseList = new ArrayList<>();
        }
        this.favoritesList = favoritesResponseList;
        favoritesAdapter.setData(favoritesList);
        dismissSwipeRefresh();

    }
}
