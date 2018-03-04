package com.incon.connect.user.ui.history.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppCheckBoxListDialog;
import com.incon.connect.user.dto.dialog.CheckedModelSpinner;
import com.incon.connect.user.ui.BasePurchasedFavoritesFragment;
import com.incon.connect.user.ui.billformat.BillFormatActivity;
import com.incon.connect.user.ui.history.adapter.PurchasedAdapter;
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
public class PurchasedFragment extends BasePurchasedFavoritesFragment implements PurchasedContract.View {

    private AppCheckBoxListDialog favoriteDialog;

    private List<AddUserAddressResponse> addressesList;
    private boolean isFromFavorites = false;

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
            rootView = binding.getRoot();
            shimmerFrameLayout = rootView.findViewById(R.id
                    .effect_shimmer);
            loadBottomSheet();
            initViews();
        }
        setTitle();
        return rootView;
    }

    // load bottom sheet
    @Override
    public void loadBottomSheet() {
        super.loadBottomSheet();
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
        binding.purchasedRecyclerview.setAdapter(purchasedAdapter);
        binding.purchasedRecyclerview.setLayoutManager(linearLayoutManager);
        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        getProductsApi();


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
            serviceCenterResponseList = null;
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
                createBottomSheetFirstRow();
                bottomSheetDialog.show();
            }
        }
    };

    // bottom sheet first row creation
    private void createBottomSheetFirstRow() {
        int length;
        int[] drawablesArray;
        String[] textArray;
        int[] tagsArray;

        ProductInfoResponse productInfoResponse = purchasedAdapter.getItemFromPosition(productSelectedPosition);
        if (productInfoResponse.getAddressId() == null) { //checking whether it is already installed or not
            length = 5;
        } else {
            length = 4;
        }

        tagsArray = new int[length];
        tagsArray[0] = R.id.SUPPORT;
        tagsArray[1] = R.id.PRODUCT;
        tagsArray[2] = R.id.SHOWROOM;
        tagsArray[3] = R.id.DELETE;

        textArray = new String[length];
        textArray[0] = getString(R.string.bottom_option_service);
        textArray[1] = getString(R.string.bottom_option_product);
        textArray[2] = getString(R.string.bottom_option_showroom);
        textArray[3] = getString(R.string.bottom_option_delete);

        drawablesArray = new int[length];
        drawablesArray[0] = R.drawable.ic_option_service_support;
        drawablesArray[1] = R.drawable.ic_option_product;
        drawablesArray[2] = R.drawable.ic_option_customer;
        drawablesArray[3] = R.drawable.ic_option_delete;

        if (length == 5) {
            tagsArray[4] = R.id.FAVORITE;
            textArray[4] = getString(R.string.bottom_option_add_as_favorite);
            drawablesArray[4] = R.drawable.ic_user_favorite;
        }

        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.firstRow.removeAllViews();
        bottomSheetPurchasedBinding.firstRow.setWeightSum(length);
        setBottomViewOptions(bottomSheetPurchasedBinding.firstRow, textArray, drawablesArray, tagsArray, bottomSheetFirstRowClickListener);
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
            if (tag == R.id.SUPPORT) {
                int length = 2;
                textArray = new String[length];
                textArray[0] = getString(R.string.bottom_option_un_authorized);
                textArray[1] = getString(R.string.bottom_option_authorized);

                tagsArray = new int[length];
                tagsArray[0] = R.id.SUPPORT_UNAUTHORIZE;
                tagsArray[1] = R.id.SUPPORT_AUTHORIZE;

                drawablesArray = new int[length];
                drawablesArray[0] = R.drawable.ic_option_call;
                drawablesArray[1] = R.drawable.ic_option_find_service_center;
            } else if (tag == R.id.PRODUCT) {
                int length = 8;
                textArray = new String[length];
                textArray[0] = getString(R.string.bottom_option_info);
                textArray[1] = getString(R.string.bottom_option_warranty);
                textArray[2] = getString(R.string.bottom_option_bill);
                textArray[3] = getString(R.string.bottom_option_past_history);
                textArray[4] = getString(R.string.bottom_option_share);
                textArray[5] = getString(R.string.bottom_option_transfer);
                textArray[6] = getString(R.string.bottom_option_feedback);
                textArray[7] = getString(R.string.bottom_option_suggestions);

                tagsArray = new int[length];
                tagsArray[0] = R.id.PRODUCT_DETAILS;
                tagsArray[1] = R.id.PRODUCT_WARRANTY;
                tagsArray[2] = R.id.PRODUCT_BILL;
                tagsArray[3] = R.id.PRODUCT_PAST_HISTORY;
                tagsArray[4] = R.id.PRODUCT_SHARE;
                tagsArray[5] = R.id.PRODUCT_TRANSFER;
                tagsArray[6] = R.id.PRODUCT_FEEDBACK;
                tagsArray[7] = R.id.PRODUCT_SUGGESTION;

                drawablesArray = new int[length];
                drawablesArray[0] = R.drawable.ic_option_details;
                drawablesArray[1] = R.drawable.ic_option_warranty;
                drawablesArray[2] = R.drawable.ic_option_bill;
                drawablesArray[3] = R.drawable.ic_option_pasthistory;
                drawablesArray[4] = R.drawable.ic_option_share;
                drawablesArray[5] = R.drawable.ic_option_transfer;
                drawablesArray[6] = R.drawable.ic_option_feedback;
                drawablesArray[7] = R.drawable.ic_option_suggestions;
            } else if (tag == R.id.SHOWROOM) {
                int length = 3;
                textArray = new String[length];
                textArray[0] = getString(R.string.bottom_option_Call);
                textArray[1] = getString(R.string.bottom_option_location);
                textArray[2] = getString(R.string.bottom_option_feedback);

                tagsArray = new int[length];
                tagsArray[0] = R.id.SHOWROOM_CALL;
                tagsArray[1] = R.id.SHOWROOM_LOCATION;
                tagsArray[2] = R.id.SHOWROOM_FEEDBACK;

                drawablesArray = new int[length];
                drawablesArray[0] = R.drawable.ic_option_call;
                drawablesArray[1] = R.drawable.ic_option_location;
                drawablesArray[2] = R.drawable.ic_option_feedback;
            } else if (tag == R.id.DELETE) {
                showDeleteDialog();
                return;
            } else if (tag == R.id.FAVORITE) {
                showFavoriteOptionsDialog();
                return;
            }
            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(textArray.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.secondRow, textArray, drawablesArray, tagsArray, bottomSheetSecondRowClickListener);


        }
    };


    //  favorite options
    private void showFavoriteOptionsDialog() {
        if (addressesList == null) {
            //TODO add error message
            return;
        }

        //set previous selected categories as checked
        List<CheckedModelSpinner> filterNamesList = new ArrayList<>();

        for (AddUserAddressResponse addUserAddressResponse : addressesList) {
            CheckedModelSpinner checkedModelSpinner = new CheckedModelSpinner();
            checkedModelSpinner.setName(addUserAddressResponse.getName());
            filterNamesList.add(checkedModelSpinner);
        }
        favoriteDialog = new AppCheckBoxListDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String selectedLocationName) {
                        for (AddUserAddressResponse addUserAddressResponse : addressesList) {
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
                                favoriteDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.action_add_as_favorite))
                .spinnerItems(filterNamesList)
                .build();
        favoriteDialog.showDialog();
        favoriteDialog.setRadioType(true);
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

    // bottom sheet top view click event
    private View.OnClickListener bottomSheetSecondRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            //String[] tagArray = unparsedTag.split(COMMA_SEPARATOR);
            ProductInfoResponse productInfoResponse = purchasedAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.secondRow, tag);
            String[] textArray = new String[0];
            int[] drawablesArray = new int[0];
            int[] tagsArray = new int[0];

            if (tag == R.id.SUPPORT_UNAUTHORIZE) {
                int length = 3;
                List<AddServiceEngineer> serviceEngineerList = productInfoResponse.getServiceEngineerList();
                if (serviceEngineerList != null && serviceEngineerList.size() > 0) {
                    length = 4;
                }
                textArray = new String[length];
                tagsArray = new int[length];
                drawablesArray = new int[length];

                if (length == 4) {
                    textArray[0] = getString(R.string.bottom_option_Call);
                    tagsArray[0] = R.id.SUPPORT_UNAUTHORIZE_CALL;
                    drawablesArray[0] = R.drawable.ic_option_call;

                    textArray[1] = getString(R.string.bottom_option_add);
                    tagsArray[1] = R.id.SUPPORT_UNAUTHORIZE_ADD;
                    drawablesArray[1] = R.drawable.ic_option_bill;

                    textArray[2] = getString(R.string.bottom_option_find_service_center);
                    tagsArray[2] = R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_CENTER;
                    drawablesArray[2] = R.drawable.ic_option_bill;

                    textArray[3] = getString(R.string.bottom_option_service_request);
                    tagsArray[3] = R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_REQUEST;
                    drawablesArray[3] = R.drawable.ic_option_bill;
                } else {
                    textArray[0] = getString(R.string.bottom_option_add);
                    tagsArray[0] = R.id.SUPPORT_UNAUTHORIZE_ADD;
                    drawablesArray[0] = R.drawable.ic_option_bill;

                    textArray[1] = getString(R.string.bottom_option_find_service_center);
                    tagsArray[1] = R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_CENTER;
                    drawablesArray[1] = R.drawable.ic_option_bill;

                    textArray[2] = getString(R.string.bottom_option_service_request);
                    tagsArray[2] = R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_REQUEST;
                    drawablesArray[2] = R.drawable.ic_option_bill;
                }

            } else if (tag == R.id.SUPPORT_AUTHORIZE) {

                int length = 3;

                textArray = new String[length];
                textArray[0] = getString(R.string.bottom_option_call_customer_care);
                textArray[1] = getString(R.string.bottom_option_find_service_center);
                textArray[2] = getString(R.string.bottom_option_service_request);

                tagsArray = new int[length];
                tagsArray[0] = R.id.SUPPORT_AUTHORIZE_CALL;
                tagsArray[1] = R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_CENTER;
                tagsArray[2] = R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_REQUEST;

                drawablesArray = new int[length];
                drawablesArray[0] = R.drawable.ic_option_call;
                drawablesArray[1] = R.drawable.ic_option_bill;
                drawablesArray[2] = R.drawable.ic_option_bill;
            } else if (tag == R.id.PRODUCT_DETAILS) {
                int length = TextUtils.isEmpty(productInfoResponse.getSpecialInstruction()) ? 1 : 2;

                textArray = new String[length];
                tagsArray = new int[length];
                drawablesArray = new int[length];

                if (length == 2) {
                    textArray[0] = getString(R.string.bottom_option_special_instructions);
                    tagsArray[0] = R.id.PRODUCT_DETAILS_SPECIAL_INSTUCTIONS;
                    drawablesArray[0] = R.drawable.ic_option_sp_instructions;

                    textArray[1] = getString(R.string.bottom_option_details);
                    tagsArray[1] = R.id.PRODUCT_DETAILS_DESCRIPTION;
                    drawablesArray[1] = R.drawable.ic_option_details;
                } else {
                    textArray[0] = getString(R.string.bottom_option_details);
                    tagsArray[0] = R.id.PRODUCT_DETAILS_DESCRIPTION;
                    drawablesArray[0] = R.drawable.ic_option_details;
                }
            } else if (tag == R.id.PRODUCT_WARRANTY) {
                showInformationDialog(getString(
                        R.string.bottom_option_warranty), AppUtils.getFormattedWarrantyDataInString(productInfoResponse, getActivity()));
                return;

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
                showFeedBackDialog();
            } else if (tag == R.id.PRODUCT_SUGGESTION) {
                showSuggestionsDialog();
            } else if (tag == R.id.SHOWROOM_CALL) {
                callPhoneNumber(productInfoResponse.getStoreContactNumber());
                return;
            } else if (tag == R.id.SHOWROOM_LOCATION) {
                showLocationDialog();
            } else if (tag == R.id.SHOWROOM_FEEDBACK) {
                showFeedBackDialog();
            }


            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.removeAllViews();
            bottomSheetPurchasedBinding.thirdRow.setWeightSum(textArray.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.thirdRow, textArray, drawablesArray, tagsArray, bottomSheetThirdRowClickListener);
        }
    };


    private View.OnClickListener bottomSheetThirdRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            ProductInfoResponse productInfoResponse = purchasedAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.thirdRow, tag);

            if (tag == R.id.SUPPORT_UNAUTHORIZE_CALL) {
                List<AddServiceEngineer> serviceEngineerList = productInfoResponse.getServiceEngineerList();
                showPhoneNumberList(serviceEngineerList);
                return;
            } else if (tag == R.id.SUPPORT_UNAUTHORIZE_ADD) {
                showCustomPhoneNumberDialog();
            } else if (tag == R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_CENTER) {
                // todo have call  service centers api
            } else if (tag == R.id.SUPPORT_UNAUTHORIZE_FIND_SERVICE_REQUEST) {
                // todo have call  service request api
            } else if (tag == R.id.SUPPORT_AUTHORIZE_CALL) {
                callPhoneNumber(productInfoResponse.getMobileNumber());
                return;

            } else if (tag == R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_CENTER) {
                isFindServiceCenter = true;
                loadNearByServiceCentersDialogData(productInfoResponse.getBrandId());

            } else if (tag == R.id.SUPPORT_AUTHORIZE_FIND_SERVICE_REQUEST) {
                isFindServiceCenter = false;
                if (serviceCenterResponseList != null) {
                    loadServiceRequesDialogData();
                } else {
                    loadNearByServiceCentersDialogData(productInfoResponse.getBrandId());
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
            }
        }

    };

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
            if (isFromFavorites) {
                binding.purchasedTextview.setText(getString(R.string.error_no_products_for_favorites));
            }
            binding.purchasedTextview.setVisibility(View.VISIBLE);
        } else {
            purchasedAdapter.setData(productInfoResponses);
        }
        dismissSwipeRefresh();

        binding.purchasedRecyclerview.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);
    }


    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }


    @Override
    public void loadAddresses(List<AddUserAddressResponse> productLocationList) {
        this.addressesList = productLocationList;
    }

    // add product to favorites list
    @Override
    public void addedToFavorite() {
        if (favoriteDialog != null && favoriteDialog.isShowing()) {
            favoriteDialog.dismiss();
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
