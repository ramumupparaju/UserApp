package com.incon.connect.user.ui.favorites;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.TextAddressDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppCheckBoxListDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.AppUserAddressDialog;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.dto.dialog.CheckedModelSpinner;
import com.incon.connect.user.ui.BasePurchasedFavoritesFragment;
import com.incon.connect.user.ui.addnewmodel.AddCustomProductFragment;
import com.incon.connect.user.ui.billformat.BillFormatActivity;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.favorites.adapter.HorizontalRecycleViewAdapter;
import com.incon.connect.user.ui.history.fragments.PurchasedFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.pin.CustomPinActivity;
import com.incon.connect.user.ui.pin.managers.AppLock;
import com.incon.connect.user.utils.GravitySnapHelper;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.incon.connect.user.AppConstants.ApiRequestKeyConstants.BODY_ADDRESS_ID;
import static com.incon.connect.user.AppConstants.ApiRequestKeyConstants.BODY_ID;
import static com.incon.connect.user.ui.BaseActivity.TRANSACTION_TYPE_REPLACE;

/**
 * Created by PC on 11/4/2017.
 */
public class FavoritesFragment extends BasePurchasedFavoritesFragment implements FavoritesContract.View {


    private AppUserAddressDialog dialog;
    private AddUserAddress addUserAddress;
    private AppCheckBoxListDialog productLocationDialog;
    private AppEditTextDialog productNameEditDialog;
    private String productEdit;


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
            favoritesBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_favorites, container, false);
            favoritesBinding.setFavorites(this);
            rootView = favoritesBinding.getRoot();
            shimmerFrameLayout = rootView.findViewById(R.id
                    .effect_shimmer);
            initFabs();
            initViews();
        }
        setTitle();
        return rootView;
    }

    private void initFabs() {
        final FloatingActionButton programFab1 = new FloatingActionButton(getActivity());
        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        favoritesBinding.fab.showMenuButton(true);
        favoritesBinding.fab.setClosedOnTouchOutside(true);
    }

    //adding location
    public void onLocationAddClick() {
        favoritesBinding.fab.close(false);
        showAddressDialog();
    }

    // add product
    public void onProductAddClick() {
        favoritesBinding.fab.close(false);
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


    public void onAddCustomProductClick() {
        favoritesBinding.fab.close(false);

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

        favoritesBinding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        favoritesBinding.swiperefresh.setOnRefreshListener(onRefreshListener);

        //top recyclerview
        addressessAdapter = new HorizontalRecycleViewAdapter();
        addressessAdapter.setClickCallback(iAddressClickCallback);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        favoritesBinding.addressesRecyclerview.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), linearLayoutManager.getOrientation());
        favoritesBinding.addressesRecyclerview.addItemDecoration(dividerItemDecoration);
        favoritesBinding.addressesRecyclerview.setAdapter(addressessAdapter);
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.END);
        snapHelper.attachToRecyclerView(favoritesBinding.addressesRecyclerview);

        //bottom recyclerview
        favoritesAdapter = new FavoritesAdapter();
        favoritesAdapter.setClickCallback(iProductClickCallback);

        LinearLayoutManager secondLinearLayoutManager = new LinearLayoutManager(getContext());
        favoritesBinding.favoritesRecyclerview.setAdapter(favoritesAdapter);
        favoritesBinding.favoritesRecyclerview.setLayoutManager(secondLinearLayoutManager);

        //api call to get addresses
        favoritesPresenter.doGetAddressApi(userId);
        loadBottomSheet();
        // getProductsApi();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.LOCATION_CHANGED) {
            if (resultCode == Activity.RESULT_OK) {
                productLocationChangeApi();
            } else {
                showErrorMessage(getString(R.string.error_athentication_failed));
            }
            return;
        }
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
            createBottomSheetFirstRow();
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetFirstRow() {

        ArrayList<Integer> drawablesArray = new ArrayList<>();
        ArrayList<String> textArray = new ArrayList<>();
        ArrayList<Integer> tagsArray = new ArrayList<>();


        textArray.add(getString(R.string.bottom_option_service));
        tagsArray.add(R.id.SUPPORT);
        drawablesArray.add(R.drawable.ic_option_service_support);

        textArray.add(getString(R.string.bottom_option_product));
        tagsArray.add(R.id.PRODUCT);
        drawablesArray.add(R.drawable.ic_option_product);

        ProductInfoResponse productInfoResponse = favoritesAdapter.getItemFromPosition(productSelectedPosition);
        if (!productInfoResponse.getCustomProductFlag().equalsIgnoreCase(CUSTOM)) {
            textArray.add(getString(R.string.bottom_option_showroom));
            tagsArray.add(R.id.SHOWROOM);
            drawablesArray.add(R.drawable.ic_option_customer);
        }

        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(tagsArray.size());
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, textArray, drawablesArray, tagsArray, bottomSheetFirstRowClickListener);

    }


    private View.OnClickListener bottomSheetFirstRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Integer tag = (Integer) view.getTag();

            ArrayList<Integer> drawablesArray = new ArrayList<>();
            ArrayList<String> textArray = new ArrayList<>();
            ArrayList<Integer> tagsArray = new ArrayList<>();

            changeSelectedViews(bottomSheetPurchasedBinding.firstRow, tag);
            if (tag == R.id.SUPPORT) {
                textArray.add(getString(R.string.bottom_option_un_authorized));
                textArray.add(getString(R.string.bottom_option_authorized));

                tagsArray.add(R.id.SUPPORT_UNAUTHORIZE);
                tagsArray.add(R.id.SUPPORT_AUTHORIZE);

                drawablesArray.add(R.drawable.ic_option_call);
                drawablesArray.add(R.drawable.ic_option_find_service_center);
            } else if (tag == R.id.PRODUCT) {
                textArray.add(getString(R.string.bottom_option_info));
                tagsArray.add(R.id.PRODUCT_DETAILS);
                drawablesArray.add(R.drawable.ic_option_details);

                textArray.add(getString(R.string.bottom_option_warranty));
                tagsArray.add(R.id.PRODUCT_WARRANTY);
                drawablesArray.add(R.drawable.ic_option_warranty);

                textArray.add(getString(R.string.bottom_option_bill));
                tagsArray.add(R.id.PRODUCT_BILL);
                drawablesArray.add(R.drawable.ic_option_bill);

                textArray.add(getString(R.string.bottom_option_past_history));
                tagsArray.add(R.id.PRODUCT_PAST_HISTORY);
                drawablesArray.add(R.drawable.ic_option_pasthistory);

                textArray.add(getString(R.string.bottom_option_share));
                tagsArray.add(R.id.PRODUCT_SHARE);
                drawablesArray.add(R.drawable.ic_option_share);

                textArray.add(getString(R.string.bottom_option_transfer));
                tagsArray.add(R.id.PRODUCT_TRANSFER);
                drawablesArray.add(R.drawable.ic_option_transfer);

                textArray.add(getString(R.string.bottom_option_feedback));
                tagsArray.add(R.id.PRODUCT_FEEDBACK);
                drawablesArray.add(R.drawable.ic_option_feedback);

                textArray.add(getString(R.string.bottom_option_suggestions));
                tagsArray.add(R.id.PRODUCT_SUGGESTION);
                drawablesArray.add(R.drawable.ic_option_suggestions);

                textArray.add(getString(R.string.bottom_option_edit));
                tagsArray.add(R.id.PRODUCT_EDIT);
                drawablesArray.add(R.drawable.ic_option_suggestions);


            } else if (tag == R.id.SHOWROOM) {
                textArray.add(getString(R.string.bottom_option_Call));
                textArray.add(getString(R.string.bottom_option_location));
                textArray.add(getString(R.string.bottom_option_feedback));

                tagsArray.add(R.id.SHOWROOM_CALL);
                tagsArray.add(R.id.SHOWROOM_LOCATION);
                tagsArray.add(R.id.SHOWROOM_FEEDBACK);

                drawablesArray.add(R.drawable.ic_option_call);
                drawablesArray.add(R.drawable.ic_option_location);
                drawablesArray.add(R.drawable.ic_option_feedback);


            } else {
                // showFavoritesLocationChangeDialog();
                return;
            }

            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(tagsArray.size());
            setBottomViewOptions(bottomSheetPurchasedBinding.secondRow, textArray, drawablesArray, tagsArray, bottomSheetSecondRowClickListener);

        }
    };


    // bottom sheet top view click event
    private View.OnClickListener bottomSheetSecondRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Integer tag = (Integer) view.getTag();

            ArrayList<Integer> drawablesArray = new ArrayList<>();
            ArrayList<String> textArray = new ArrayList<>();
            ArrayList<Integer> tagsArray = new ArrayList<>();

            ProductInfoResponse productInfoResponse = favoritesAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.secondRow, tag);


            if (tag == R.id.SUPPORT_UNAUTHORIZE) {


                List<AddServiceEngineer> serviceEngineerList = productInfoResponse.getServiceEngineerList();
                if (serviceEngineerList != null && serviceEngineerList.size() > 0) {
                    textArray.add(getString(R.string.bottom_option_Call));
                    tagsArray.add(R.id.SUPPORT_UNAUTHORIZE_CALL);
                    drawablesArray.add(R.drawable.ic_option_call);
                }

                textArray.add(getString(R.string.bottom_option_find_service_center));
                tagsArray.add(R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_CENTER);
                drawablesArray.add(R.drawable.ic_option_bill);

                textArray.add(getString(R.string.bottom_option_service_request));
                tagsArray.add(R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_REQUEST);
                drawablesArray.add(R.drawable.ic_option_bill);

                textArray.add(getString(R.string.bottom_option_add));
                tagsArray.add(R.id.SUPPORT_UNAUTHORIZE_ADD);
                drawablesArray.add(R.drawable.ic_option_bill);

            } else if (tag == R.id.SUPPORT_AUTHORIZE) {

                textArray.add(getString(R.string.bottom_option_call_customer_care));
                tagsArray.add(R.id.SUPPORT_AUTHORIZE_CALL);
                drawablesArray.add(R.drawable.ic_option_call);

                textArray.add(getString(R.string.bottom_option_find_service_center));
                tagsArray.add(R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_CENTER);
                drawablesArray.add(R.drawable.ic_option_bill);

                textArray.add(getString(R.string.bottom_option_service_request));
                tagsArray.add(R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_REQUEST);
                drawablesArray.add(R.drawable.ic_option_bill);
            } else if (tag == R.id.PRODUCT_DETAILS) {

                if (!TextUtils.isEmpty(productInfoResponse.getSpecialInstruction())) {
                    textArray.add(getString(R.string.bottom_option_special_instructions));
                    tagsArray.add(R.id.PRODUCT_DETAILS_SPECIAL_INSTUCTIONS);
                    drawablesArray.add(R.drawable.ic_option_sp_instructions);
                }

                textArray.add(getString(R.string.bottom_option_details));
                tagsArray.add(R.id.PRODUCT_DETAILS_DESCRIPTION);
                drawablesArray.add(R.drawable.ic_option_details);

            } else if (tag == R.id.PRODUCT_WARRANTY) {
                showInformationDialog(getString(
                        R.string.bottom_option_warranty), AppUtils.getFormattedWarrantyDataInString(productInfoResponse, getActivity()));
            } else if (tag == R.id.PRODUCT_BILL) {
                Intent billFormatIntent = new Intent(getActivity(), BillFormatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(BundleConstants.PRODUCT_INFO_RESPONSE, productInfoResponse);
                billFormatIntent.putExtras(bundle);
                startActivity(billFormatIntent);
                return;
            } else if (tag == R.id.PRODUCT_PAST_HISTORY) {
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
            } else if (tag == R.id.PRODUCT_SHARE) {
                shareProductDetails(productInfoResponse);
                return;
            } else if (tag == R.id.PRODUCT_TRANSFER) {
                showTransferDialog();
            } else if (tag == R.id.PRODUCT_FEEDBACK) {
                doReviewsApi();
            } else if (tag == R.id.PRODUCT_SUGGESTION) {
                showSuggestionsDialog();
            } else if (tag == R.id.PRODUCT_EDIT) {
                textArray.add(getString(R.string.bottom_option_nick_name));
                textArray.add(getString(R.string.bottom_option_location_change));
                textArray.add(getString(R.string.bottom_option_delete));

                tagsArray.add(R.id.PRODUCT_EDIT_NICK_NAME);
                tagsArray.add(R.id.PRODUCT_EDIT_LOCATION_CHANGE);
                tagsArray.add(R.id.PRODUCT_EDIT_DELETE);

                drawablesArray.add(R.drawable.ic_option_details);
                drawablesArray.add(R.drawable.ic_option_details);
                drawablesArray.add(R.drawable.ic_option_details);

            } else if (tag == R.id.SHOWROOM_CALL) {
                callPhoneNumber(productInfoResponse.getStoreContactNumber());
                return;
            } else if (tag == R.id.SHOWROOM_LOCATION) {
                showLocationDialog();
            } else if (tag == R.id.SHOWROOM_FEEDBACK) {
                doReviewsApi();
            }

            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.removeAllViews();
            bottomSheetPurchasedBinding.thirdRow.setWeightSum(tagsArray.size());
            setBottomViewOptions(bottomSheetPurchasedBinding.thirdRow, textArray, drawablesArray, tagsArray, bottomSheetThirdRowClickListener);
        }
    };


    private View.OnClickListener bottomSheetThirdRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            ProductInfoResponse productInfoResponse = favoritesAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.thirdRow, tag);

            if (tag == R.id.SUPPORT_UNAUTHORIZE_CALL) {
                List<AddServiceEngineer> serviceEngineerList = productInfoResponse.getServiceEngineerList();
                showPhoneNumberList(serviceEngineerList);
                return;
            } else if (tag == R.id.SUPPORT_UNAUTHORIZE_ADD) {
                showCustomPhoneNumberDialog();

            } else if (tag == R.id.SUPPORT_AUTHORIZE_CALL) {
                callPhoneNumber(productInfoResponse.getMobileNumber());
                return;

            } else if (tag == R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_CENTER) {
                isFindServiceCenter = true;
                loadNearByServiceCentersDialogData(ServiceConstants.AUTHORIZED_TYPE,productInfoResponse.getBrandId());

            } else if (tag == R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_REQUEST) {
                isFindServiceCenter = false;
                if (serviceCenterResponseList != null) {
                    loadServiceRequesDialogData();
                } else {
                    loadNearByServiceCentersDialogData(ServiceConstants.AUTHORIZED_TYPE,productInfoResponse.getBrandId());
                }

            } else if (tag == R.id.PRODUCT_DETAILS_SPECIAL_INSTUCTIONS) {
                showInformationDialog(getString(
                        R.string.bottom_option_special_instructions),
                        productInfoResponse.getSpecialInstruction());
            } else if (tag == R.id.PRODUCT_DETAILS_DESCRIPTION) {
                showInformationDialog(getString(
                        R.string.bottom_option_description), productInfoResponse.getInformation()
                        + productInfoResponse.getProductSpecification()
                        + productInfoResponse.getColor()
                        + productInfoResponse.getProductDimensions());
                return;

            } else if (tag == R.id.PRODUCT_EDIT_NICK_NAME) {
                productNameEditDialog();
                return;
            } else if (tag == R.id.PRODUCT_EDIT_LOCATION_CHANGE) {
                showFavoritesLocationChangeDialog();
                return;
            } else if (tag == R.id.PRODUCT_EDIT_DELETE) {
                showDeleteDialog();

            }
        }

    };

    private void productNameEditDialog() {
        productNameEditDialog = new AppEditTextDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String commentString) {
                        ProductInfoResponse itemFromPosition = favoritesAdapter.
                                getItemFromPosition(productSelectedPosition);
                        productEdit = commentString;
                        HashMap<String, String> productNameEditMap = new HashMap<>();
                        productNameEditMap.put(ApiRequestKeyConstants.BODY_NICK_NAME, productEdit);
                        productNameEditMap.put(BODY_ID, String.valueOf(itemFromPosition.getFavouriteId()));
                        favoritesPresenter.doLocationChangeProductNameEditApi(productNameEditMap);
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                break;
                            case AlertDialogCallback.CANCEL:
                                productNameEditDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.bottom_option_product_name_edit))
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        productNameEditDialog.showDialog();
        productNameEditDialog.setCancelable(true);
    }


    private void showFavoritesLocationChangeDialog() {

        final List<AddUserAddressResponse> addressResponsesList = addressessAdapter.getAddressResponsesList();
        if (addressResponsesList == null || addressResponsesList.size() == 0) {
            Logger.e("showFavoriteOptionsDialog", "addressResponsesList are either empty are zero");
            return;
        }

        //set previous selected categories as checked
        List<CheckedModelSpinner> filterNamesList = new ArrayList<>();
        for (AddUserAddressResponse addUserAddressResponse : addressResponsesList) {

            AddUserAddressResponse singleAddressResponse = addressessAdapter.
                    getItemFromPosition(addressSelectedPosition);
            if (singleAddressResponse.getId() == addUserAddressResponse.getId()) {
                continue;
            }
            CheckedModelSpinner checkedModelSpinner = new CheckedModelSpinner();
            checkedModelSpinner.setName(addUserAddressResponse.getName());
            filterNamesList.add(checkedModelSpinner);
        }
        productLocationDialog = new AppCheckBoxListDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String selectedLocationName) {
                        for (AddUserAddressResponse addUserAddressResponse : addressResponsesList) {
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
                                Intent pinIntent = new Intent(getActivity(), CustomPinActivity.class);
                                pinIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                                startActivityForResult(pinIntent, RequestCodes.LOCATION_CHANGED);
                                break;
                            case AlertDialogCallback.CANCEL:
                                productLocationDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.bottom_option_location_change))
                .spinnerItems(filterNamesList)
                .build();
        productLocationDialog.showDialog();
        productLocationDialog.setRadioType(true);
    }

    private void productLocationChangeApi() {
        ProductInfoResponse itemFromPosition = favoritesAdapter.
                getItemFromPosition(productSelectedPosition);
        HashMap<String, String> productLocationChangeMap = new HashMap<>();
        productLocationChangeMap.put(BODY_ID, String.valueOf(itemFromPosition.getFavouriteId()));
        productLocationChangeMap.put(BODY_ADDRESS_ID, String.valueOf(itemFromPosition.getAddressId()));
        favoritesPresenter.doLocationChangeProductNameEditApi(productLocationChangeMap);
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


    @Override
    public void loadAddresses(List<AddUserAddressResponse> addressesList) {
        if (addressesList == null) {
            addressesList = new ArrayList<>();
        }
        addressessAdapter.setData(addressesList);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dismissSwipeRefresh();

        if (addressesList.size() > 0) {
            iAddressClickCallback.onClickPosition(0);

            favoritesBinding.customFab.show(false);
            favoritesBinding.addFavFab.show(false);
        } else {
            loadFavoritesProducts(null);
            // binding.parentProduct.setVisibility(View.GONE);

            favoritesBinding.customFab.hide(false);
            favoritesBinding.addFavFab.hide(false);
        }
    }

    @Override
    public void onLocationChanged() {
        dismissDialog(productLocationDialog);
        dismissDialog(productNameEditDialog);
        dismissDialog(bottomSheetDialog);
        onRefreshListener.onRefresh();
    }


    @Override
    public void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList) {
        if (favoritesResponseList == null) {
            favoritesResponseList = new ArrayList<>();
        }
        if (favoritesResponseList.size() == 0) {
            // binding.listHeader.setVisibility(View.GONE);
            favoritesBinding.noItemsTextview.setVisibility(View.VISIBLE);
        } else {
            // binding.listHeader.setVisibility(View.VISIBLE);
            favoritesBinding.noItemsTextview.setVisibility(View.GONE);
        }
        {
            favoritesAdapter.setData(favoritesResponseList);
            dismissSwipeRefresh();
        }
        favoritesBinding.favoritesRecyclerview.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void productReviews() {

    }

    private void dismissSwipeRefresh() {
        if (favoritesBinding.swiperefresh.isRefreshing()) {
            favoritesBinding.swiperefresh.setRefreshing(false);
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
    public void onSearchClickListerner(String searchableText, String searchType) {
        //DO nothing
    }
}
