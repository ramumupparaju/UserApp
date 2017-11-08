package com.incon.connect.user.ui.favorites;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.TextAddressDialogCallback;
import com.incon.connect.user.custom.view.AppAddressDialog;
import com.incon.connect.user.databinding.FragmentFavoritesBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.favorites.adapter.HorizontalRecycleViewAdapter;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.utils.GravitySnapHelper;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PC on 11/4/2017.
 */

public class FavoritesFragment extends BaseFragment implements FavoritesContract.View,
        View.OnClickListener {
    private FragmentFavoritesBinding binding;
    private FavoritesPresenter favoritesPresenter;
    private View rootView;
    private int userId;
    private AppAddressDialog dialog;
    private HorizontalRecycleViewAdapter addressessAdapter;
    private FavoritesAdapter favoritesAdapter;
    private List<FavoritesResponse> favoritesList;
    private int productSelectedPosition;
    private int addressSelectedPosition = 0;
    private String editTextName;
    private String editTextAddress;
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
        //getting customer id to fetch addresses and product info
        /*userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);*/
        userId = 45; // TODO have to remove and uncomment above

        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);

        //sets add address view
        binding.addAddressView.homeImageview.setImageResource(R.drawable.ic_add_new);
        binding.addAddressView.homeText.setText(getString(R.string.action_add));
        binding.addAddressView.homeImageview.setOnClickListener(this);

        //top recyclerview
        addressessAdapter = new HorizontalRecycleViewAdapter();
        addressessAdapter.setClickCallback(iAddressClickCallback);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.addressesRecyclerview.setLayoutManager(linearLayoutManager);
        binding.addressesRecyclerview.setAdapter(addressessAdapter);
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.END);
        snapHelper.attachToRecyclerView(binding.addressesRecyclerview);

        //bottom recyclerview
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), gridLayoutManager.getOrientation());
        favoritesAdapter = new FavoritesAdapter(FavoritesFragment.this, favoritesImages);
        // favoritesAdapter.setClickCallback(iClickCallback);
        binding.favoritesRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.favoritesRecyclerview.setAdapter(favoritesAdapter);
        binding.favoritesRecyclerview.setLayoutManager(gridLayoutManager);

        //api call to get addresses
        favoritesPresenter.doGetAddressApi(userId);
    }
    @Override
    public void onClick(View view) {
        showAddressDialog();
    }

    private void showAddressDialog() {
        final String name = SharedPrefsUtils.loginProvider().getStringPreference(
                LoginPrefs.USER_NAME);
        dialog = new AppAddressDialog.AlertDialogBuilder(getActivity(),
                new TextAddressDialogCallback() {
            @Override
            public void enteredTextName(String nameString) {
                editTextName = nameString;

            }
            @Override
            public void enteredTextAddress(String addressString) {
                editTextAddress = addressString;

            }
            @Override
            public void alertDialogCallback(byte dialogStatus) {

                switch (dialogStatus) {
                    case AlertDialogCallback.OK:
                        if ((TextUtils.isEmpty(editTextName)) &&  (TextUtils.isEmpty(
                                editTextAddress))) {
                            showErrorMessage(getString(R.string.error_name_address));
                            return;
                        }
                        break;
                    case AlertDialogCallback.CANCEL:
                        dialog.dismiss();
                        break;

                    default:
                        break;
                }

            }
        }).title(getString(R.string.dialog_verify_title, name)).build();
                dialog.showDialog();
    }


    private IClickCallback iAddressClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            if (addressSelectedPosition != position) {
                addressSelectedPosition = position;
                onRefreshListener.onRefresh();
            }
        }
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    AddUserAddressResponse singleAddressResponse = addressessAdapter.
                            getItemFromPosition(addressSelectedPosition);
                    favoritesPresenter.doFavoritesProductApi(userId, singleAddressResponse.getId());
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        favoritesPresenter.disposeAll();
    }

    @Override
    public void loadAddresses(List<AddUserAddressResponse> favoritesResponseList) {
        if (favoritesResponseList == null) {
            favoritesResponseList = new ArrayList<>();
        }
        addressessAdapter.setData(favoritesResponseList);
        dismissSwipeRefresh();
    }

    @Override
    public void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList) {
        dismissSwipeRefresh();
    }
}
