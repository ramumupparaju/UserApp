package com.incon.connect.user.ui.favorites;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.incon.connect.user.databinding.FragmentFavoritesBinding;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.addnewmodel.AddNewModelFragment;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.favorites.adapter.HorizontalRecycleViewAdapter;
import com.incon.connect.user.ui.history.base.BaseProductOptionsFragment;
import com.incon.connect.user.ui.history.fragments.PurchasedFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.utils.GravitySnapHelper;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.incon.connect.user.ui.BaseActivity.TRANSACTION_TYPE_REPLACE;

/**
 * Created by PC on 11/4/2017.
 */

public class FavoritesFragment extends BaseProductOptionsFragment implements FavoritesContract.View,
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
            binding.setFavorites(this);
            rootView = binding.getRoot();
            initViews();
        }
        setTitle();
        return rootView;
    }

    // add product
    public void onProductAddClick() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BundleConstants.FROM_FAVORITES, true);
        AddUserAddressResponse singleAddressResponse = addressessAdapter.
                getItemFromPosition(addressSelectedPosition);
        ((HomeActivity) getActivity()).setToolbarTitle(
                singleAddressResponse.getName());
        bundle.putInt(BundleConstants.ADDRESS_ID, singleAddressResponse.getId());
        ((HomeActivity) getActivity()).replaceFragmentAndAddToStackWithTargetFragment(
                PurchasedFragment.class, this, RequestCodes.PRODUCT_ADD_FRAGMENT,
                bundle, 0, 0, TRANSACTION_TYPE_REPLACE);
    }

    public void onParentProductClick() {
        binding.addProduct.setVisibility(View.VISIBLE);
        binding.customProduct.setVisibility(View.VISIBLE);
    }

    public void onAddNewModel() {
        ((HomeActivity) getActivity()).replaceFragmentAndAddToStackWithTargetFragment(
                AddNewModelFragment.class, this, RequestCodes.ADD_NEW_MODEL_FRAGMENT,
                null, 0, 0, TRANSACTION_TYPE_REPLACE);
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), linearLayoutManager.getOrientation());
        binding.addressesRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.addressesRecyclerview.setAdapter(addressessAdapter);
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.END);
        snapHelper.attachToRecyclerView(binding.addressesRecyclerview);

        //bottom recyclerview
        favoritesAdapter = new FavoritesAdapter();
        favoritesAdapter.setClickCallback(iProductClickCallback);

        LinearLayoutManager secondLinearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(
                getContext(), secondLinearLayoutManager.getOrientation());
        binding.favoritesRecyclerview.addItemDecoration(dividerItemDecoration1);
        binding.favoritesRecyclerview.setAdapter(favoritesAdapter);
        binding.favoritesRecyclerview.setLayoutManager(secondLinearLayoutManager);

        //api call to get addresses
        favoritesPresenter.doGetAddressApi(userId);
        loadBottomSheet();
    }

    // load bottom sheet
    @Override
    public void loadBottomSheet() {
        super.loadBottomSheet();
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
        addUserAddress.setAdressType(Favorites.ADDRESS_TYPE_ONE);
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
                                        && ((TextUtils.isEmpty(addUserAddress.getAddress())))) {
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
                case RequestCodes.PRODUCT_ADD_FRAGMENT:
                    //After adding new  product refrehes list
                    onRefreshListener.onRefresh();
                    setTitle();
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
                    favoritesAdapter.getItemFromPosition(position);
            favoritesAdapter.notifyDataSetChanged();
            interestHistoryResponse.setSelected(true);
            createBottomSheetView(position);
            bottomSheetDialog.show();
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
    private void createBottomSheetView(int position) /*{
        productSelectedPosition = position;
        bottomSheetFavouriteBinding.topRow.setVisibility(View.GONE);
        String[] bottomNames = new String[3];
        bottomNames[0] = getString(R.string.bottom_option_service);
        bottomNames[1] = getString(R.string.bottom_option_product);
        bottomNames[2] = getString(R.string.bottom_option_showroom);

        int[] bottomDrawables = new int[3];
        bottomDrawables[0] = R.drawable.ic_option_service_support;
        bottomDrawables[1] = R.drawable.ic_option_product;
        bottomDrawables[2] = R.drawable.ic_option_customer;

        bottomSheetFavouriteBinding.bottomRow.removeAllViews();
        int length = bottomNames.length;
        for (int i = 0; i < length; i++) {

            LinearLayout customBottomView = getCustomBottomView();

            getBottomTextView(customBottomView).setText(bottomNames[i]);
            getBottomImageView(customBottomView).setImageResource(bottomDrawables[i]);

            customBottomView.setTag(i);

            customBottomView.setOnClickListener(bottomViewClickListener);
            bottomSheetFavouriteBinding.bottomRow.addView(customBottomView);
        }
    }*/ {}

    // bottom sheet click event
    private View.OnClickListener bottomViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) /*{
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
            } else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
//                showFavoriteOptionsDialog();
            }
            bottomSheetFavouriteBinding.secondTopRow.removeAllViews();
            bottomSheetFavouriteBinding.topRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetFavouriteBinding.topRow.setVisibility(View.VISIBLE);
            int length = length1;
            for (int i = 0; i < length; i++) {

                LinearLayout customBottomView = getCustomBottomView();

                getBottomTextView(customBottomView).setText(bottomOptions[i]);
                getBottomImageView(customBottomView).setImageResource(topDrawables[i]);

                customBottomView.setTag(i);

                customBottomView.setOnClickListener(topViewClickListener);
                bottomSheetFavouriteBinding.topRow.addView(customBottomView);
            }
        }*/{}
    };

    // bottom sheet top view click event
    private View.OnClickListener topViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) /*{
            ProductInfoResponse itemFromPosition = favoritesAdapter.getItemFromPosition(
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
                        itemFromPosition.getWarrantyEndDate(), System.currentTimeMillis());
                showInformationDialog(getString(
                        R.string.bottom_option_warranty), getString(
                        R.string.purchased_warranty_status_now)
                        + noOfDays + " Days Left "
                        + "\n"
                        + getString(
                        R.string.purchased_purchased_date)
                        + purchasedDate
                        + "\n"
                        + getString(
                        R.string.purchased_warranty_covers_date)
                        + " "
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
                Bundle bundle = new Bundle();
                bundle.putParcelable(BundleConstants.PRODUCT_INFO_RESPONSE, itemFromPosition);
                billFormatIntent.putExtras(bundle);
                startActivity(billFormatIntent);
            } else if (tag == 3 && topClickedText.equals(getString(
                    R.string.bottom_option_past_history))) {
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
                bottomOptions = new String[0];
                topDrawables = new int[0];
            } else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_service_request))) {
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
            bottomSheetFavouriteBinding.secondTopRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetFavouriteBinding.secondTopRow.setVisibility(View.VISIBLE);
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
                bottomSheetFavouriteBinding.secondTopRow.addView(customBottomView);
            }
        }*/{}
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
   /* private View.OnClickListener secondtopViewClickListener = new View.OnClickListener() {
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
                showInformationDialog(itemFromPosition.getReturnPolicy());
            }
            else if (tag == 1 && topClickedText.equals(getString(
                    R.string.bottom_option_special_instructions))) {
                showInformationDialog(itemFromPosition.getSpecialInstruction());
            }   else if (tag == 2 && topClickedText.equals(getString(
                    R.string.bottom_option_how_to_use))) {
//                showInformationDialog(itemFromPosition.getInformation());
            }  else if (tag == 3 && topClickedText.equals(getString(
                    R.string.bottom_option_description))) {
                showInformationDialog(itemFromPosition.getInformation());
            }
       *//*  else if (tag == 0 && topClickedText.equals(getString(
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
        }*//*
        }

    };*/

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
            binding.listHeader.setVisibility(View.GONE);
            binding.noItemsTextview.setVisibility(View.VISIBLE);
        } else {
            binding.listHeader.setVisibility(View.VISIBLE);
            binding.noItemsTextview.setVisibility(View.GONE);
        }
        favoritesAdapter.setData(favoritesResponseList);
        dismissSwipeRefresh();
    }
}
