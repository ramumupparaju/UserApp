package com.incon.connect.user.ui.history.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppFeedBackDialog;
import com.incon.connect.user.databinding.FragmentShowroomBinding;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.history.adapter.ShowRoomAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 1/23/2018.
 */

public class ShowRoomFragment extends BaseTabFragment implements ShowRoomContract.View {

    private FragmentShowroomBinding binding;
    private View rootView;
    private AppFeedBackDialog feedBackDialog;
    private String feedBackComment;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ShowRoomAdapter showRoomAdapter;
    private ShowRoomPresenter showRoomPresenter;
    private int userId;

    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {

    }

    @Override
    protected void initializePresenter() {
        showRoomPresenter = new ShowRoomPresenter();
        showRoomPresenter.setView(this);
        setBasePresenter(showRoomPresenter);

    }

    @Override
    public void setTitle() {
        //do nothing
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            // handle events from here using android binding
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_showroom,
                    container, false);
            rootView = binding.getRoot();
            shimmerFrameLayout = rootView.findViewById(R.id
                    .effect_shimmer);
            loadBottomSheet();
            initViews();
        }
        setTitle();
        return rootView;
    }

    // load bottom sheet
    @Override
    public void loadBottomSheet() {
        super.loadBottomSheet();
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showRoomAdapter.clearSelection();
            }
        });
    }
    private void initViews() {
        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);
        showRoomAdapter = new ShowRoomAdapter();
        showRoomAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.showroomRecyclerview.setAdapter(showRoomAdapter);
        binding.showroomRecyclerview.setLayoutManager(linearLayoutManager);
        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        getProductsApi();

    }

    private void getProductsApi() {

        binding.showroomRecyclerview.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        showRoomPresenter.storesList(userId);


    }

    // data re load
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    showRoomAdapter.clearData();
                    getProductsApi();

                }
            };


    //recyclerview click event
    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            showRoomAdapter.clearSelection();
            ProductInfoResponse showRoomResponse =
                    showRoomAdapter.
                            getItemFromPosition(position);
            showRoomAdapter.notifyDataSetChanged();
            showRoomResponse.setSelected(true);
            productSelectedPosition = position;
            createBottomSheetFirstRow();
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetFirstRow() {
        ArrayList<Integer> drawablesArray = new ArrayList<>();
        ArrayList<String> textArray = new ArrayList<>();
        ArrayList<Integer> tagsArray = new ArrayList<>();

        tagsArray.add(R.id.CALL);
        tagsArray.add(R.id.LOCATION);
        tagsArray.add(R.id.TIMINGS);
        tagsArray.add(R.id.FEEDBACK);

        textArray.add(getString(R.string.bottom_option_Call));
        textArray.add(getString(R.string.bottom_option_location));
        textArray.add(getString(R.string.bottom_option_timings));
        textArray.add(getString(R.string.bottom_option_feedback));

        drawablesArray.add(R.drawable.ic_option_product);
        drawablesArray.add(R.drawable.ic_showroom);
        drawablesArray.add(R.drawable.ic_option_delete);
        drawablesArray.add(R.drawable.ic_option_delete);


        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(tagsArray.size());
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, textArray, drawablesArray,tagsArray, bottomSheetFirstRowClickListener);

    }


    private View.OnClickListener bottomSheetFirstRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Integer tag = (Integer) view.getTag();

            changeSelectedViews(bottomSheetPurchasedBinding.firstRow, tag);
            ProductInfoResponse itemFromPosition = showRoomAdapter.getItemFromPosition(
                    productSelectedPosition);

            if (tag == R.id.CALL) {
                callPhoneNumber(itemFromPosition.getStoreContactNumber());
                return;
            } else if (tag == R.id.LOCATION) {
                showLocationDialog();
            }
            else if (tag == R.id.TIMINGS) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
            }
            else if (tag == R.id.FEEDBACK) {
                showFeedBackDialog();
                return;
            }

            bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(0);

        }
    };

    private void showLocationDialog() {
        ProductInfoResponse itemFromPosition = showRoomAdapter.getItemFromPosition(
                productSelectedPosition);

        if (TextUtils.isEmpty(itemFromPosition.getLocation())) {
            AppUtils.shortToast(getActivity(), getString(R.string.error_location));
            return;
        }

        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        addressIntent.putExtra(IntentConstants.LOCATION_COMMA, itemFromPosition.getLocation());
        addressIntent.putExtra(IntentConstants.ADDRESS_COMMA, itemFromPosition.getAddress());
        startActivity(addressIntent);

    }

    private void showFeedBackDialog() {

        feedBackDialog = new AppFeedBackDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String commentString) {
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                break;
                            case AlertDialogCallback.CANCEL:
                                feedBackDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(
                R.string.action_feedback))
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        feedBackDialog.showDialog();

    }

    @Override
    public void loadStores(List<ProductInfoResponse> showRoomResponseList) {


        if (showRoomResponseList == null) {
            showRoomResponseList = new ArrayList<>();
        }
        if (showRoomResponseList.size() == 0) {
            binding.showroomTextview.setVisibility(View.VISIBLE);
            dismissSwipeRefresh();
        } else {
            showRoomAdapter.setData(showRoomResponseList);
            dismissSwipeRefresh();
        }

        binding.showroomRecyclerview.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showRoomPresenter.disposeAll();
    }

}
