package com.incon.connect.user.ui.history.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppAlertVerticalTwoButtonsDialog;
import com.incon.connect.user.custom.view.AppCheckBoxListDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.AppFeedBackDialog;
import com.incon.connect.user.databinding.BottomSheetPurchasedBinding;
import com.incon.connect.user.databinding.CustomBottomViewBinding;
import com.incon.connect.user.databinding.CustomBottomViewProductBinding;
import com.incon.connect.user.databinding.FragmentPurchasedBinding;
import com.incon.connect.user.dto.dialog.CheckedModelSpinner;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.billformat.BillFormatActivity;
import com.incon.connect.user.ui.history.adapter.PurchasedAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.incon.connect.user.AppConstants.ApiRequestKeyConstants.BODY_ADDRESS_ID;
import static com.incon.connect.user.AppConstants.ApiRequestKeyConstants.BODY_WARRANTY_ID;


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
    private int productSelectedPosition = -1;
    private AppAlertDialog detailsDialog;
    private AppCheckBoxListDialog productLocationDialog;
    private List<AddUserAddressResponse> productLocationList;
    private Integer addressId;
    private AppEditTextDialog buyRequestDialog;
    private AppFeedBackDialog buyFeedBackRequestDialog;
    private String buyRequestComment;
    private boolean isFromFavorites = false;
    private AppAlertVerticalTwoButtonsDialog dialogDelete;


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

    // load bottom sheet
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


        Bundle bundle = getArguments();
        if (bundle != null) {
            isFromFavorites = bundle.getBoolean(BundleConstants.FROM_FAVORITES);
        } else {
            purchasedPresenter.doGetAddressApi(userId);
        }

    }

    //recyclerview click event
    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            purchasedAdapter.clearSelection();
            ProductInfoResponse purchasedHistoryResponse =
                    purchasedAdapter.
                            getItemFromPosition(position);
            purchasedHistoryResponse.setSelected(true);
            purchasedAdapter.notifyDataSetChanged();

            productSelectedPosition = position;

            //checking if it is from favorites screens
            if (isFromFavorites) {
                addressId = getArguments().getInt(BundleConstants.ADDRESS_ID);
                callAddFavoriteApi();
            } else {
                createBottomSheetView(position);
                bottomSheetDialog.show();
            }
        }
    };

    // bottom sheet creation
    private void createBottomSheetView(int position) {
        bottomSheetPurchasedBinding.topRow.setVisibility(View.GONE);
        int length;
        int[] bottomDrawables;
        String[] bottomNames;
        if (purchasedAdapter.
                getItemFromPosition(position).getAddressId() != null) {
            bottomNames = new String[4];
            bottomNames[0] = getString(R.string.bottom_option_service);
            bottomNames[1] = getString(R.string.bottom_option_product);
            bottomNames[2] = getString(R.string.bottom_option_showroom);
            bottomNames[3] = getString(R.string.bottom_option_delete);
//            bottomNames[3] = getString(R.string.bottom_option_add_as_favorite);

            bottomDrawables = new int[4];
            bottomDrawables[0] = R.drawable.ic_option_service_support;
            bottomDrawables[1] = R.drawable.ic_option_product;
            bottomDrawables[2] = R.drawable.ic_option_customer;
            bottomDrawables[3] = R.drawable.ic_option_delete;
            length = bottomNames.length;

//            bottomDrawables[3] = R.drawable.ic_option_favorite;

        } else {
            bottomNames = new String[5];
            bottomNames[0] = getString(R.string.bottom_option_service);
            bottomNames[1] = getString(R.string.bottom_option_product);
            bottomNames[2] = getString(R.string.bottom_option_showroom);
            bottomNames[3] = getString(R.string.bottom_option_delete);
            bottomNames[4] = getString(R.string.bottom_option_add_as_favorite);

            bottomDrawables = new int[5];
            bottomDrawables[0] = R.drawable.ic_option_service_support;
            bottomDrawables[1] = R.drawable.ic_option_product;
            bottomDrawables[2] = R.drawable.ic_option_customer;
            bottomDrawables[3] = R.drawable.ic_option_delete;
            bottomDrawables[4] = R.drawable.ic_option_favorite;
            length = bottomNames.length;

        }

        bottomSheetPurchasedBinding.bottomRow.removeAllViews();
//        int length = bottomNames.length;
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.MATCH_PARENT, length);
//        params.setMargins(1, 1, 1, 1);
        for (int i = 0; i < length; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setWeightSum(1f);
            linearLayout.setGravity(Gravity.CENTER);
            CustomBottomViewBinding customBottomView = getCustomBottomView();
            customBottomView.viewTv.setText(bottomNames[i]);
            customBottomView.viewLogo.setImageResource(bottomDrawables[i]);
            View bottomRootView = customBottomView.getRoot();
            bottomRootView.setTag(i);
            linearLayout.addView(bottomRootView);
            bottomRootView.setOnClickListener(bottomViewClickListener);
            bottomSheetPurchasedBinding.bottomRow.addView(linearLayout, params);
        }
    }

    // bottom sheet click event
    private View.OnClickListener bottomViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            String[] bottomOptions;
            int[] topDrawables;
            changeBackgroundText(tag, view);
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
                bottomOptions = new String[8];
                bottomOptions[0] = getString(R.string.bottom_option_details);
                bottomOptions[1] = getString(R.string.bottom_option_warranty);
                bottomOptions[2] = getString(R.string.bottom_option_bill);
                bottomOptions[3] = getString(R.string.bottom_option_past_history);
                bottomOptions[4] = getString(R.string.bottom_option_share);
                bottomOptions[5] = getString(R.string.bottom_option_transfer);
                bottomOptions[6] = getString(R.string.bottom_option_feedback);
                bottomOptions[7] = getString(R.string.bottom_option_suggestions);

                topDrawables = new int[8];
                topDrawables[0] = R.drawable.ic_option_details;
                topDrawables[1] = R.drawable.ic_option_warranty;
                topDrawables[2] = R.drawable.ic_option_bill;
                topDrawables[3] = R.drawable.ic_option_pasthistory;
                topDrawables[4] = R.drawable.ic_option_share;
                topDrawables[5] = R.drawable.ic_option_transfer;
                topDrawables[6] = R.drawable.ic_option_feedback;
                topDrawables[7] = R.drawable.ic_option_suggestions;
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
            } else if (tag == 3) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                showDeleteDialog();
                changeBackgroundText(tag, view);
            } else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                showFavoriteOptionsDialog();
            }
            bottomSheetPurchasedBinding.secondTopRow.removeAllViews();
            bottomSheetPurchasedBinding.topRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetPurchasedBinding.topRow.setVisibility(View.VISIBLE);
            int length = length1;
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(1, 1, 1, 1);
            for (int i = 0; i < length; i++) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                CustomBottomViewBinding customBottomView = getCustomBottomView();
                customBottomView.viewTv.setText(bottomOptions[i]);
                customBottomView.viewLogo.setImageResource(topDrawables[i]);
                View topRootView = customBottomView.getRoot();
                topRootView.setTag(i);
                linearLayout.addView(topRootView);
                topRootView.setOnClickListener(topViewClickListener);
                bottomSheetPurchasedBinding.topRow.addView(linearLayout, params);
            }
        }
    };

    //  favorite options
    private void showFavoriteOptionsDialog() {
        if (productLocationList == null) {
            //TODO add error message
            return;
        }

        //set previous selected categories as checked
        List<CheckedModelSpinner> filterNamesList = new ArrayList<>();

        for (AddUserAddressResponse addUserAddressResponse : productLocationList) {
            CheckedModelSpinner checkedModelSpinner = new CheckedModelSpinner();
            checkedModelSpinner.setName(addUserAddressResponse.getName());
            filterNamesList.add(checkedModelSpinner);
        }
        productLocationDialog = new AppCheckBoxListDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String selectedLocationName) {
                        for (AddUserAddressResponse addUserAddressResponse : productLocationList) {
                            if (addUserAddressResponse.getName().equals(selectedLocationName)) {
                                addressId = addUserAddressResponse.getId();
                                break;
                            }
                        }

                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                callAddFavoriteApi();
                                break;
                            case AlertDialogCallback.CANCEL:
                                productLocationDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.action_add_as_favorite))
                .spinnerItems(filterNamesList)
                .build();
        productLocationDialog.showDialog();
        productLocationDialog.setRadioType(true);
    }

    private void callAddFavoriteApi() {
        ProductInfoResponse itemFromPosition = purchasedAdapter.
                getItemFromPosition(productSelectedPosition);
        HashMap<String, String> favoritesMap = new HashMap<>();
        favoritesMap.put(ApiRequestKeyConstants.BODY_USER_ID,
                String.valueOf(userId));
        favoritesMap.put(BODY_ADDRESS_ID, String.valueOf(addressId));
        favoritesMap.put(BODY_WARRANTY_ID,
                itemFromPosition.getWarrantyId());
        purchasedPresenter.addToFavotites(favoritesMap);

    }

    // changeing text colore
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
                                        ((TextView) childView3).setTextColor(
                                                ContextCompat.getColor(
                                                        getActivity(), R.color.colorPrimary));
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

    // bottom sheet top view click event
    private View.OnClickListener topViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ProductInfoResponse itemFromPosition = purchasedAdapter.getItemFromPosition(
                    productSelectedPosition);
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            Integer tag = (Integer) view.getTag();
            String[] bottomOptions;
            int[] topDrawables;
            changeBackgroundText(tag, view);
            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_call_customer_care))) {
                callPhoneNumber(itemFromPosition.getMobileNumber());
                bottomOptions = new String[0];
                topDrawables = new int[0];

            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_find_service_center))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];

            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_service_request))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];

            } else if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_details))) {
                bottomOptions = new String[4];
                bottomOptions[0] = getString(R.string.bottom_option_return_policy);
                bottomOptions[1] = getString(R.string.bottom_option_special_instructions);
                bottomOptions[2] = getString(R.string.bottom_option_how_to_use);
                bottomOptions[3] = getString(R.string.bottom_option_description);
                topDrawables = new int[4];
                topDrawables[0] = R.drawable.ic_option_return_policy;
                topDrawables[1] = R.drawable.ic_option_sp_instructions;
                topDrawables[2] = R.drawable.ic_option_howtouse;
                topDrawables[3] = R.drawable.ic_option_details;

            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_warranty))) {
                String purchasedDate = DateUtils.convertMillisToStringFormat(
                        itemFromPosition.getPurchasedDate(), DateFormatterConstants.DD_MM_YYYY);
                String warrantyEndDate = DateUtils.convertMillisToStringFormat(
                        itemFromPosition.getWarrantyEndDate(), DateFormatterConstants.DD_MM_YYYY);
                long noOfDays = DateUtils.convertDifferenceDateIndays(
                        itemFromPosition.getPurchasedDate()
                        , itemFromPosition.getPurchasedDate());
                String warrantyConditions = itemFromPosition.getWarrantyConditions();
                showInformationDialog(getString(
                        R.string.bottom_option_warranty) ,getString(
                        R.string.purchased_warranty_status_now)
                        + noOfDays + " Days Left "
                        + "\n"
                        + getString(
                        R.string.purchased_purchased_date)
                        + purchasedDate
                        + "\n"
                        + getString(
                        R.string.purchased_warranty_covers_date)
                        + warrantyConditions
                        + "\n"
                        + getString(
                        R.string.purchased_warranty_ends_on) + warrantyEndDate);
                bottomOptions = new String[0];
                topDrawables = new int[0];

            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_bill))) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                Intent billFormatIntent = new Intent(getActivity(), BillFormatActivity.class);
                startActivity(billFormatIntent);
            } else if (tag == 3 && topClickedText.equals(getString(
                    R.string.bottom_option_past_history))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 4 && topClickedText.equals(getString(
                    R.string.bottom_option_share))) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                shareProductDetails(itemFromPosition);
            } else if (tag == 5 && topClickedText.equals(getString(
                    R.string.bottom_option_transfer))) {
                showBuyRequestDialog();
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 6 && topClickedText.equals(getString(
                    R.string.bottom_option_feedback))) {
//                showFeedBackDialog();
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 7 && topClickedText.equals(getString(
                    R.string.bottom_option_suggestions))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_Call))) {
                callPhoneNumber(itemFromPosition.getStoreContactNumber());
                bottomOptions = new String[0];
                topDrawables = new int[0];

            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_find_service_center))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_service_request))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_location))) {
                showLocationDialog();
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_feedback))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                bottomOptions = new String[0];
                topDrawables = new int[0];
