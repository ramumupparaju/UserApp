package com.incon.connect.user.ui.history.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.history.purchased.PurchasedHistoryResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppAlertDialogMap;
import com.incon.connect.user.databinding.BottomSheetPurchasedBinding;
import com.incon.connect.user.databinding.CustomBottomViewBinding;
import com.incon.connect.user.databinding.FragmentPurchasedBinding;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.history.adapter.PurchasedAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created on 13 Jun 2017 4:01 PM.
 */
public class PurchasedFragment extends BaseTabFragment implements PurchasedContract.View {

    private View rootView;
    private PurchasedPresenter purchasedPresenter;
    private FragmentPurchasedBinding binding;
    private PurchasedAdapter purchasedAdapter;
    private int userId;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetPurchasedBinding bottomSheetPurchasedBinding;
    private int productSelectedPosition;
    private AppAlertDialog detailsDialog;
    private AppAlertDialogMap mapDialog;


    @Override
    protected void initializePresenter() {
        purchasedPresenter = new PurchasedPresenter();
        purchasedPresenter.setView(this);
        setBasePresenter(purchasedPresenter);
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
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchased,
                    container, false);

            loadBottomSheet();
            initViews();
            rootView = binding.getRoot();
        }
        setTitle();
        return rootView;
    }

    private void loadBottomSheet() {
        bottomSheetPurchasedBinding = DataBindingUtil.inflate(LayoutInflater.from(
                getActivity()), R.layout.bottom_sheet_purchased, null, false);

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(bottomSheetPurchasedBinding.getRoot());
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                purchasedAdapter.clearSelection();
            }
        });
        /*dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);*/
    }

    private void initViews() {

        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);

        purchasedAdapter = new PurchasedAdapter();
        purchasedAdapter.setClickCallback(iClickCallback);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), linearLayoutManager.getOrientation());
        binding.purchasedRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.purchasedRecyclerview.setAdapter(purchasedAdapter);
        binding.purchasedRecyclerview.setLayoutManager(linearLayoutManager);


        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        purchasedPresenter.purchased(userId);
    }

    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            purchasedAdapter.clearSelection();
            PurchasedHistoryResponse purchasedHistoryResponse =
                    purchasedAdapter.
                    getItemFromPosition(position);
            purchasedHistoryResponse.setSelected(true);
            purchasedAdapter.notifyDataSetChanged();
            createBottomSheetView(position);
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetView(int position) {
        productSelectedPosition = position;
        bottomSheetPurchasedBinding.topRow.setVisibility(View.GONE);
        // bottomSheetPurchasedBinding.bottomRow.removeAllViews();

        String[] bottomNames = new String[4];
        bottomNames[0] = getString(R.string.bottom_option_service);
        bottomNames[1] = getString(R.string.bottom_option_product);
        bottomNames[2] = getString(R.string.bottom_option_showroom);
        bottomNames[3] = getString(R.string.bottom_option_add_as_favorite);

        int[] bottomDrawables = new int[4];
        bottomDrawables[0] = R.drawable.ic_option_service_support;
        bottomDrawables[1] = R.drawable.ic_option_product;
        bottomDrawables[2] = R.drawable.ic_option_customer;
        bottomDrawables[3] = R.drawable.ic_option_delivery_status;

        bottomSheetPurchasedBinding.bottomRow.removeAllViews();
        int length = bottomNames.length;
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, length);
        params.setMargins(1, 1, 1, 1);
