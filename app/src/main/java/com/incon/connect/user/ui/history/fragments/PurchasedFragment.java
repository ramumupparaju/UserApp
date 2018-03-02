package com.incon.connect.user.ui.history.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

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
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.callbacks.TimeSlotAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppAlertVerticalTwoButtonsDialog;
import com.incon.connect.user.custom.view.AppCheckBoxListDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.CustomPhoneNumberDialog;
import com.incon.connect.user.custom.view.ServiceRequestDialog;
import com.incon.connect.user.custom.view.TimeSlotAlertDialog;
import com.incon.connect.user.databinding.FragmentPurchasedBinding;
import com.incon.connect.user.dto.dialog.CheckedModelSpinner;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.billformat.BillFormatActivity;
import com.incon.connect.user.ui.history.adapter.PurchasedAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.ui.servicecenters.ServiceCentersActivity;
import com.incon.connect.user.utils.DateUtils;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

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
    private AppAlertDialog detailsDialog;
    private AppCheckBoxListDialog productLocationDialog;
    private List<AddUserAddressResponse> productLocationList;
    private Integer addressId;
    private AppEditTextDialog transferDialog;
    private AppEditTextDialog feedBackDialog;
    // private AppFeedBackDialog feedBackDialog;
    private String buyRequestComment;
    private String serviceRequestComment;
    private boolean isFromFavorites = false;
    private AppAlertVerticalTwoButtonsDialog dialogDelete;
    private ServiceRequestDialog serviceRequestDialog;
    private TimeSlotAlertDialog timeSlotAlertDialog;
    private ArrayList<ServiceCenterResponse> serviceCenterResponseList;
    private boolean isFindServiceCenter;
    private ShimmerFrameLayout shimmerFrameLayout;

    //Adding unauthorized phone number
    private CustomPhoneNumberDialog customPhoneNumberDialog;
    private AddServiceEngineer serviceEngineer;


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
                                    purchasedPresenter.addServiceEngineer(serviceEngineer, userId);
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

    private void getProductsApi() {
        binding.purchasedRecyclerview.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        purchasedPresenter.purchased(userId);
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
                int length = 3 ;
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
                }
                else  {
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
                AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
            }
            else if (tag == R.id.SHOWROOM_CALL) {
                callPhoneNumber(productInfoResponse.getStoreContactNumber());
                return;
            } else if (tag == R.id.SHOWROOM_LOCATION) {
                showLocationDialog();
            } else if (tag == R.id.SHOWROOM_FEEDBACK) {
                showFeedBackDialog();
            }


            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.removeAllViews();
            bottomSheetPurchasedBinding.thirdRow.setWeightSum(textArray.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.thirdRow, textArray, drawablesArray, tagsArray, bottomSheetThirdRowClickListener);
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

   /* private void showFeedBackDialog() {
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

    }*/

    private void navigateToAddressActivity() {
        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        startActivityForResult(addressIntent, RequestCodes.ADDRESS_LOCATION);
    }

    private void showTransferDialog() {
        transferDialog = new AppEditTextDialog.AlertDialogBuilder(getActivity(), new
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
                                transferDialog.dismiss();
                                AppUtils.hideSoftKeyboard(getContext(), getView());
                                break;
                            case AlertDialogCallback.CANCEL:
                                transferDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.bottom_option_transfer))
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        transferDialog.showDialog();
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
            }
            else if (tag == R.id.SUPPORT_AUTHORIZE_CALL) {
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


    private void loadNearByServiceCentersDialogData(String brandId) {
        if (TextUtils.isEmpty(brandId)) {
            AppUtils.longToast(getActivity(), getString(R.string.error_contact_customer_care));
        } else {
            purchasedPresenter.nearByServiceCenters(Integer.parseInt(brandId));
        }
    }

    private void loadServiceRequesDialogData() {
        //fetching service certer user info
        if (serviceCenterResponseList.size() > 0) {// checking whether service centers are found or not
            loadUsersDataFromServiceCenterId(serviceCenterResponseList.get(0).getId());
        } else {
            showErrorMessage(getString(R.string.error_no_service_centers_found));
        }
    }

    private void loadUsersDataFromServiceCenterId(Integer serviceCenterId) {
        purchasedPresenter.getUsersListOfServiceCenters(serviceCenterId);
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
                serviceRequest.setPurchaseId(Integer.valueOf(purchasedAdapter.getItemFromPosition(productSelectedPosition).getWarrantyId()));
                serviceRequest.setCustomerId(userId);
                purchasedPresenter.serviceRequest(serviceRequest);
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


    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    purchasedAdapter.clearData();
                    getProductsApi();
                }
            };


    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void handleException(Pair<Integer, String> error) {
        super.handleException(error);
        if (serviceRequestDialog != null && serviceRequestDialog.isShowing()) {
            AppUtils.shortToast(getActivity(), error.second);
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
        AppUtils.showSnackBar(getView(), getString(R.string.action_delete));
    }

    //service sequest
    @Override
    public void loadServiceRequest() {
        if (serviceRequestDialog != null && serviceRequestDialog.isShowing()) {
            serviceRequestDialog.dismiss();
        }
    }

    @Override
    public void loadNearByServiceCenters(List<ServiceCenterResponse> serviceCenterResponseList) {
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

    @Override
    public void addedServiceEngineer(ProductInfoResponse productInfoResponse) {
        if (customPhoneNumberDialog != null && customPhoneNumberDialog.isShowing()) {
            customPhoneNumberDialog.dismiss();
        }
        onRefreshListener.onRefresh();
        bottomSheetDialog.dismiss();
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
