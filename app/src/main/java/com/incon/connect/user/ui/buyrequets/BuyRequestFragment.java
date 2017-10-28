package com.incon.connect.user.ui.buyrequets;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.apimodel.components.buyrequest.BuyRequestResponse;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.FragmentBuyRequestBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.buyrequets.adapter.BuyRequestAdapter;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created on 13 Jun 2017 4:01 PM.
 */
public class BuyRequestFragment extends BaseFragment implements View.OnClickListener,
        BuyRequestContract.View {

    private static final String TAG = BuyRequestFragment.class.getSimpleName();
    private FragmentBuyRequestBinding binding;
    private View rootView;
    private BuyRequestAdapter buyRequestAdapter;
    private BuyRequestPresenter buyRequestPresenter;
    private int userId;
    private List<BuyRequestResponse> buyRequestList;

    @Override
    protected void initializePresenter() {
        buyRequestPresenter = new BuyRequestPresenter();
        buyRequestPresenter.setView(this);
        setBasePresenter(buyRequestPresenter);

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
                    inflater, R.layout.fragment_buy_request, container, false);
            rootView = binding.getRoot();
            initViews();
        }
        setTitle();
        return rootView;
    }

    private void initViews() {

        binding.searchLayout.searchIconIv.setOnClickListener(this);
        binding.searchLayout.filterIconIv.setOnClickListener(this);

        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);

        buyRequestList = new ArrayList<>();
        buyRequestAdapter = new BuyRequestAdapter();
        buyRequestAdapter.setData(buyRequestList);
        buyRequestAdapter.setClickCallback(iClickCallback);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), linearLayoutManager.getOrientation());
        binding.buyRequestRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.buyRequestRecyclerview.setAdapter(buyRequestAdapter);
        binding.buyRequestRecyclerview.setLayoutManager(linearLayoutManager);

        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        buyRequestPresenter.buyRequest(userId);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    buyRequestAdapter.clearData();
                    buyRequestPresenter.buyRequest(userId);
                }
            };


    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            AppUtils.showSnackBar(getView(), "Accepted" + position);
        }
    };

    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }


    @Override
    public void loadBuyRequest(List<BuyRequestResponse> buyRequestResponseList) {
        if (buyRequestResponseList == null) {
            buyRequestResponseList = new ArrayList<>();

        }
        this.buyRequestList = buyRequestResponseList;
        buyRequestAdapter.setData(buyRequestList);
        dismissSwipeRefresh();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_icon_iv:
                String searchableText = binding.searchLayout.searchEt.getText().toString();
                int filterType;
                /*if (TextUtils.isEmpty(searchableText)) {
                    filterType = FilterConstants.NONE;
                } else {
                    filterType = FilterConstants.NAME;
                }
                buyRequestAdapter.searchData(searchableText, filterType);
                */
                break;
            case R.id.filter_icon_iv:
                AppUtils.showSnackBar(rootView, "have to show popup");
                break;
            default:
                //do nothing
        }
    }
}
