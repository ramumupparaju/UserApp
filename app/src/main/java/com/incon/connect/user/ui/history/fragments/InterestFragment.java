package com.incon.connect.user.ui.history.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.history.purchased.InterestHistoryResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.databinding.BottomSheetInterestBinding;
import com.incon.connect.user.databinding.CustomBottomViewBinding;
import com.incon.connect.user.databinding.FragmentInterestBinding;
import com.incon.connect.user.ui.history.adapter.InterestAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public class InterestFragment extends BaseTabFragment implements InterestContract.View {
    private FragmentInterestBinding binding;
    private View rootView;
    private InterestPresenter interestPresenter;
    private List<InterestHistoryResponse> interestList;
    private InterestAdapter interestAdapter;
    private BottomSheetInterestBinding bottomSheetInterestBinding;

    private BottomSheetDialog bottomSheetDialog;
    private AppAlertDialog detailsDialog;
    private int userId;
    private int productSelectedPosition = -1;

    @Override
    protected void initializePresenter() {
        interestPresenter = new InterestPresenter();
        interestPresenter.setView(this);
        setBasePresenter(interestPresenter);
    }

    @Override
    public void setTitle() {
        //do nothing
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        if (rootView == null) {
            // handle events from here using android binding
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_interest,
                    container, false);
            initViews();
            loadBottomSheet();

            rootView = binding.getRoot();
        }
        setTitle();
        return rootView;
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        if (bottomSheetDialog.isShowing()) {
            AppUtils.shortToast(getActivity(), errorMessage);
        } else {
            super.showErrorMessage(errorMessage);
        }
    }

    private void loadBottomSheet() {
        bottomSheetInterestBinding = DataBindingUtil.inflate(LayoutInflater.from(
                getActivity()), R.layout.bottom_sheet_interest, null, false);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(bottomSheetInterestBinding.getRoot());
    }

    private void initViews() {
        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);

        interestList = new ArrayList<>();
        interestAdapter = new InterestAdapter();
        interestAdapter.setData(interestList);
        interestAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), linearLayoutManager.getOrientation());
        binding.interestRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.interestRecyclerview.setAdapter(interestAdapter);
        binding.interestRecyclerview.setLayoutManager(linearLayoutManager);

        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        interestPresenter.interestApi(userId);
    }

    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            createBottomSheetView(position);
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetView(int position) {
        productSelectedPosition = position;
        bottomSheetInterestBinding.topRow.setVisibility(View.GONE);

        String[] bottomNames = new String[4];
        bottomNames[0] = getString(R.string.bottom_option_buy_request);
        bottomNames[1] = getString(R.string.bottom_option_product);
        bottomNames[2] = getString(R.string.bottom_option_showroom);
        bottomNames[3] = getString(R.string.bottom_option_delete);

        int[] bottomDrawables = new int[4];
        bottomDrawables[0] = R.drawable.ic_option_customer;
        bottomDrawables[1] = R.drawable.ic_option_product;
        bottomDrawables[2] = R.drawable.ic_showroom;
        bottomDrawables[3] = R.drawable.ic_option_delete;

        bottomSheetInterestBinding.bottomRow.removeAllViews();
        int length = bottomNames.length;
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, length);
        params.setMargins(1, 1, 1, 1);
        for (int i = 0; i < length; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setWeightSum(1f);
            linearLayout.setGravity(Gravity.CENTER);
            CustomBottomViewBinding customBottomView = getCustomBottomView();
            customBottomView.viewTv.setText(bottomNames[i]);
            customBottomView.viewTv.setTextSize(10f);
            customBottomView.viewLogo.setImageResource(bottomDrawables[i]);
            View bottomRootView = customBottomView.getRoot();
            bottomRootView.setTag(i);
            linearLayout.addView(bottomRootView);
            bottomRootView.setOnClickListener(bottomViewClickListener);
            bottomSheetInterestBinding.bottomRow.addView(linearLayout, params);
        }
    }


    private View.OnClickListener bottomViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            String[] bottomOptions;
            int[] topDrawables;
            if (tag == 0) {
                bottomOptions = new String[1];
                bottomOptions[0] = getString(R.string.bottom_option_note);
                topDrawables = new int[1];
                topDrawables[0] = R.drawable.ic_option_details;
                changeBackgroundText(tag, view);


            } else if (tag == 1) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_main_features);
                bottomOptions[1] = getString(R.string.bottom_option_details);
                bottomOptions[2] = getString(R.string.bottom_option_feedback);

                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_option_details;
                topDrawables[1] = R.drawable.ic_option_details;
                topDrawables[2] = R.drawable.ic_option_feedback;
                changeBackgroundText(tag, view);

            } else if (tag == 2) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_Call);
                bottomOptions[1] = getString(R.string.bottom_option_location);
                bottomOptions[2] = getString(R.string.bottom_option_feedback);
                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_location;
                topDrawables[2] = R.drawable.ic_option_feedback;
                changeBackgroundText(tag, view);

            } else {
                bottomOptions = new String[0];
              //  bottomOptions[0] = getString(R.string.bottom_option_Call);
                topDrawables = new int[0];
               // topDrawables[0] = R.drawable.ic_option_call;
                changeBackgroundText(tag, view);
                showInterestProductDeleteDialog(getString(R.string.dilog_delete));

            }
            bottomSheetInterestBinding.secondtopRow.removeAllViews();
            bottomSheetInterestBinding.topRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetInterestBinding.topRow.setVisibility(View.VISIBLE);
            int length = length1;
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.MATCH_PARENT, length);
            params.setMargins(1, 1, 1, 1);
            for (int i = 0; i < length; i++) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setWeightSum(1f);
                linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                CustomBottomViewBinding customBottomView = getCustomBottomView();
                customBottomView.viewTv.setText(bottomOptions[i]);
                customBottomView.viewTv.setTextSize(10f);
                customBottomView.viewLogo.setImageResource(topDrawables[i]);
                View topRootView = customBottomView.getRoot();
                topRootView.setTag(i);
                topRootView.setOnClickListener(topViewClickListener);
                linearLayout.addView(topRootView);
                bottomSheetInterestBinding.topRow.addView(linearLayout, params);
            }
        }
    };

    private View.OnClickListener topViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            String[] bottomOptions;
            int[] topDrawables;
            if (tag == 0) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                changeBackgroundText(tag, view);
                AppUtils.shortToast(getActivity(), getString(R.string.sample_test));
            }
            else if (tag == 1) {
                bottomOptions = new String[5];
                bottomOptions[0] = getString(R.string.bottom_option_return_policy);
                bottomOptions[1] = getString(R.string.bottom_option_special_instructions);
                bottomOptions[2] = getString(R.string.bottom_option_how_to_use);
                bottomOptions[3] = getString(R.string.bottom_option_warranty);
                bottomOptions[4] = getString(R.string.bottom_option_share);
                topDrawables = new int[5];
                topDrawables[0] = R.drawable.ic_option_return_policy;
                topDrawables[1] = R.drawable.ic_option_sp_instructions;
                topDrawables[2] = R.drawable.ic_option_howtouse;
                topDrawables[3] = R.drawable.ic_option_warranty;
                topDrawables[4] = R.drawable.ic_option_share;
                changeBackgroundText(tag, view);

            }
            else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                changeBackgroundText(tag, view);
            }

            bottomSheetInterestBinding.secondtopRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetInterestBinding.secondtopRow.setVisibility(View.VISIBLE);
            int length = length1;
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, length);
            params.setMargins(1, 1, 1, 1);
            for (int i = 0; i < length; i++) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setWeightSum(1f);
                linearLayout.setGravity(Gravity.CENTER);
                CustomBottomViewBinding customBottomView = getCustomBottomView();
                customBottomView.viewTv.setText(bottomOptions[i]);
                customBottomView.viewTv.setTextSize(10f);
                customBottomView.viewLogo.setImageResource(topDrawables[i]);
                View bottomRootView = customBottomView.getRoot();
                bottomRootView.setTag(i);
                linearLayout.addView(bottomRootView);
                bottomRootView.setOnClickListener(secondtopViewClickListener);
                bottomSheetInterestBinding.secondtopRow.addView(linearLayout, params);
            }
        }
    };

    private View.OnClickListener secondtopViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            Integer tag = (Integer) view.getTag();
            InterestHistoryResponse itemFromPosition = interestAdapter.getItemFromPosition(
                    productSelectedPosition);
            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_call_customer_care))) {
            }
            else if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_return_policy))) {
            }   else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_special_instructions))) {
            }   else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_how_to_use))) {
            }  else if (tag == 3 && topClickedText.equals(getString(
                    R.string.bottom_option_warranty))) {
            }  else if (tag == 4 && topClickedText.equals(getString(
                    R.string.bottom_option_share))) {
            }
            else  if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_feedback))) {
            }
        }
    };

    private void showInterestProductDeleteDialog(String messageInfo) {
        detailsDialog = new AppAlertDialog.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                interestPresenter.deleteApi(interestAdapter.
                                        getInterestDateFromPosition(
                                                productSelectedPosition).getInterestId());
                                detailsDialog.dismiss();
                                break;
                            case AlertDialogCallback.CANCEL:
                                detailsDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(messageInfo)
                .button1Text(getString(R.string.action_ok))
                .button2Text(getString(R.string.action_cancel))
                .build();
        detailsDialog.showDialog();
    }

    private void changeBackgroundText(Integer tag, View view) {
        if (view instanceof LinearLayout) {
            View topRootView = (View) view.getParent();
            View topRootView1 = (View) topRootView.getParent();
//                    here we get count of 4
            for (int j = 0; j < ((ViewGroup) topRootView1).getChildCount(); j++) {
                View childView1 = ((ViewGroup) topRootView1).getChildAt(j);

                if (j == tag) {
                    if (childView1 instanceof LinearLayout) {
                        for (int k = 0; k < ((ViewGroup) childView1).getChildCount(); k++) {
                            View childView2 = ((ViewGroup) childView1).getChildAt(k);
                            if (childView2 instanceof LinearLayout) {
                                for (int l = 0;
                                     l < ((ViewGroup) childView2).getChildCount(); l++) {
                                    View childView3
                                            = ((ViewGroup) childView2).getChildAt(l);
                                    if (childView3 instanceof TextView) {
                                        ((TextView) childView3).setTextColor(getResources()
                                                .getColor(R.color.colorPrimary));
                                    }
                                }
                            }
                        }

                    }
                } else {
                    if (childView1 instanceof LinearLayout) {
                        for (int k = 0; k < ((ViewGroup) childView1).getChildCount(); k++) {
                            View childView2 = ((ViewGroup) childView1).getChildAt(k);
                            if (childView2 instanceof LinearLayout) {
                                for (int l = 0;
                                     l < ((ViewGroup) childView2).getChildCount(); l++) {
                                    View childView3
                                            = ((ViewGroup) childView2).getChildAt(l);
                                    if (childView3 instanceof TextView) {
                                        ((TextView) childView3).setTextColor(getResources()
                                                .getColor(R.color.colorAccent));
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private CustomBottomViewBinding getCustomBottomView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(getActivity()), R.layout.custom_bottom_view, null, false);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    interestAdapter.clearData();
                    interestPresenter.interestApi(userId);
                }
            };

    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void loadInterestHistory(List<InterestHistoryResponse> interestHistoryResponseList) {
        if (interestHistoryResponseList == null) {
            interestHistoryResponseList = new ArrayList<>();
        }
        if (interestHistoryResponseList.size() == 0) {
            binding.interestTextview.setVisibility(View.VISIBLE);
            dismissSwipeRefresh();
        } else {
            interestAdapter.setData(interestHistoryResponseList);
            dismissSwipeRefresh();
        }


    }

    @Override
    public void loadInterestDeleteHistory(Object interestHistoryResponseList) {
        bottomSheetDialog.dismiss();
        interestPresenter.interestApi(userId);
    }


    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {
        AppUtils.hideSoftKeyboard(getActivity(), rootView);
        interestAdapter.searchData(searchableText, searchType);
    }
}
