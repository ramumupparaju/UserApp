package com.incon.connect.user.ui.favorites;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Build;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.CustomPhoneNumberAlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.ServiceRequestCallback;
import com.incon.connect.user.callbacks.TextAddressDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.callbacks.TimeSlotAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.AppUserAddressDialog;
import com.incon.connect.user.custom.view.CustomPhoneNumberDialog;
import com.incon.connect.user.custom.view.ServiceRequestDialog;
import com.incon.connect.user.custom.view.TimeSlotAlertDialog;
import com.incon.connect.user.databinding.FragmentFavoritesBinding;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.addnewmodel.AddCustomProductFragment;
import com.incon.connect.user.ui.billformat.BillFormatActivity;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.favorites.adapter.HorizontalRecycleViewAdapter;
import com.incon.connect.user.ui.history.base.BaseProductOptionsFragment;
import com.incon.connect.user.ui.history.fragments.PurchasedFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.servicecenters.ServiceCentersActivity;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.GravitySnapHelper;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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
    private AppEditTextDialog feedBackDialog;
    private String buyRequestComment;
    private AppAlertDialog detailsDialog;
    private ShimmerFrameLayout shimmerFrameLayout;
    private boolean isFindServiceCenter;
    private ArrayList<ServiceCenterResponse> serviceCenterResponseList;
    private ServiceRequestDialog serviceRequestDialog;
    private TimeSlotAlertDialog timeSlotAlertDialog;
    private String serviceRequestComment;

    //Adding unauthorized phone number
    private CustomPhoneNumberDialog customPhoneNumberDialog;
    private AddServiceEngineer serviceEngineer;

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
            shimmerFrameLayout = rootView.findViewById(R.id
                    .effect_shimmer);
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
        ImageView addProduct = binding.addProduct;
        if (addProduct.getVisibility() == View.VISIBLE) {
            binding.parentProduct.setImageResource(R.drawable.ic_add_circle);
            addProduct.setVisibility(View.GONE);
            binding.customProduct.setVisibility(View.GONE);
        } else {
            binding.parentProduct.setImageResource(R.drawable.ic_close);
            addProduct.setVisibility(View.VISIBLE);
            binding.customProduct.setVisibility(View.VISIBLE);
        }
    }

    public void onAddCustomProductClick() {
        Bundle bundle = new Bundle();
        AddUserAddressResponse singleAddressResponse = addressessAdapter.
                getItemFromPosition(addressSelectedPosition);
        bundle.putInt(BundleConstants.ADDRESS_ID, singleAddressResponse.getId());
        ((HomeActivity) getActivity()).replaceFragmentAndAddToStackWithTargetFragment(
                AddCustomProductFragment.class, this, RequestCodes.ADD_CUSTOM_PRODUCT_FRAGMENT,
                bundle, 0, 0, TRANSACTION_TYPE_REPLACE);
    }

    private void initViews() {
        //getting customer id to fetch addresses and product info
        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);

        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);

        binding.parentProduct.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

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
        binding.favoritesRecyclerview.setAdapter(favoritesAdapter);
        binding.favoritesRecyclerview.setLayoutManager(secondLinearLayoutManager);

        //api call to get addresses
        favoritesPresenter.doGetAddressApi(userId);
        loadBottomSheet();
        // getProductsApi();

    }


    private void getProductsApi() {
        binding.favoritesRecyclerview.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        if (addressSelectedPosition == -1) {
            return;
        }
        AddUserAddressResponse singleAddressResponse = addressessAdapter.
                getItemFromPosition(addressSelectedPosition);
        binding.addressesRecyclerview.getLayoutManager().scrollToPosition(
                addressSelectedPosition);
        favoritesPresenter.doFavoritesProductApi(userId, singleAddressResponse.getId());

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
        addressIntent.putExtra(IntentConstants.BUTTON_TEXT, getString(R.string.action_add));
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
                case RequestCodes.ADD_CUSTOM_PRODUCT_FRAGMENT:
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
            productSelectedPosition = position;
            //createBottomSheetView(position);
            createBottomSheetFirstRow(position);
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetFirstRow(int position) {

        int length;
        int[] bottomDrawables;
        String[] bottomNames;
        length = 3;

        bottomNames = new String[3];
        bottomNames[0] = getString(R.string.bottom_option_service);
        bottomNames[1] = getString(R.string.bottom_option_product);
        bottomNames[2] = getString(R.string.bottom_option_showroom);

        bottomDrawables = new int[3];
        bottomDrawables[0] = R.drawable.ic_option_service_support;
        bottomDrawables[1] = R.drawable.ic_option_product;
        bottomDrawables[2] = R.drawable.ic_option_customer;

        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(length);
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, bottomNames, bottomDrawables, bottomSheetFirstRowClickListener, "-1");

    }


    private View.OnClickListener bottomSheetFirstRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String unparsedTag = (String) view.getTag();
            Integer tag = Integer.valueOf(unparsedTag);
            String[] bottomOptions;
            int[] topDrawables;
            changeSelectedViews(bottomSheetPurchasedBinding.firstRow, unparsedTag);
            if (tag == 0) {
                bottomOptions = new String[2];
                bottomOptions[0] = getString(R.string.bottom_option_un_authorized);
                bottomOptions[1] = getString(R.string.bottom_option_authorized);
                topDrawables = new int[2];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_find_service_center;
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
            } else {
                // showFavoriteOptionsDialog();
                return;
            }

            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(bottomOptions.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.secondRow, bottomOptions, topDrawables, bottomSheetSecondRowClickListener, unparsedTag);
        }
    };


    // bottom sheet top view click event
    private View.OnClickListener bottomSheetSecondRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String unparsedTag = (String) view.getTag();
            String[] tagArray = unparsedTag.split(COMMA_SEPARATOR);
            ProductInfoResponse itemFromPosition = favoritesAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.secondRow, unparsedTag);

            String[] bottomOptions = new String[0];
            int[] topDrawables = new int[0];

            int firstRowTag = Integer.parseInt(tagArray[0]);
            int secondRowTag = Integer.parseInt(tagArray[1]);

            // service/support
            if (firstRowTag == 0) {


                //un authorized
                if (secondRowTag == 0) {
                    bottomOptions = new String[3];
                    bottomOptions[0] = getString(R.string.bottom_option_Call);
                    bottomOptions[1] = getString(R.string.bottom_option_service_request);
                    bottomOptions[2] = getString(R.string.bottom_option_add);

                    topDrawables = new int[3];
                    topDrawables[0] = R.drawable.ic_option_details;
                    topDrawables[1] = R.drawable.ic_option_return_product;
                    topDrawables[2] = R.drawable.ic_option_bill;
                } else if (secondRowTag == 1) { // authorized
                    bottomOptions = new String[4];
                    bottomOptions[0] = getString(R.string.bottom_option_Call);
                    bottomOptions[1] = getString(R.string.bottom_option_find_service_center);
                    bottomOptions[2] = getString(R.string.bottom_option_service_request);
                    bottomOptions[3] = getString(R.string.bottom_option_add);

                    topDrawables = new int[4];
                    topDrawables[0] = R.drawable.ic_option_details;
                    topDrawables[1] = R.drawable.ic_option_return_product;
                    topDrawables[2] = R.drawable.ic_option_bill;
                    topDrawables[3] = R.drawable.ic_option_bill;
                }

            } else if (firstRowTag == 1) { // product

                if (secondRowTag == 0) { // details
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
                } else if (secondRowTag == 1) { // warranty

                    String purchasedDate = DateUtils.convertMillisToStringFormat(
                            itemFromPosition.getPurchasedDate(), DateFormatterConstants.DD_MM_YYYY);
                    String warrantyEndDate = DateUtils.convertMillisToStringFormat(
                            itemFromPosition.getWarrantyEndDate(), DateFormatterConstants.DD_MM_YYYY);
                    long noOfDays = DateUtils.convertDifferenceDateIndays(
                            itemFromPosition.getWarrantyEndDate(), System.currentTimeMillis());
                    String warrantyConditions = itemFromPosition.getWarrantyConditions();
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
                            + warrantyConditions
                            + "\n"
                            + getString(
                            R.string.purchased_warranty_ends_on) + warrantyEndDate);
                    return;
                } else if (secondRowTag == 2) { // bill
                    Intent billFormatIntent = new Intent(getActivity(), BillFormatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(BundleConstants.PRODUCT_INFO_RESPONSE, itemFromPosition);
                    billFormatIntent.putExtras(bundle);
                    startActivity(billFormatIntent);
                    return;
                } else if (secondRowTag == 3) { // past history
                    AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                    bottomOptions = new String[0];
                    topDrawables = new int[0];
                } else if (secondRowTag == 4) { // share
                    shareProductDetails(itemFromPosition);
                    return;
                } else if (secondRowTag == 5) { // transfer
                    //  showTransferDialog();
                    return;
                } else if (secondRowTag == 6) { // feed back
                    showFeedBackDialog();
                } else if (secondRowTag == 7) { // suggestions
                    AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                    bottomOptions = new String[0];
                    topDrawables = new int[0];
                }
            } else if (firstRowTag == 2) { // showroom

                if (secondRowTag == 0) { // call
                    callPhoneNumber(itemFromPosition.getStoreContactNumber());
                    return;
                } else if (secondRowTag == 1) { // location
                    showLocationDialog();

                    return;
                } else if (secondRowTag == 2) { // feed back
                    AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                    bottomOptions = new String[0];
                    topDrawables = new int[0];
                }
            }

            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.removeAllViews();
            bottomSheetPurchasedBinding.thirdRow.setWeightSum(bottomOptions.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.thirdRow, bottomOptions, topDrawables, bottomSheetThirdRowClickListener, unparsedTag);
        }
    };

    private void showFeedBackDialog() {
        feedBackDialog = new AppEditTextDialog.AlertDialogBuilder(getActivity(), new
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
                }).title(getString(R.string.bottom_option_feedback))
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        feedBackDialog.showDialog();
        feedBackDialog.setCancelable(true);

    }


    private View.OnClickListener bottomSheetThirdRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String unparsedTag = (String) view.getTag();
            String[] tagArray = unparsedTag.split(COMMA_SEPARATOR);
            int firstRowTag = Integer.parseInt(tagArray[0]);
            int secondRowTag = Integer.parseInt(tagArray[1]);
            int thirdRowTag = Integer.parseInt(tagArray[2]);
            ProductInfoResponse productInfoResponse = favoritesAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.thirdRow, unparsedTag);
            if (firstRowTag == 0) { // service/support
                if (secondRowTag == 0) { // un authorized
                    if (thirdRowTag == 0) { // call
                        List<AddServiceEngineer> serviceEngineerList = productInfoResponse.getServiceEngineerList();
                        showPhoneNumberList(serviceEngineerList);
                        return;
                    } else if (thirdRowTag == 1) { //service request
                        AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                    } else if (thirdRowTag == 2) { // add
                        showCustomPhoneNumberDialog();
                    }
                } else if (secondRowTag == 1) { // authorized

                    if (thirdRowTag == 0) { // call
                        // todo have to check
                        callPhoneNumber(productInfoResponse.getMobileNumber());
                        return;
                    } else if (thirdRowTag == 1) { //find service center
                        // todo have to check
                        isFindServiceCenter = true;
                        loadNearByServiceCentersDialogData(productInfoResponse.getBrandId());
                    } else if (thirdRowTag == 2) { // service center
                        // todo have to check
                        isFindServiceCenter = false;
                        if (serviceCenterResponseList != null) {
                            loadServiceRequesDialogData();
                        } else {
                            loadNearByServiceCentersDialogData(productInfoResponse.getBrandId());
                        }

                    } else if (thirdRowTag == 3) { // add
//TODO have to add

                    }
                }

            }

            //product
            else if (firstRowTag == 1) {
                // details
                if (secondRowTag == 0) {
                    // return policy
                    if (thirdRowTag == 0) {
                        showInformationDialog(getString(R.string.bottom_option_return_policy), productInfoResponse.getReturnPolicy());
                    }
                    // special instruction
                    else if (thirdRowTag == 1) {
                        showInformationDialog(getString(
                                R.string.bottom_option_special_instructions),
                                productInfoResponse.getSpecialInstruction());
                    }
                    //how to use
                    else if (thirdRowTag == 2) {
                        AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                        //                showInformationDialog(itemFromPosition.getInformation());
                    }
                    //description
                    else if (thirdRowTag == 3) {
                        showInformationDialog(getString(
                                R.string.bottom_option_description), productInfoResponse.getInformation()
                                + productInfoResponse.getProductSpecification()
                                + productInfoResponse.getColor()
                                + productInfoResponse.getProductDimensions());
                        return;
                    }
                }
            }


        }

    };

    private void loadServiceRequesDialogData() {
        if (serviceCenterResponseList.size() > 0) {// checking whether service centers are found or not
            loadUsersDataFromServiceCenterId(serviceCenterResponseList.get(0).getId());
        } else {
            showErrorMessage(getString(R.string.error_no_service_centers_found));
        }
    }

    private void loadUsersDataFromServiceCenterId(Integer serviceCenterId) {
        //todo have to check
        favoritesPresenter.getUsersListOfServiceCenters(serviceCenterId);
    }

    private void loadNearByServiceCentersDialogData(String brandId) {
        //todo have to check
        favoritesPresenter.nearByServiceCenters(Integer.parseInt(brandId));
    }


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

    @Override
    public void addedServiceEngineer(ProductInfoResponse productInfoResponse) {
        if (customPhoneNumberDialog != null && customPhoneNumberDialog.isShowing()) {
            customPhoneNumberDialog.dismiss();
        }
        onRefreshListener.onRefresh();
        bottomSheetDialog.dismiss();
    }

    private void showCustomPhoneNumberDialog() {
        if (serviceEngineer == null) {
            serviceEngineer = new AddServiceEngineer();
        }
        customPhoneNumberDialog = new CustomPhoneNumberDialog.AlertDialogBuilder(getActivity(), new
                CustomPhoneNumberAlertDialogCallback() {
                    @Override
                    public void enteredName(String name) {

                        serviceEngineer.setName(name);
                    }

                    @Override
                    public void enteredPhoneNumber(String phoneNumber) {
                        serviceEngineer.setMobileNumber(phoneNumber);
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                if (TextUtils.isEmpty(serviceEngineer.getMobileNumber())) {
                                    showErrorMessage(getString(R.string.error_phone_req));
                                } else {
                                    AppUtils.hideSoftKeyboard(getActivity(), rootView);
                                    favoritesPresenter.addServiceEngineer(serviceEngineer, userId);
                                }
                                break;
                            case AlertDialogCallback.CANCEL:
                                customPhoneNumberDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.action_approval))
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        customPhoneNumberDialog.showDialog();
        customPhoneNumberDialog.setCancelable(true);
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

    // data reload
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getProductsApi();
                   /* if (addressSelectedPosition == -1) {
                        return;
                    }
                    AddUserAddressResponse singleAddressResponse = addressessAdapter.
                            getItemFromPosition(addressSelectedPosition);
                    binding.addressesRecyclerview.getLayoutManager().scrollToPosition(
                            addressSelectedPosition);
                    favoritesPresenter.doFavoritesProductApi(userId, singleAddressResponse.getId());*/
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
            binding.parentProduct.setVisibility(View.VISIBLE);
        } else {
            loadFavoritesProducts(null);
            binding.parentProduct.setVisibility(View.GONE);

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
        {
            favoritesAdapter.setData(favoritesResponseList);
            dismissSwipeRefresh();
        }
        binding.favoritesRecyclerview.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);


    }

    @Override
    public void loadServiceRequest() {
        if (serviceRequestDialog != null && serviceRequestDialog.isShowing()) {
            serviceRequestDialog.dismiss();
        }

    }

    private void showServiceRequestDialog(List<UsersListOfServiceCenters> listOfServiceCenters) {
        if (serviceCenterResponseList == null) {
            return;
        }
        String[] problemsArray = new String[4]; //TODO have to change based legal info
        problemsArray[0] = "Engine repaired";
        problemsArray[1] = "Need service";
        problemsArray[2] = "Power problem";
        problemsArray[3] = "Others";

        serviceRequestDialog = new ServiceRequestDialog.AlertDialogBuilder(getContext(), new ServiceRequestCallback() {
            @Override
            public void getUsersListFromServiceCenterId(int serviceCenterId) {
                loadUsersDataFromServiceCenterId(serviceCenterId);
            }

            @Override
            public void dateClicked(String date) {
                showDatePickerToPlaceServiceRequest(date);
            }

            @Override
            public void timeClicked() {
                showTimePickerToPlaceServiceRequest();
            }

            @Override
            public void enteredText(String commentString) {
                serviceRequestComment = commentString;

            }

            @Override
            public void doServiceRequestApi(ServiceRequest serviceRequest) {
                serviceRequest.setPurchaseId(Integer.valueOf(favoritesAdapter.getItemFromPosition(productSelectedPosition).getWarrantyId()));
                serviceRequest.setCustomerId(userId);
                favoritesPresenter.serviceRequest(serviceRequest);
            }

            @Override
            public void alertDialogCallback(byte dialogStatus) {
                switch (dialogStatus) {
                    case AlertDialogCallback.OK:
                        break;
                    case AlertDialogCallback.CANCEL:
                        serviceRequestDialog.dismiss();
                        break;
                    default:
                        break;
                }

            }
        }).problemsArray(problemsArray)
                .loadUsersList(listOfServiceCenters)
                .loadServiceCentersData(serviceCenterResponseList)
                .build();
        serviceRequestDialog.showDialog();
    }

    private void showTimePickerToPlaceServiceRequest() {
        timeSlotAlertDialog = new TimeSlotAlertDialog.AlertDialogBuilder(getContext(), new TimeSlotAlertDialogCallback() {
            @Override
            public void selectedTimeSlot(String timeSlot) {
                serviceRequestDialog.setTimeFromPicker(timeSlot);
            }

            @Override
            public void alertDialogCallback(byte dialogStatus) {
                timeSlotAlertDialog.dismiss();

            }
        }).build();
        timeSlotAlertDialog.showDialog();


    }

    private void showDatePickerToPlaceServiceRequest(String date) {
        AppUtils.hideSoftKeyboard(getContext(), getView());
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        String selectedDate = date;
        if (!TextUtils.isEmpty(selectedDate)) {
            cal.setTimeInMillis(DateUtils.convertStringFormatToMillis(
                    selectedDate, DateFormatterConstants.DD_MM_YYYY));
        }

        int customStyle = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                ? R.style.DatePickerDialogTheme : android.R.style.Theme_DeviceDefault_Light_Dialog;
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                customStyle,
                serviceRequestDatePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.show();
    }

    // date Listener
    private DatePickerDialog.OnDateSetListener serviceRequestDatePickerListener =
            new DatePickerDialog.OnDateSetListener() {
                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay);

                    String dobInDD_MM_YYYY = DateUtils.convertDateToOtherFormat(
                            selectedDateTime.getTime(), DateFormatterConstants.DD_MM_YYYY);
                    serviceRequestDialog.setDateFromPicker(dobInDD_MM_YYYY);

                }
            };


    @Override
    public void loadNearByServiceCenters(List<ServiceCenterResponse> serviceCenterResponses) {
        this.serviceCenterResponseList = (ArrayList<ServiceCenterResponse>) serviceCenterResponseList;
        if (serviceCenterResponseList == null) {
            return;
        }
        if (isFindServiceCenter) {
            if (serviceCenterResponseList.size() > 0) {// checking whether service centers are found or not
                Intent serviceCenters = new Intent(getActivity(), ServiceCentersActivity.class);
                serviceCenters.putParcelableArrayListExtra(IntentConstants.SERVICE_CENTER_DATA, this.serviceCenterResponseList);
                startActivity(serviceCenters);
            } else {
                showErrorMessage(getString(R.string.error_no_service_centers_found));
            }
        } else {
            loadServiceRequesDialogData();
        }

    }

    @Override
    public void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> usersList) {

        if (serviceRequestDialog != null && serviceRequestDialog.isShowing()) {
            serviceRequestDialog.setUsersData(usersList);
        } else {
            showServiceRequestDialog(usersList);
        }

    }
}
