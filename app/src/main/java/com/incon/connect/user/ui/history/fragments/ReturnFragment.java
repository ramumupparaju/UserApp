package com.incon.connect.user.ui.history.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.databinding.FragmentReturnBinding;
import com.incon.connect.user.ui.history.adapter.ReturnAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public class ReturnFragment extends BaseTabFragment implements ReturnContract.View {
    private View rootView;
    private FragmentReturnBinding binding;
    private ReturnPresenter returnPresenter;
    private ReturnAdapter returnAdapter;
    private int userId;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void initializePresenter() {
        returnPresenter = new ReturnPresenter();
        returnPresenter.setView(this);
        setBasePresenter(returnPresenter);
    }

    @Override
    public void setTitle() {
        //do nothing
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_return,
                    container, false);
            rootView = binding.getRoot();
            shimmerFrameLayout = rootView.findViewById(R.id
                    .effect_shimmer);
            initViews();
        }
        setTitle();
        return rootView;
    }

    private void initViews() {

        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);
        returnAdapter = new ReturnAdapter();
        returnAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.returnRecyclerview.setAdapter(returnAdapter);
        binding.returnRecyclerview.setLayoutManager(linearLayoutManager);
        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        getProductsApi();
    }

    private void getProductsApi() {

        binding.returnRecyclerview.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        returnPresenter.returnHistory(userId);


    }

    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }

    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            returnAdapter.clearSelection();
            ProductInfoResponse returnHistoryResponse = returnAdapter.
                    getItemFromPosition(position);
            returnHistoryResponse.setSelected(true);
            returnAdapter.notifyDataSetChanged();
        }
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    returnAdapter.clearData();
                    getProductsApi();
                }
            };

    @Override
    public void loadReturnHistory(List<ProductInfoResponse> returnHistoryResponseList) {
        if (returnHistoryResponseList == null) {
            returnHistoryResponseList = new ArrayList<>();
        }
        if (returnHistoryResponseList.size() == 0) {
            binding.returnTextview.setVisibility(View.VISIBLE);
            dismissSwipeRefresh();
        } else {
            returnAdapter.setData(returnHistoryResponseList);
            dismissSwipeRefresh();
        }
        binding.returnRecyclerview.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);

    }

    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {
        AppUtils.hideSoftKeyboard(getActivity(), rootView);
        returnAdapter.searchData(searchableText, searchType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        returnPresenter.disposeAll();
    }
}
