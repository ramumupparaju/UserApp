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

import com.incon.connect.user.AppConstants;
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
import com.incon.connect.user.utils.Logger;
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
        ArrayList<Integer> drawablesArray = new ArrayList<>();
        ArrayList<String> textArray = new ArrayList<>();
        ArrayList<Integer> tagsArray = new ArrayList<>();

        ProductInfoResponse productInfoResponse = purchasedAdapter.getItemFromPosition(productSelectedPosition);


        tagsArray.add(R.id.SUPPORT);
        textArray.add(getString(R.string.bottom_option_service));
        drawablesArray.add(R.drawable.ic_option_service_support);

        tagsArray.add(R.id.PRODUCT);
        textArray.add(getString(R.string.bottom_option_product));
        drawablesArray.add(R.drawable.ic_option_product);

        tagsArray.add(R.id.SHOWROOM);
        textArray.add(getString(R.string.bottom_option_showroom));
        drawablesArray.add(R.drawable.ic_option_customer);

        tagsArray.add(R.id.DELETE);
        textArray.add(getString(R.string.bottom_option_delete));
        drawablesArray.add(R.drawable.ic_option_delete);


        if (productInfoResponse.getAddressId() == null) { //checking whether it is already added as favorite or not
            tagsArray.add(R.id.FAVORITE);
            textArray.add(getString(R.string.bottom_option_add_as_favorite));
            drawablesArray.add(R.drawable.ic_user_favorite);
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

    // bottom sheet click event
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
                tagsArray.add(R.id.SUPPORT_UNAUTHORIZE);
                drawablesArray.add(R.drawable.ic_option_call);

                textArray.add(getString(R.string.bottom_option_authorized));
                tagsArray.add(R.id.SUPPORT_AUTHORIZE);
                drawablesArray.add(R.drawable.ic_option_find_service_center);

            } else if (tag == R.id.PRODUCT) {
                textArray.add(getString(R.string.bottom_option_info));
                tagsArray.add(R.id.PRODUCT_DETAILS);
                drawablesArray.add(R.drawable.ic_option_details);

                ProductInfoResponse productInfoResponse = purchasedAdapter.getItemFromPosition(productSelectedPosition);
                if (!productInfoResponse.getCategoryName().equalsIgnoreCase(AppConstants.CATEGORY_AUTOMOBILES)) {
                    textArray.add(getString(R.string.bottom_option_warranty));
                    tagsArray.add(R.id.PRODUCT_WARRANTY);
                    drawablesArray.add(R.drawable.ic_option_warranty);
                }
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


            } else if (tag == R.id.SHOWROOM) {
                int length = 3;
                textArray.add(getString(R.string.bottom_option_Call));
                tagsArray.add(R.id.SHOWROOM_CALL);
                drawablesArray.add(R.drawable.ic_option_call);

                tagsArray.add(R.id.SHOWROOM_LOCATION);
                textArray.add(getString(R.string.bottom_option_location));
                drawablesArray.add(R.drawable.ic_option_location);

                textArray.add(getString(R.string.bottom_option_feedback));
                tagsArray.add(R.id.SHOWROOM_FEEDBACK);
                drawablesArray.add(R.drawable.ic_option_feedback);

            } else if (tag == R.id.DELETE) {
                showDeleteDialog();
            } else if (tag == R.id.FAVORITE) {
                showFavoriteOptionsDialog();
            }
            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.secondRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRow.removeAllViews();
            bottomSheetPurchasedBinding.secondRow.setWeightSum(textArray.size());
            setBottomViewOptions(bottomSheetPurchasedBinding.secondRow, textArray, drawablesArray, tagsArray, bottomSheetSecondRowClickListener);


        }
    };


    //  favorite options
    private void showFavoriteOptionsDialog() {
        if (addressesList == null) {
            Logger.e("showFavoriteOptionsDialog", "not showing dialog because noplaces found");
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

            ArrayList<Integer> drawablesArray = new ArrayList<>();
            ArrayList<String> textArray = new ArrayList<>();
            ArrayList<Integer> tagsArray = new ArrayList<>();


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
                doReviewsApi();
            } else if (tag == R.id.PRODUCT_SUGGESTION) {
                showSuggestionsDialog();
            } else if (tag == R.id.SHOWROOM_CALL) {
                callPhoneNumber(productInfoResponse.getStoreContactNumber());
                return;
            } else if (tag == R.id.SHOWROOM_LOCATION) {
                showLocationDialog();
            } else if (tag == R.id.SHOWROOM_FEEDBACK) {
                doReviewsApi();
            }


            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.removeAllViews();
            bottomSheetPurchasedBinding.thirdRow.setWeightSum(textArray.size());
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
