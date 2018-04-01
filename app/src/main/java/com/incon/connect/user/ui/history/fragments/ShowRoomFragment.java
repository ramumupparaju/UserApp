package com.incon.connect.user.ui.history.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppFeedBackDialog;
import com.incon.connect.user.databinding.FragmentShowroomBinding;
import com.incon.connect.user.ui.history.adapter.InterestAdapter;
import com.incon.connect.user.ui.history.adapter.ShowRoomAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;

import java.util.HashMap;

/**
 * Created by govin on 22-01-2018.
 */

public class ShowRoomFragment extends BaseTabFragment {
    private View rootView;

    private ShowRoomAdapter showRoomAdapter;
    private FragmentShowroomBinding binding;
    private ShimmerFrameLayout shimmerFrameLayout;
    private AppFeedBackDialog feedBackDialog;

    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {

    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    public void setTitle() {

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
            initViews();
            loadBottomSheet();
        }
        setTitle();
        return rootView;
    }

    private void initViews() {
        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);
        showRoomAdapter = new ShowRoomAdapter();
        showRoomAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.showroomRecyclerview.setAdapter(showRoomAdapter);
        binding.showroomRecyclerview.setLayoutManager(linearLayoutManager);


    }

    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            showRoomAdapter.clearSelection();
            ProductInfoResponse interestHistoryResponse =
                    showRoomAdapter.
                            getItemFromPosition(position);
            showRoomAdapter.notifyDataSetChanged();
            interestHistoryResponse.setSelected(true);
            productSelectedPosition = position;
            createBottomSheetFirstRow();
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetFirstRow() {
        int length;
        int[] drawablesArray;
        String[] textArray;
        int[] tagsArray;
        length = 4;
        tagsArray = new int[length];
        tagsArray[0] = R.id.CALL;
        tagsArray[1] = R.id.LOCATION;
        tagsArray[2] = R.id.TIMINGS;
        tagsArray[3] = R.id.FEEDBACK;

        textArray = new String[length];
        textArray[0] = getString(R.string.bottom_option_Call);
        textArray[1] = getString(R.string.bottom_option_location);
        textArray[2] = getString(R.string.bottom_option_timings);
        textArray[3] = getString(R.string.bottom_option_feedback);

        drawablesArray = new int[length];
        drawablesArray[0] = R.drawable.ic_option_product;
        drawablesArray[1] = R.drawable.ic_showroom;
        drawablesArray[2] = R.drawable.ic_option_delete;
        drawablesArray[3] = R.drawable.ic_option_delete;
        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(length);
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, textArray, drawablesArray, tagsArray, bottomSheetFirstRowClickListener);

    }

    private View.OnClickListener bottomSheetFirstRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Integer tag = (Integer) view.getTag();
            String[] textArray = new String[0];
            int[] drawablesArray = new int[0];
            int[] tagsArray = new int[0];

            changeSelectedViews(bottomSheetPurchasedBinding.firstRow, tag);
            if (tag == R.id.CALL) {

            } else if (tag == R.id.LOCATION) {

            } else if (tag == R.id.TIMINGS) {

            }else if (tag == R.id.FEEDBACK) {
                showFeedBackDialog();
                return;

            }

            bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(textArray.length);
            //setBottomViewOptions(bottomSheetPurchasedBinding.secondRow, textArray, drawablesArray, tagsArray, bottomSheetSecondRowClickListener);

        }
    };

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


    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    showRoomAdapter.clearData();
                }
            };
}