//TODO have to remove hard codeings
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
            bottomSheetPurchasedBinding.bottomRow.addView(linearLayout, params);
        }
    }

    private View.OnClickListener bottomViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            String[] bottomOptions;
            int[] topDrawables;
            if (tag == 0) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_call_customer_care);
                bottomOptions[1] = getString(R.string.bottom_option_find_service_center);
                bottomOptions[1] = getString(R.string.bottom_option_service_request);

                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_location;
                topDrawables[1] = R.drawable.ic_option_location;

            } else if (tag == 1) {
                bottomOptions = new String[2];
                bottomOptions[0] = getString(R.string.bottom_option_details);
                bottomOptions[1] = getString(R.string.bottom_option_warranty);

                topDrawables = new int[2];
                topDrawables[0] = R.drawable.ic_option_details;
                topDrawables[1] = R.drawable.ic_option_warranty;
            } else if (tag == 2) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_Call);
                bottomOptions[1] = getString(R.string.bottom_option_location);
                bottomOptions[2] = getString(R.string.bottom_option_feedback);
                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_location;
                topDrawables[2] = R.drawable.ic_option_details;
                /*bottomOptions = new String[3];
                topDrawables = new int[3];
                bottomOptions[0] = "Call";
                bottomOptions[1] = "Request Installation";
                bottomOptions[2] = "Share Customer Location";


                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_accept_request;
                topDrawables[2] = R.drawable.ic_option_location;*/
            } else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                /*bottomOptions = new String[4];
                topDrawables = new int[4];
                bottomOptions[0] = "Dispatches On";
                bottomOptions[1] = "Dispatched";
                bottomOptions[2] = "Delivered";
                bottomOptions[3] = "Installed";


                topDrawables[0] = R.drawable.ic_option_delivery_status;
                topDrawables[1] = R.drawable.ic_option_delivery_status;
                topDrawables[2] = R.drawable.ic_option_delivery_status;
                topDrawables[3] = R.drawable.ic_option_delivery_status;*/
            }
            bottomSheetPurchasedBinding.topRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetPurchasedBinding.topRow.setVisibility(View.VISIBLE);
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
                bottomSheetPurchasedBinding.topRow.addView(linearLayout, params);
            }
        }
    };

    private View.OnClickListener topViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            showErrorMessage(topClickedText);
            Integer tag = (Integer) view.getTag();
            PurchasedHistoryResponse itemFromPosition = purchasedAdapter.getItemFromPosition(
                    productSelectedPosition);
            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_call_customer_care))) {
                callPhoneNumber(itemFromPosition.getMobileNumber());
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_find_service_center))) {
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_service_request))) {
            }

            else if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_Call))) {
                AppUtils.shortToast(getActivity(), getString(R.string.sample_test));
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_location))) {
                AppUtils.shortToast(getActivity(), getString(R.string.sample_test));
            }   else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_feedback))) {
                AppUtils.shortToast(getActivity(), getString(R.string.sample_test));
            }
        }
    };

    private void onOpenAlert(String messageInfo) {
        detailsDialog = new AppAlertDialog.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
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
                .build();
        detailsDialog.showDialog();
    }

    private void showLocationDialog() {
        PurchasedHistoryResponse itemFromPosition = purchasedAdapter.getItemFromPosition(
                productSelectedPosition);

        if (TextUtils.isEmpty(itemFromPosition.getLocation())) {
            AppUtils.shortToast(getActivity(), getString(R.string.error_location));
            return;
        }
        /*String[] splitLocation = itemFromPosition.getLocation().split(
        AppConstants.COMMA_SEPARATOR);
        LatLng latLng = new LatLng(Double.parseDouble(splitLocation[0]), Double.parseDouble(
                splitLocation[1]));
        mapDialog = new AppAlertDialogMap.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.CANCEL:
                                mapDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .location(latLng, itemFromPosition.getAddress())
                .button2Text(getString(R.string.action_ok))
                .build();
        mapDialog.showDialog();*/

        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        addressIntent.putExtra(IntentConstants.LOCATION_COMMA, itemFromPosition.getLocation());
        addressIntent.putExtra(IntentConstants.ADDRESS_COMMA, itemFromPosition.getAddress());
        startActivity(addressIntent);
    }

    private void callPhoneNumber(String phoneNumber) {
        AppUtils.callPhoneNumber(getActivity(), phoneNumber);
    }

    private CustomBottomViewBinding getCustomBottomView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(getActivity()), R.layout.custom_bottom_view, null, false);
    }


    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    purchasedAdapter.clearData();
                    purchasedPresenter.purchased(userId);
                }
            };


    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void loadPurchasedHistory(List<PurchasedHistoryResponse> purchasedHistoryResponseList) {
        if (purchasedHistoryResponseList == null) {
            purchasedHistoryResponseList = new ArrayList<>();
        }
        if (purchasedHistoryResponseList.size() == 0) {
            binding.purchasedTextview.setVisibility(View.VISIBLE);
            dismissSwipeRefresh();
        }   else {
            purchasedAdapter.setData(purchasedHistoryResponseList);
            dismissSwipeRefresh();
        }
    }

    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {
        AppUtils.hideSoftKeyboard(getActivity(), rootView);
        purchasedAdapter.searchData(searchableText, searchType);
    }
}
