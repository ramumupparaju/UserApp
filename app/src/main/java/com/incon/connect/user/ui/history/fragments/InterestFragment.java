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
import android.widget.TextView;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.AppFeedBackDialog;
import com.incon.connect.user.databinding.FragmentInterestBinding;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.history.adapter.InterestAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public class InterestFragment extends BaseTabFragment implements InterestContract.View {
    private FragmentInterestBinding binding;
    private View rootView;
    private InterestPresenter interestPresenter;
    private InterestAdapter interestAdapter;
    private AppAlertDialog detailsDialog;
    private AppEditTextDialog buyRequestDialog;
    private int userId;
    private int productSelectedPosition = -1;
    private String buyRequestComment;
    private AppFeedBackDialog feedBackDialog;
    private String feedBackComment;

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

    // load bottom sheet
    @Override
    public void loadBottomSheet() {
        super.loadBottomSheet();
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                interestAdapter.clearSelection();
            }
        });
    }

    private void initViews() {
        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);
        interestAdapter = new InterestAdapter();
        interestAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.interestRecyclerview.setAdapter(interestAdapter);
        binding.interestRecyclerview.setLayoutManager(linearLayoutManager);
        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        interestPresenter.interestApi(userId);
    }

    //recyclerview click event
    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            interestAdapter.clearSelection();
            ProductInfoResponse interestHistoryResponse =
                    interestAdapter.
                            getItemFromPosition(position);
            interestAdapter.notifyDataSetChanged();
            interestHistoryResponse.setSelected(true);
          //  createBottomSheetView(position);
            createBottomSheetFirstRow(position);
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetFirstRow(int position) {
        int length;
        int[] bottomDrawables;
        String[] bottomNames;
        length = 4;
        bottomNames = new String[length];
        bottomNames[0] = getString(R.string.bottom_option_buy_request);
        bottomNames[1] = getString(R.string.bottom_option_product);
        bottomNames[2] = getString(R.string.bottom_option_showroom);
        bottomNames[3] = getString(R.string.bottom_option_delete);
        bottomDrawables = new int[length];
        bottomDrawables[0] = R.drawable.ic_option_customer;
        bottomDrawables[1] = R.drawable.ic_option_product;
        bottomDrawables[2] = R.drawable.ic_showroom;
        bottomDrawables[3] = R.drawable.ic_option_delete;

        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(length);
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, bottomNames, bottomDrawables, bottomSheetFirstRowClickListener, "-1");

    }

    // bottom sheet click event
    private View.OnClickListener bottomSheetFirstRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String unparsedTag = (String) view.getTag();
            Integer tag = Integer.valueOf(unparsedTag);
            String[] bottomOptions;
            int[] topDrawables;
            changeSelectedViews(bottomSheetPurchasedBinding.firstRow, unparsedTag);
            if (tag == 0) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_call_customer_care);
                bottomOptions[1] = getString(R.string.bottom_option_find_service_center);
                bottomOptions[2] = getString(R.string.bottom_option_service_request);
                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_find_service_center;
                topDrawables[2] = R.drawable.ic_option_service_request;

            } else if (tag == 1) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_main_features);
                bottomOptions[1] = getString(R.string.bottom_option_details);
                bottomOptions[2] = getString(R.string.bottom_option_feedback);
                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_options_features;
                topDrawables[1] = R.drawable.ic_option_details;
                topDrawables[2] = R.drawable.ic_option_feedback;
            } else if (tag == 2) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_Call);
                bottomOptions[1] = getString(R.string.bottom_option_location);
                bottomOptions[2] = getString(R.string.bottom_option_review);
                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_location;
                topDrawables[2] = R.drawable.ic_option_feedback;
            } else  {
                showInterestProductDeleteDialog(getString(R.string.dilog_delete));
                return;
            }

                bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(bottomOptions.length);
        }
    };

    // bottom sheet creation
    private void createBottomSheetView(int position) /*{
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
        for (int i = 0; i < length; i++) {
            LinearLayout customBottomView = getCustomBottomView();

            getBottomTextView(customBottomView).setText(bottomNames[i]);
            getBottomImageView(customBottomView).setImageResource(bottomDrawables[i]);

            customBottomView.setTag(i);

            customBottomView.setOnClickListener(bottomViewClickListener);
            bottomSheetInterestBinding.bottomRow.addView(customBottomView);
        }
    }*/{}

    // bottom sheet click event
    private View.OnClickListener bottomViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) /*{
            Integer tag = (Integer) view.getTag();
            String[] bottomOptions;
            int[] topDrawables;
            changeBackgroundText(tag, view);
            if (tag == 0) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                showBuyRequestDialog();
            } else if (tag == 1) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_main_features);
                bottomOptions[1] = getString(R.string.bottom_option_details);
                bottomOptions[2] = getString(R.string.bottom_option_feedback);
                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_options_features;
                topDrawables[1] = R.drawable.ic_option_details;
                topDrawables[2] = R.drawable.ic_option_feedback;
            } else if (tag == 2) {
                bottomOptions = new String[3];
                bottomOptions[0] = getString(R.string.bottom_option_Call);
                bottomOptions[1] = getString(R.string.bottom_option_location);
                bottomOptions[2] = getString(R.string.bottom_option_review);
                topDrawables = new int[3];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_location;
                topDrawables[2] = R.drawable.ic_option_feedback;
                changeBackgroundText(tag, view);
            } else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                showInterestProductDeleteDialog(getString(R.string.dilog_delete));
            }
            bottomSheetInterestBinding.secondtopRow.removeAllViews();
            bottomSheetInterestBinding.topRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetInterestBinding.topRow.setVisibility(View.VISIBLE);
            int length = length1;
            for (int i = 0; i < length; i++) {
                LinearLayout customBottomView = getCustomBottomView();

                getBottomTextView(customBottomView).setText(bottomOptions[i]);
                getBottomImageView(customBottomView).setImageResource(topDrawables[i]);

                customBottomView.setTag(i);

                customBottomView.setOnClickListener(topViewClickListener);
                bottomSheetInterestBinding.topRow.addView(customBottomView);
            }
        }*/{}
    };

    //buy request dialog
    private void showBuyRequestDialog() {
        buyRequestDialog = new AppEditTextDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String commentString) {
                        buyRequestComment = commentString;
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                HashMap<String, String> buyRequestApi = new HashMap<>();
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_CUSTOMER_ID,
                                        String.valueOf(userId));
                                ProductInfoResponse productInfoResponse = interestAdapter.
                                        getItemFromPosition(productSelectedPosition);
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_MERCHANT_ID,
                                        String.valueOf(productInfoResponse.getMerchantId()));
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_INTEREST_ID,
                                        String.valueOf(productInfoResponse.getInterestId()));
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_QRCODE_ID,
                                        String.valueOf(productInfoResponse.getQrcodeId()));
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_COMMENTS,
                                        buyRequestComment);
                                interestPresenter.buyRequestApi(buyRequestApi);
                                break;
                            case AlertDialogCallback.CANCEL:
                                buyRequestDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.bottom_option_buy_request))
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        buyRequestDialog.showDialog();
    }

    // bottom sheet top view click event
    private View.OnClickListener topViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) /*{
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            Integer tag = (Integer) view.getTag();
            changeBackgroundText(tag, view);
            String[] bottomOptions;
            int[] topDrawables;
            ProductInfoResponse itemFromPosition = interestAdapter.getItemFromPosition(
                    productSelectedPosition);
          *//*  if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_note))) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                showBuyRequestDialog();
            }
            else*//*

            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_main_features))) {
                showInformationDialog(getString(
                        R.string.bottom_option_main_features), itemFromPosition.getInformation());
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_details))) {
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
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_feedback))) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
//                showFeedBackDialog();
            } else if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_Call))) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                callPhoneNumber(itemFromPosition.getMobileNumber());
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_location))) {
                showLocationDialog();
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_review))) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
//                showFeedBackDialog();
            } else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
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

                LinearLayout customBottomView = getCustomBottomView();

                getBottomTextView(customBottomView).setText(bottomOptions[i]);
                getBottomImageView(customBottomView).setImageResource(topDrawables[i]);

                customBottomView.setTag(i);

                customBottomView.setOnClickListener(secondtopViewClickListener);
                bottomSheetInterestBinding.secondtopRow.addView(customBottomView);
            }
        }*/{}
    };

    // feedback dialog
    private void showFeedBackDialog() {

        feedBackDialog = new AppFeedBackDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String commentString) {
                        buyRequestComment = commentString;
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                HashMap<String, String> buyRequestApi = new HashMap<>();
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_CUSTOMER_ID,
                                        String.valueOf(userId));
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

    // bottom sheet second top view click event
    private View.OnClickListener secondtopViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            Integer tag = (Integer) view.getTag();
            changeBackgroundText(tag, view);
            ProductInfoResponse itemFromPosition = interestAdapter.getItemFromPosition(
                    productSelectedPosition);
            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_return_policy))) {
                String returnPolicy = itemFromPosition.getReturnPolicy();
                if (returnPolicy != null) {
                    showInformationDialog(getString(
                            R.string.bottom_option_return_policy), returnPolicy);
                }
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_special_instructions))) {
                String specialInstruction = itemFromPosition.getSpecialInstruction();
                if (specialInstruction != null) {
                    showInformationDialog(getString(
                            R.string.bottom_option_special_instructions), specialInstruction);
                }
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_how_to_use))) {
            } else if (tag == 3 && topClickedText.equals(getString(
                    R.string.bottom_option_warranty))) {
                if (itemFromPosition.getWarrantyYears() != null) {
                    showInformationDialog(getString(
                            R.string.bottom_option_warranty),
                            +itemFromPosition.getWarrantyYears() + "Year");
                } else {
                    showInformationDialog(getString(
                            R.string.bottom_option_warranty), "No Warranty Exists");
                }
            } else if (tag == 4 && topClickedText.equals(getString(
                    R.string.bottom_option_share))) {
                shareProductDetails(itemFromPosition);
            } else if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_feedback))) {
            }
        }
    };

    // share product details
    private void shareProductDetails(ProductInfoResponse productSelectedPosition) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, productSelectedPosition.getInformation()
                + " Price " + productSelectedPosition.getMrp());
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);

    }

    private void showInterestProductDeleteDialog(String messageInfo) {
        detailsDialog = new AppAlertDialog.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                interestPresenter.deleteApi(interestAdapter.getItemFromPosition(
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

    private void showInformationDialog(String title, String messageInfo) {
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
                }).title(title).content(messageInfo)
                .build();
        detailsDialog.showDialog();
        detailsDialog.setCancelable(true);
    }

    //location dialog
    private void showLocationDialog() {
        ProductInfoResponse itemFromPosition = interestAdapter.getItemFromPosition(
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

    private void callPhoneNumber(String phoneNumber) {
        AppUtils.callPhoneNumber(getActivity(), phoneNumber);
    }

    // data re load
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

    // load interest data
    @Override
    public void loadInterestHistory(List<ProductInfoResponse> interestHistoryResponseList) {
        if (interestHistoryResponseList == null) {
            interestHistoryResponseList = new ArrayList<>();
        }
        if (interestHistoryResponseList.size() == 0) {
            binding.interestTextview.setVisibility(View.VISIBLE);
            dismissSwipeRefresh();
        } else {
            List<ProductInfoResponse> interestHistoryResponseLastToFirst = new ArrayList<>();
            for (int i = interestHistoryResponseList.size() - 1;
                 i >= 0; i--) {
                interestHistoryResponseLastToFirst.add(interestHistoryResponseList.get(i));
            }
            interestAdapter.setData(interestHistoryResponseLastToFirst);
            dismissSwipeRefresh();
        }
    }

    // delete interest items
    @Override
    public void loadInterestDeleteHistory(Object interestHistoryResponseList) {
        bottomSheetDialog.dismiss();
        interestPresenter.interestApi(userId);
    }


    @Override
    public void loadBuyRequestResponce(Object buyRequestResponceList) {
        dismissDialog(buyRequestDialog);
    }

    // product search
    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {
        AppUtils.hideSoftKeyboard(getActivity(), rootView);
        interestAdapter.searchData(searchableText, searchType);
    }
}
