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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.callbacks.ServiceRequestCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.callbacks.TimeSlotAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppAlertVerticalTwoButtonsDialog;
import com.incon.connect.user.custom.view.AppCheckBoxListDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.AppFeedBackDialog;
import com.incon.connect.user.custom.view.ServiceRequestDialog;
import com.incon.connect.user.custom.view.TimeSlotAlertDialog;
import com.incon.connect.user.databinding.FragmentPurchasedBinding;
import com.incon.connect.user.dto.dialog.CheckedModelSpinner;
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
    private AppFeedBackDialog buyFeedBackRequestDialog;
    private String buyRequestComment;
    private String serviceRequestComment;
    private boolean isFromFavorites = false;
    private AppAlertVerticalTwoButtonsDialog dialogDelete;
    private ServiceRequestDialog serviceRequestDialog;
    private TimeSlotAlertDialog timeSlotAlertDialog;


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
                createBottomSheetFirstRow(position);
                bottomSheetDialog.show();
            }
        }
    };

    // bottom sheet first row creation
    private void createBottomSheetFirstRow(int position) {
        int length;
        int[] bottomDrawables;
        String[] bottomNames;
        ProductInfoResponse productInfoResponse = purchasedAdapter.getItemFromPosition(productSelectedPosition);
        // checking whether product is installed or not
        if (productInfoResponse.getStatus().equalsIgnoreCase(StatusConstants.INSTALLED) && productInfoResponse.getAddressId() == null) {
            length = 5;
        } else if (productInfoResponse.getAddressId() != null) { //checking whether it is already installed or not
            length = 4;
        } else {
            length = 4;
        }

        bottomNames = new String[length];
        bottomNames[0] = getString(R.string.bottom_option_service);
        bottomNames[1] = getString(R.string.bottom_option_product);
        bottomNames[2] = getString(R.string.bottom_option_showroom);
        bottomNames[3] = getString(R.string.bottom_option_delete);

        bottomDrawables = new int[length];
        bottomDrawables[0] = R.drawable.ic_option_service_support;
        bottomDrawables[1] = R.drawable.ic_option_product;
        bottomDrawables[2] = R.drawable.ic_option_customer;
        bottomDrawables[3] = R.drawable.ic_option_delete;

        if (length == 5) {
            bottomNames[4] = getString(R.string.bottom_option_add_as_favorite);
            bottomDrawables[4] = R.drawable.ic_user_favorite;
        }

        bottomSheetPurchasedBinding.firstRow.setVisibility(View.VISIBLE);
        bottomSheetPurchasedBinding.secondRowLine.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.secondRow.setVisibility(View.GONE);
        bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.GONE);
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
            } else if (tag == 3) {
                showDeleteDialog();
                return;
            } else {
                showFavoriteOptionsDialog();
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
            String unparsedTag = (String) view.getTag();
            String[] tagArray = unparsedTag.split(COMMA_SEPARATOR);
            ProductInfoResponse itemFromPosition = purchasedAdapter.getItemFromPosition(
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
                    showTransferDialog();
                    return;
                } else if (secondRowTag == 6) { // feed back
                    AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                    bottomOptions = new String[0];
                    topDrawables = new int[0];
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

            bottomSheetPurchasedBinding.thirdRowLine.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.setVisibility(View.VISIBLE);
            bottomSheetPurchasedBinding.thirdRow.removeAllViews();
            bottomSheetPurchasedBinding.thirdRow.setWeightSum(bottomOptions.length);
            setBottomViewOptions(bottomSheetPurchasedBinding.thirdRow, bottomOptions, topDrawables, bottomSheetThirdRowClickListener, unparsedTag);
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
            String unparsedTag = (String) view.getTag();
            String[] tagArray = unparsedTag.split(COMMA_SEPARATOR);


            ProductInfoResponse itemFromPosition = purchasedAdapter.getItemFromPosition(
                    productSelectedPosition);
            changeSelectedViews(bottomSheetPurchasedBinding.thirdRow, unparsedTag);

            int firstRowTag = Integer.parseInt(tagArray[0]);
            int secondRowTag = Integer.parseInt(tagArray[1]);
            int thirdRowTag = Integer.parseInt(tagArray[2]);

            if (firstRowTag == 0) { // service/support
                if (secondRowTag == 0) { // un authorized
                    if (thirdRowTag == 0) { // call
                        callPhoneNumber(itemFromPosition.getMobileNumber());
                        return;
                    } else if (thirdRowTag == 1) { //service request
                        AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));

                    } else if (thirdRowTag == 2) { // add
                        AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                    }
                } else if (secondRowTag == 1) { // authorized

                    if (thirdRowTag == 0) { // call
                        callPhoneNumber(itemFromPosition.getMobileNumber());
                        return;
                    } else if (thirdRowTag == 1) { //find service center
                        /*Intent serviceCenters = new Intent(getActivity(), ServiceCentersActivity.class);
                        startActivity(serviceCenters);*/
                        loadNearByServiceCenters();
                    } else if (thirdRowTag == 2) { // service center
                        loadServiceRequesDialogData();
                    } else if (thirdRowTag == 3) { // add

                    }
                }

            }

            // product
            else if (firstRowTag == 1) {
                // details
                if (secondRowTag == 0) {
                    // return policy
                    if (thirdRowTag == 0) {
                        showInformationDialog(getString(R.string.bottom_option_return_policy), itemFromPosition.getReturnPolicy());
                    }
                    // special instruction
                    else if (thirdRowTag == 1) {
                        showInformationDialog(getString(
                                R.string.bottom_option_special_instructions),
                                itemFromPosition.getSpecialInstruction());
                    }
                    //how to use
                    else if (thirdRowTag == 2) {
                        AppUtils.shortToast(getActivity(), getString(R.string.coming_soon));
                        //                showInformationDialog(itemFromPosition.getInformation());
                    }
                    //description
                    else if (thirdRowTag == 3) {
                        showInformationDialog(getString(
                                R.string.bottom_option_description), itemFromPosition.getInformation()
                                + itemFromPosition.getProductSpecification()
                                + itemFromPosition.getColor()
                                + itemFromPosition.getProductDimensions());
                        return;
                    }
                }
            }

        }

    };
    private void loadServiceRequesDialogData() {
        purchasedPresenter.getUsersListOfServiceCenters(11);
    }
    private void showServiceRequestDialog(List<UsersListOfServiceCenters> listOfServiceCenters) {
        String[] problemsArray = new String[4];
        problemsArray[0] = "Engine repaired";
        problemsArray[1] = "Need service";
        problemsArray[2] = "Power problem";
        problemsArray[3] = "Others";

        serviceRequestDialog = new ServiceRequestDialog.AlertDialogBuilder(getContext(), new ServiceRequestCallback() {
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
            public void alertDialogCallback(byte dialogStatus) {
                switch (dialogStatus) {
                    case AlertDialogCallback.OK:
                        //TODO have to call service request api
                        //purchasedPresenter.serviceRequest();

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

    private void callPhoneNumber(String phoneNumber) {
        AppUtils.callPhoneNumber(getActivity(), phoneNumber);
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
                if (!singleProductData.getStatus().equalsIgnoreCase(StatusConstants.INSTALLED) || singleProductData.getAddressId() != null) {
                    iter.remove();
                }
            }
        }

        if (productInfoResponses.size() == 0) {
            if (isFromFavorites) {
                binding.purchasedTextview.setText(getString(R.string.error_no_products_for_favorites));
            }
            binding.purchasedTextview.setVisibility(View.VISIBLE);
            dismissSwipeRefresh();
        } else {
            purchasedAdapter.setData(productInfoResponses);
            dismissSwipeRefresh();
        }

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
        // TODO  have to implemented code

    }

    @Override
    public void loadNearByServiceCenters() {
         Intent serviceCenters = new Intent(getActivity(), ServiceCentersActivity.class);

                        startActivity(serviceCenters);
    }

    @Override
    public void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> listOfServiceCenters) {
        showServiceRequestDialog(listOfServiceCenters);

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