//                showFeedBackDialog();
            } else if (tag == 3) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                changeBackgroundText(tag, view);
            } else if (tag == 4) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 5) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
            }
            bottomSheetPurchasedBinding.secondTopRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetPurchasedBinding.secondTopRow.setVisibility(View.VISIBLE);
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
                bottomSheetPurchasedBinding.secondTopRow.addView(linearLayout, params);
            }
        }
    };

    private void showFeedBackDialog() {
        buyFeedBackRequestDialog = new AppFeedBackDialog.AlertDialogBuilder(getActivity(), new
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
                              /*  ProductInfoResponse productInfoResponse = interestAdapter.
                                        getItemFromPosition(productSelectedPosition);
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_MERCHANT_ID,
                                        String.valueOf(productInfoResponse.getMerchantId()));
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_QRCODE_ID,
                                        String.valueOf(productInfoResponse.getQrcodeId()));
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_COMMENTS,
                                        buyRequestComment);
                                interestPresenter.buyRequestApi(buyRequestApi);*/
                                break;
                            case AlertDialogCallback.CANCEL:
                                buyFeedBackRequestDialog.dismiss();
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
        buyFeedBackRequestDialog.showDialog();

    }

    private void shareProductDetails(ProductInfoResponse productSelectedPosition) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, productSelectedPosition.getInformation()
                + " Price " + productSelectedPosition.getMrp());
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);

    }

    private void navigateToAddressActivity() {
        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        startActivityForResult(addressIntent, RequestCodes.ADDRESS_LOCATION);
    }

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
                                purchasedPresenter.doTransferProductApi(buyRequestComment,
                                        userId);
                                buyRequestDialog.dismiss();
                                AppUtils.hideSoftKeyboard(getContext(), getView());
                                break;
                            case AlertDialogCallback.CANCEL:
                                buyRequestDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.bottom_option_transfer))
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        buyRequestDialog.showDialog();
    }

    private void showDeleteDialog() {
        final ProductInfoResponse itemFromPosition = purchasedAdapter.
                getItemFromPosition(productSelectedPosition);
        dialogDelete = new AppAlertVerticalTwoButtonsDialog.AlertDialogBuilder(getActivity(), new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                dialogDelete.dismiss();
                                break;
                            case AlertDialogCallback.CANCEL:
//                                onLogoutClick();
                                purchasedPresenter.deleteProduct(Integer.parseInt(
                                        itemFromPosition.getWarrantyId()));
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
        dialogDelete.setButtonBlueUnselectBackground();
    }

    private View.OnClickListener secondtopViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            Integer tag = (Integer) view.getTag();
            changeBackgroundText(tag, view);
            ProductInfoResponse itemFromPosition = purchasedAdapter.getItemFromPosition(
                    productSelectedPosition);
            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_return_policy))) {
                showInformationDialog(getString(
                        R.string.bottom_option_return_policy) , itemFromPosition.getReturnPolicy());
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_special_instructions))) {
                showInformationDialog(getString(
                        R.string.bottom_option_special_instructions) ,
                        itemFromPosition.getSpecialInstruction());
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_how_to_use))) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
//                showInformationDialog(itemFromPosition.getInformation());
            } else if (tag == 3 && topClickedText.equals(getString(
                    R.string.bottom_option_description))) {
                showInformationDialog(getString(
                        R.string.bottom_option_description) ,itemFromPosition.getInformation()
                        + itemFromPosition.getProductSpecification()
                        + itemFromPosition.getColor()
                        + itemFromPosition.getProductDimensions()

                );
            }
       /*  else if (tag == 0 && topClickedText.equals(getString(
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
        }*/
        }

    };

    private void showInformationDialog(String title ,String messageInfo) {
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

    private void showLocationDialog() {
        ProductInfoResponse itemFromPosition = purchasedAdapter.getItemFromPosition(
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

    private CustomBottomViewBinding getCustomBottomView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(getActivity()), R.layout.custom_bottom_view, null, false);
    }

    private CustomBottomViewProductBinding getCustomBottomProductView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(getActivity()), R.layout.custom_bottom_view_product, null,
                false);
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
    public void loadPurchasedHistory(List<ProductInfoResponse> productInfoResponses) {
        if (productInfoResponses == null) {
            productInfoResponses = new ArrayList<>();
        }

        if (isFromFavorites) {
            for (Iterator<ProductInfoResponse> iter = productInfoResponses.listIterator(); iter.hasNext(); ) {
                ProductInfoResponse singleProductData = iter.next();
                if (singleProductData.getAddressId() != null) {
                    iter.remove();
                }
            }
        }

        if (productInfoResponses.size() == 0) {
            binding.purchasedTextview.setVisibility(View.VISIBLE);
            dismissSwipeRefresh();
        } else {
            purchasedAdapter.setData(productInfoResponses);
            dismissSwipeRefresh();
        }

        /*purchasedAdapter.setData(purchasedHistoryResponseList);
        dismissSwipeRefresh();*/
    }

    @Override
    public void loadAddresses(List<AddUserAddressResponse> productLocationList) {
        this.productLocationList = productLocationList;
    }

    // add product to favorites list
    @Override
    public void addedToFavorite() {
        if (productLocationDialog != null && productLocationDialog.isShowing()) {
            productLocationDialog.dismiss();
        }

        if (isFromFavorites) {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment != null) {
                targetFragment.onActivityResult(getTargetRequestCode(),
                        Activity.RESULT_OK, null);
            }
            getActivity().onBackPressed();
        }
    }

    @Override
    public void transferMobileNumber(Object o) {
    }

    @Override
    public void deleteProduct(Object response) {
            onRefreshListener.onRefresh();
            AppUtils.showSnackBar(getView(),getString(R.string.action_delete));
    }

    // product search
    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {
        AppUtils.hideSoftKeyboard(getActivity(), rootView);
        purchasedAdapter.searchData(searchableText, searchType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        purchasedPresenter.disposeAll();
    }
}
