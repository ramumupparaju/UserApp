package com.incon.connect.user.ui.favorites;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
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
import com.incon.connect.user.callbacks.TextAddressDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.AppUserAddressDialog;
import com.incon.connect.user.databinding.BottomSheetInterestBinding;
import com.incon.connect.user.databinding.CustomBottomViewBinding;
import com.incon.connect.user.databinding.FragmentFavoritesBinding;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.favorites.adapter.HorizontalRecycleViewAdapter;
import com.incon.connect.user.ui.history.adapter.InterestAdapter;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.utils.GravitySnapHelper;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.HashMap;
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

    private HorizontalRecycleViewAdapter addressessAdapter;
    private FavoritesAdapter favoritesAdapter;
    private int addressSelectedPosition = -1;
    private int productSelectedPosition = -1;
    private AppUserAddressDialog dialog;
    private AddUserAddress addUserAddress;
    private BottomSheetInterestBinding bottomSheetInterestBinding;
    private BottomSheetDialog bottomSheetDialog;
    private AppEditTextDialog buyRequestDialog;
    private String buyRequestComment;
    private AppAlertDialog detailsDialog;

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
        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);

        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);

        //sets add address view
        binding.addAddressView.homeImageview.setImageResource(R.drawable.ic_add_new_location);
        binding.addAddressView.homeText.setText(getString(R.string.action_add_location));
        binding.addAddressView.getRoot().setOnClickListener(this);
        binding.addAddressView.getRoot().setVisibility(View.GONE);


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
        favoritesAdapter = new FavoritesAdapter();
        favoritesAdapter.setClickCallback(iProductClickCallback);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.favoritesRecyclerview.setAdapter(favoritesAdapter);
        binding.favoritesRecyclerview.setLayoutManager(gridLayoutManager);

        //api call to get addresses
        favoritesPresenter.doGetAddressApi(userId);
        loadBottomSheet();
    }
    // load bottom sheet
    private void loadBottomSheet() {
        bottomSheetInterestBinding = DataBindingUtil.inflate(LayoutInflater.from(
                getActivity()), R.layout.bottom_sheet_interest, null, false);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(bottomSheetInterestBinding.getRoot());
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                favoritesAdapter.clearSelection();
            }
        });
    }
    @Override
    public void onClick(View view) {
        showAddressDialog();
    }

    // address dialog
    private void showAddressDialog() {
        addUserAddress = new AddUserAddress();
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.loginProvider();
        addUserAddress.setSubscriberId(sharedPrefsUtils.getIntegerPreference(LoginPrefs.USER_ID,
                DEFAULT_VALUE));
        addUserAddress.setAdressType("1"); //TODO have to remove hard coding
        addUserAddress.setContact(sharedPrefsUtils.getStringPreference(LoginPrefs
                .USER_PHONE_NUMBER));
        dialog = new AppUserAddressDialog.AlertDialogBuilder(getActivity(),
                new TextAddressDialogCallback() {
                    @Override
                    public void openAddressActivity() {
                        navigateToAddressActivity();
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {

                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                if ((TextUtils.isEmpty(addUserAddress.getName()))
                                        &&
                                        ((TextUtils.isEmpty(addUserAddress.getAddress())))) {
                                    showErrorMessage(getString(R.string.error_name_address));
                                    return;
                                }
                                favoritesPresenter.doAddAddressApi(addUserAddress);
                                break;
                            case AlertDialogCallback.CANCEL:
                                dialog.dismiss();
                                break;

                            default:
                                break;
                        }

                    }
                }).addUserAddress(addUserAddress).build();
        dialog.showDialog();
    }

    private void navigateToAddressActivity() {
        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        startActivityForResult(addressIntent, RequestCodes.ADDRESS_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.ADDRESS_LOCATION:
                    String stringAddress = data.getStringExtra(IntentConstants.ADDRESS_COMMA);
                    String stringLocation = data.getStringExtra(IntentConstants.LOCATION_COMMA);
                    if (dialog != null) {
                        try {
                            String[] addressData = stringAddress.split(COMMA_SEPARATOR);
                            addUserAddress.setAddress(addressData[1]);
                            addUserAddress.setPincode(Integer.valueOf(addressData[0]));
                            addUserAddress.setState(addressData[2]);
                            addUserAddress.setCountry(addressData[3]);
                        } catch (Exception e) {
                            //DO nothing
                        }
                        addUserAddress.setLocation(stringLocation);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //recyclerview click event
    private IClickCallback iProductClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            favoritesAdapter.clearSelection();
            ProductInfoResponse interestHistoryResponse =
                    favoritesAdapter.
                            getItemFromPosition(position);
            favoritesAdapter.notifyDataSetChanged();
            interestHistoryResponse.setSelected(true);
            createBottomSheetView(position);
            bottomSheetDialog.show();
          /*  if (productSelectedPosition != position) {
                productSelectedPosition = position;
            }*/
        }
    };
    // top recyclerview click event
    private IClickCallback iAddressClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            if (addressSelectedPosition != position) {
                addressSelectedPosition = position;
                addressessAdapter.clearSelection();
                addressessAdapter.getItemFromPosition(position).setSelected(true);
                addressessAdapter.notifyDataSetChanged();
                onRefreshListener.onRefresh();
            }
        }
    };

    // bottom sheet creation
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
    private CustomBottomViewBinding getCustomBottomView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(getActivity()), R.layout.custom_bottom_view, null, false);
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
//                showInterestProductDeleteDialog(getString(R.string.dilog_delete));
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
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            Integer tag = (Integer) view.getTag();
            changeBackgroundText(tag, view);
            String[] bottomOptions;
            int[] topDrawables;
            ProductInfoResponse itemFromPosition = favoritesAdapter.getItemFromPosition(
                    productSelectedPosition);
          /*  if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_note))) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                showBuyRequestDialog();
            }
            else*/

            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_main_features))) {
                showInformationDialog(itemFromPosition.getInformation());
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

    // bottom sheet second top view click event
    private View.OnClickListener secondtopViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            Integer tag = (Integer) view.getTag();
            changeBackgroundText(tag, view);
            ProductInfoResponse itemFromPosition = favoritesAdapter.getItemFromPosition(
                    productSelectedPosition);
            if (tag == 0 && topClickedText.equals(getString(
                    R.string.bottom_option_return_policy))) {
                String returnPolicy = itemFromPosition.getReturnPolicy();
                if (returnPolicy != null) {
                    showInformationDialog(returnPolicy);
                }
            } else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_special_instructions))) {
                String specialInstruction = itemFromPosition.getSpecialInstruction();
                if (specialInstruction != null) {
                    showInformationDialog(specialInstruction);
                }
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_how_to_use))) {
            } else if (tag == 3 && topClickedText.equals(getString(
                    R.string.bottom_option_warranty))) {
                if (itemFromPosition.getWarrantyYears()!=null){
                    showInformationDialog("Warranty: "
                            + itemFromPosition.getWarrantyYears() +"Year");
                } else {
                    showInformationDialog("No Warranty Exists");
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
                +" Price "+productSelectedPosition.getMrp());
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
                                /*interestPresenter.deleteApi(interestAdapter.
                                        getInterestDateFromPosition(
                                                productSelectedPosition).getInterestId());*/
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
    private void showInformationDialog(String messageInfo) {
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
    //location dialog
    private void showLocationDialog() {
        ProductInfoResponse itemFromPosition = favoritesAdapter.getItemFromPosition(
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
                                ProductInfoResponse productInfoResponse = favoritesAdapter.
                                        getItemFromPosition(productSelectedPosition);
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_MERCHANT_ID,
                                        String.valueOf(productInfoResponse.getMerchantId()));
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_QRCODE_ID,
                                        String.valueOf(productInfoResponse.getQrcodeId()));
                                buyRequestApi.put(ApiRequestKeyConstants.BODY_COMMENTS,
                                        buyRequestComment);
//                                interestPresenter.buyRequestApi(buyRequestApi);
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

    // data reload
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (addressSelectedPosition == -1) {
                        return;
                    }
                    AddUserAddressResponse singleAddressResponse = addressessAdapter.
                            getItemFromPosition(addressSelectedPosition);
                    binding.addressesRecyclerview.getLayoutManager().scrollToPosition(
                            addressSelectedPosition);
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        binding.addAddressView.getRoot().setVisibility(View.VISIBLE);
        dismissSwipeRefresh();

        if (favoritesResponseList.size() > 0) {
            iAddressClickCallback.onClickPosition(0);
        } else {
            loadFavoritesProducts(null);
        }
    }

    @Override
    public void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList) {
        if (favoritesResponseList == null) {
            favoritesResponseList = new ArrayList<>();
        }
        if (favoritesResponseList.size() == 0) {
            binding.noItemsTextview.setVisibility(View.VISIBLE);
        } else {
            binding.noItemsTextview.setVisibility(View.GONE);
        }
        favoritesAdapter.setData(favoritesResponseList);
        dismissSwipeRefresh();
    }
}
