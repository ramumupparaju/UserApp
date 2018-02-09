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
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppAlertVerticalTwoButtonsDialog;
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
    private AppAlertVerticalTwoButtonsDialog dialogDelete;
    private AppEditTextDialog buyRequestDialog;
    private int userId;
    private String buyRequestComment;
    private AppFeedBackDialog feedBackDialog;
    private String feedBackComment;
    private ShimmerFrameLayout shimmerFrameLayout;

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
            rootView = binding.getRoot();
            shimmerFrameLayout = rootView.findViewById(R.id
                    .effect_shimmer);
            initViews();
            loadBottomSheet();
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
        getProductsApi();
    }

    private void getProductsApi() {
        binding.interestRecyclerview.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
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
        ProductInfoResponse productInfoResponse = interestAdapter.getItemFromPosition(productSelectedPosition);
        if (productInfoResponse.getBuyReqCount() == 0) {
            length = 4;
        } else {
            length = 3;
        }

        tagsArray = new int[length];
        tagsArray[0] = R.id.PRODUCT;
        tagsArray[1] = R.id.SHOWROOM;
        tagsArray[2] = R.id.DELETE;

        textArray = new String[length];
        textArray[0] = getString(R.string.bottom_option_product);
        textArray[1] = getString(R.string.bottom_option_showroom);
        textArray[2] = getString(R.string.bottom_option_delete);

        drawablesArray = new int[length];
        drawablesArray[0] = R.drawable.ic_option_product;
        drawablesArray[1] = R.drawable.ic_showroom;
        drawablesArray[2] = R.drawable.ic_option_delete;

        if (length == 4) {
            tagsArray[3] = R.id.BUY_REQUEST;
            textArray[3] = getString(R.string.bottom_option_buy_request);
            drawablesArray[3] = R.drawable.ic_option_customer;
        }
        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(length);
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, textArray, drawablesArray,tagsArray, bottomSheetFirstRowClickListener);

    }

    // bottom sheet click event
    private View.OnClickListener bottomSheetFirstRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Integer tag = (Integer) view.getTag();
            String[] textArray = new String[0];
            int[] drawablesArray = new int[0];
            int[] tagsArray = new int[0];

            changeSelectedViews(bottomSheetPurchasedBinding.firstRow, tag);
            if (tag == R.id.PRODUCT) {
                int length = 3;
                textArray = new String[length];
                textArray[0] = getString(R.string.bottom_option_main_features);
                textArray[1] = getString(R.string.bottom_option_details);
                textArray[2] = getString(R.string.bottom_option_feedback);

                tagsArray = new int[length];
                tagsArray[0] = R.id.PRODUCT_MAINFEATURES;
                tagsArray[1] = R.id.PRODUCT_DETAILS;
                tagsArray[2] = R.id.PRODUCT_FEEDBACK;

                drawablesArray = new int[length];
                drawablesArray[0] = R.drawable.ic_options_features;
                drawablesArray[1] = R.drawable.ic_option_details;
                drawablesArray[2] = R.drawable.ic_option_feedback;

            } else if (tag == R.id.SHOWROOM) {
                int length = 3;
                textArray = new String[length];
                textArray[0] = getString(R.string.bottom_option_Call);
                textArray[1] = getString(R.string.bottom_option_location);
                textArray[2] = getString(R.string.bottom_option_review);

                tagsArray = new int[length];
                tagsArray[0] = R.id.SHOWROOM_CALL;
                tagsArray[1] = R.id.SHOWROOM_LOCATION;
                tagsArray[2] = R.id.SHOWROOM_REVIEW;

                drawablesArray = new int[length];
                drawablesArray[0] = R.drawable.ic_option_call;
                drawablesArray[1] = R.drawable.ic_option_location;
                drawablesArray[2] = R.drawable.ic_option_feedback;
            } else if (tag == R.id.DELETE) {
                showInterestProductDeleteDialog(getString(R.string.dilog_delete));
                return;
            } else if (tag == R.id.BUY_REQUEST) {
                showBuyRequestDialog();
                return;
            }

            bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(textArray.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.secondRow, textArray, drawablesArray,tagsArray, bottomSheetSecondRowClickListener);

        }
    };

    // bottom sheet top view click event
    private View.OnClickListener bottomSheetSecondRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
           // String[] tagArray = unparsedTag.split(COMMA_SEPARATOR);

            ProductInfoResponse itemFromPosition = interestAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.secondRow, tag);


            String[] textArray = new String[0];
            int[] drawablesArray = new int[0];
            int[] tagsArray = new int[0];



            String[] bottomOptions = new String[0];
            int[] topDrawables = new int[0];





            if (tag == R.id.PRODUCT_MAINFEATURES) {
                showInformationDialog(getString(
                        R.string.bottom_option_main_features), itemFromPosition.getInformation());
                return;
            }
            else if(tag == R.id.PRODUCT_DETAILS) {

                int length = 5;
                textArray = new String[length];
                textArray[0] = getString(R.string.bottom_option_return_policy);
                textArray[1] = getString(R.string.bottom_option_special_instructions);
                textArray[2] = getString(R.string.bottom_option_how_to_use);
                textArray[3] = getString(R.string.bottom_option_warranty);
                textArray[4] = getString(R.string.bottom_option_share);

                tagsArray = new int[length];
                tagsArray[0] = R.id.PRODUCT_DETAILS_RETURN_POLICY;
                tagsArray[1] = R.id.PRODUCT_DETAILS_SPECIAL_INSTUCTIONS;
                tagsArray[2] = R.id.PRODUCT_DETAILS_HOW_TO_USE;
                tagsArray[3] = R.id.PRODUCT_DETAILS_WARRANTY;
                tagsArray[4] = R.id.PRODUCT_DETAILS_SHARE;

                drawablesArray = new int[length];
                drawablesArray[0] = R.drawable.ic_option_return_policy;
                drawablesArray[1] = R.drawable.ic_option_sp_instructions;
                drawablesArray[2] = R.drawable.ic_option_howtouse;
                drawablesArray[3] = R.drawable.ic_option_warranty;
                drawablesArray[4] = R.drawable.ic_option_share;

            }
            else if(tag == R.id.PRODUCT_FEEDBACK) {
                showFeedBackDialog();
                return;


            }
            else if(tag == R.id.SHOWROOM_CALL) {
                callPhoneNumber(itemFromPosition.getStoreContactNumber());
                return;

            }else if(tag == R.id.SHOWROOM_LOCATION) {
                showLocationDialog();
                return;

            }
            else if(tag == R.id.SHOWROOM_REVIEW) {
                //showFeedBackDialog();
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));

            }

            // int firstRowTag = Integer.parseInt(tagArray[0]);
           // int secondRowTag = Integer.parseInt(tagArray[1]);
            // product

           /* if (firstRowTag == 0) {

                if (secondRowTag == 0) { //main features

                    showInformationDialog(getString(
                            R.string.bottom_option_main_features), itemFromPosition.getInformation());
                    return;

                } else if (secondRowTag == 1) { // details

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
                } else if (secondRowTag == 2) { // feed back
                    showFeedBackDialog();
                    return;
                }
            } else if (firstRowTag == 1) { // showroom

                if (secondRowTag == 0) { // call
                    callPhoneNumber(itemFromPosition.getStoreContactNumber());
                    return;
                } else if (secondRowTag == 1) { // location
                    showLocationDialog();
                    return;
                } else if (secondRowTag == 2) { // feed back
                    //showFeedBackDialog();
                    AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                    bottomOptions = new String[0];
                    topDrawables = new int[0];
                }

            } else if (firstRowTag == 2) { //  delete
                showInterestProductDeleteDialog(getString(R.string.dilog_delete));

            }
*/
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.removeAllViews();
            bottomSheetPurchasedBinding.thirdRow.setWeightSum(bottomOptions.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.thirdRow, textArray, drawablesArray, tagsArray, bottomSheetThirdRowClickListener);


        }



    };

    private View.OnClickListener bottomSheetThirdRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
          //  String[] tagArray = unparsedTag.split(COMMA_SEPARATOR);


            ProductInfoResponse itemFromPosition = interestAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.thirdRow, tag);


            String[] textArray = new String[0];
            int[] drawablesArray = new int[0];
            int[] tagsArray = new int[0];
            if(tag == R.id.PRODUCT_DETAILS_RETURN_POLICY) {
                showInformationDialog(getString(R.string.bottom_option_return_policy), itemFromPosition.getReturnPolicy());


            }
            else if (tag == R.id.PRODUCT_DETAILS_SPECIAL_INSTUCTIONS) {
                showInformationDialog(getString(
                        R.string.bottom_option_special_instructions),
                        itemFromPosition.getSpecialInstruction());

            }
            else if (tag == R.id.PRODUCT_DETAILS_HOW_TO_USE) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));


            }
            else if (tag == R.id.PRODUCT_DETAILS_WARRANTY) {
                String warrantyInformation = AppUtils.getWarrantyInformation(itemFromPosition);
                if (TextUtils.isEmpty(warrantyInformation)) {
                    AppUtils.shortToast(getContext(), getString(R.string.error_no_warranty));
                } else {
                    showInformationDialog(getString(R.string.bottom_option_warranty), warrantyInformation);
                }

            }
            else if (tag == R.id.PRODUCT_DETAILS_SHARE) {
                shareProductDetails(itemFromPosition);

            }

        }

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
        dialogDelete = new AppAlertVerticalTwoButtonsDialog.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                dialogDelete.dismiss();
                                /*interestPresenter.deleteApi(interestAdapter.getItemFromPosition(
                                        productSelectedPosition).getInterestId());
                                detailsDialog.dismiss();*/
                                break;
                            case AlertDialogCallback.CANCEL:
                                interestPresenter.deleteApi(interestAdapter.getItemFromPosition(
                                        productSelectedPosition).getInterestId());
                                dialogDelete.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.dialog_delete))
                .button1Text(getString(R.string.action_cancel))
                .button2Text(getString(R.string.action_ok))
                .build();
        dialogDelete.showDialog();
        dialogDelete.setCancelable(true);
        dialogDelete.setButtonBlueUnselectBackground();
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

    // data re load
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    interestAdapter.clearData();
                    getProductsApi();
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
            interestAdapter.setData(interestHistoryResponseList);
            dismissSwipeRefresh();

        }
        binding.interestRecyclerview.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);

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
