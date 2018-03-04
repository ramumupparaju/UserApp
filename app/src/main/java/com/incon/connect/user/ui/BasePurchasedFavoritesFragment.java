package com.incon.connect.user.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
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
import com.incon.connect.user.callbacks.ServiceRequestCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.callbacks.TimeSlotAlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.custom.view.CustomPhoneNumberDialog;
import com.incon.connect.user.custom.view.ServiceRequestDialog;
import com.incon.connect.user.custom.view.TimeSlotAlertDialog;
import com.incon.connect.user.databinding.FragmentFavoritesBinding;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.favorites.FavoritesFragment;
import com.incon.connect.user.ui.favorites.FavoritesPresenter;
import com.incon.connect.user.ui.favorites.adapter.FavoritesAdapter;
import com.incon.connect.user.ui.favorites.adapter.HorizontalRecycleViewAdapter;
import com.incon.connect.user.ui.history.base.BaseProductOptionsFragment;
import com.incon.connect.user.ui.servicecenters.ServiceCentersActivity;
import com.incon.connect.user.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by PC on 11/4/2017.
 */

public abstract class BasePurchasedFavoritesFragment extends BaseProductOptionsFragment {

    public View rootView;
    public ShimmerFrameLayout shimmerFrameLayout;

    /////////////specific to favorites fragment
    public FragmentFavoritesBinding binding;
    public FavoritesPresenter favoritesPresenter;
    public FavoritesAdapter favoritesAdapter;
    public int addressSelectedPosition = -1;
    public HorizontalRecycleViewAdapter addressessAdapter;
    /////////////////////////////////////

    public int productSelectedPosition = -1;
    public int userId;
    public Integer addressId;
    public boolean isFindServiceCenter;

    public AppEditTextDialog feedBackDialog;
    public AppEditTextDialog transferDialog;
    public AppAlertDialog detailsDialog;
    public ServiceRequestDialog serviceRequestDialog;
    public TimeSlotAlertDialog timeSlotAlertDialog;
    public ArrayList<ServiceCenterResponse> serviceCenterResponseList;
    public String serviceRequestComment;

    //Adding unauthorized phone number
    public CustomPhoneNumberDialog customPhoneNumberDialog;
    public AddServiceEngineer serviceEngineer;


    public void navigateToAddressActivity() {
        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        addressIntent.putExtra(IntentConstants.BUTTON_TEXT, getString(R.string.action_add));
        startActivityForResult(addressIntent, RequestCodes.ADDRESS_LOCATION);
    }

    // data reload
    public SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (BasePurchasedFavoritesFragment.this instanceof FavoritesFragment) {

                        getProductsApi();
                    } else {
                        //have to handle purchased
                    }
                }
            };

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

    public void showTransferDialog() {
        transferDialog = new AppEditTextDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String commentString) {
//                        buyRequestComment = commentString;
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
//                                transferDialog.dismiss(); //TODO ahve to call transfer api
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


    public void showFeedBackDialog() {
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


    public void loadServiceRequesDialogData() {
        if (serviceCenterResponseList.size() > 0) {// checking whether service centers are found or not
            loadUsersDataFromServiceCenterId(serviceCenterResponseList.get(0).getId());
        } else {
            showErrorMessage(getString(R.string.error_no_service_centers_found));
        }
    }

    public void loadUsersDataFromServiceCenterId(Integer serviceCenterId) {
        //todo have to check
        if (this instanceof FavoritesFragment) {
            favoritesPresenter.getUsersListOfServiceCenters(serviceCenterId);
        } else {
            //TODO call purchased
        }
    }

    public void loadNearByServiceCentersDialogData(String brandId) {
        //todo have to check
        if (this instanceof FavoritesFragment) {
            favoritesPresenter.nearByServiceCenters(Integer.parseInt(brandId));
        } else {
            //TODO call purchased
        }
    }


    public void showInformationDialog(String title, String messageInfo) {
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
    public void showLocationDialog() {
        ProductInfoResponse itemFromPosition = null;
        if (this instanceof FavoritesFragment) {
            itemFromPosition = favoritesAdapter.getItemFromPosition(
                    productSelectedPosition);
        } else {
            //TODO call purchased
        }


        if (TextUtils.isEmpty(itemFromPosition.getLocation())) {
            AppUtils.shortToast(getActivity(), getString(R.string.error_location));
            return;
        }
        Intent addressIntent = new Intent(getActivity(), RegistrationMapActivity.class);
        addressIntent.putExtra(IntentConstants.LOCATION_COMMA, itemFromPosition.getLocation());
        addressIntent.putExtra(IntentConstants.ADDRESS_COMMA, itemFromPosition.getAddress());
        startActivity(addressIntent);
    }

    public void addedServiceEngineer(ProductInfoResponse productInfoResponse) {
        if (customPhoneNumberDialog != null && customPhoneNumberDialog.isShowing()) {
            customPhoneNumberDialog.dismiss();
        }
        onRefreshListener.onRefresh();
        bottomSheetDialog.dismiss();
    }

    public void showCustomPhoneNumberDialog() {
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
                                    if (BasePurchasedFavoritesFragment.this instanceof FavoritesFragment) {

                                        favoritesPresenter.addServiceEngineer(serviceEngineer, userId);
                                    } else {
                                        // TODO handle purchesad
                                    }
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


    public void addedToFavorite() {

    }

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
                Integer purchaseId = Integer.valueOf(favoritesAdapter.getItemFromPosition(productSelectedPosition).getWarrantyId());
                serviceRequest.setPurchaseId(purchaseId);
                serviceRequest.setCustomerId(userId);
                favoritesPresenter.serviceRequest(serviceRequest); //TODO have to handle purchased
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

        int customStyle = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
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

    public void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> usersList) {

        if (serviceRequestDialog != null && serviceRequestDialog.isShowing()) {
            serviceRequestDialog.setUsersData(usersList);
        } else {
            showServiceRequestDialog(usersList);
        }

    }
}
