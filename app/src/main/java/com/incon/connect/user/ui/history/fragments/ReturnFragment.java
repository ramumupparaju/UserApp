package com.incon.connect.user.ui.history.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
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
import com.incon.connect.user.apimodel.components.history.purchased.ReturnHistoryResponse;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.custom.view.AppAlertDialog;
import com.incon.connect.user.databinding.BottomSheetReturnBinding;
import com.incon.connect.user.databinding.CustomBottomViewBinding;
import com.incon.connect.user.databinding.FragmentReturnBinding;
import com.incon.connect.user.ui.RegistrationMapActivity;
import com.incon.connect.user.ui.history.adapter.ReturnAdapter;
import com.incon.connect.user.ui.history.base.BaseTabFragment;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 10/2/2017.
 */

public class ReturnFragment extends BaseTabFragment implements ReturnContract.View {
    private View rootView;
    private FragmentReturnBinding binding;
    private ReturnPresenter returnPresenter;
    private ReturnAdapter returnAdapter;
    private int userId;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetReturnBinding bottomSheetReturnBinding;
    private int productSelectedPosition;
    private AppAlertDialog detailsDialog;

    @Override
    protected void initializePresenter() {
        returnPresenter = new ReturnPresenter();
        returnPresenter.setView(this);
        setBasePresenter(returnPresenter);

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
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_return,
                    container, false);
            initViews();
           // loadBottomSheet();

            rootView = binding.getRoot();
        }
        setTitle();
        return rootView;
    }

    private void loadBottomSheet() {

        bottomSheetReturnBinding = DataBindingUtil.inflate(LayoutInflater.from(
                getActivity()), R.layout.bottom_sheet_return, null, false);

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(bottomSheetReturnBinding.getRoot());
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                returnAdapter.clearSelection();
            }
        });


    }

    private void initViews() {

        binding.swiperefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swiperefresh.setOnRefreshListener(onRefreshListener);
        returnAdapter = new ReturnAdapter();
        returnAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), linearLayoutManager.getOrientation());
        binding.returnRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.returnRecyclerview.setAdapter(returnAdapter);
        binding.returnRecyclerview.setLayoutManager(linearLayoutManager);

        userId = SharedPrefsUtils.loginProvider().getIntegerPreference(
                LoginPrefs.USER_ID, DEFAULT_VALUE);
        returnPresenter.returnHistory(userId);

    }

    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }

    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
           // createBottomSheetView(position);
            bottomSheetDialog.show();
        }
    };

    private void createBottomSheetView(int position) {
        productSelectedPosition = position;
        bottomSheetReturnBinding.topRow.setVisibility(View.GONE);
        String[] bottomNames = new String[3];
        bottomNames[0] = "Customer";
        bottomNames[1] = "Product";
        bottomNames[2] = "Showroom";

        int[] bottomDrawables = new int[3];
        bottomDrawables[0] = R.drawable.ic_option_customer;
        bottomDrawables[1] = R.drawable.ic_option_product;
        bottomDrawables[2] = R.drawable.ic_option_service_support;
        bottomSheetReturnBinding.bottomRow.removeAllViews();
        int length = bottomNames.length;
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.MATCH_PARENT, length);
//        params.setMargins(1, 1, 1, 1);
//TODO have to remove hard codeings
        for (int i = 0; i < length; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setWeightSum(1f);
            linearLayout.setGravity(Gravity.CENTER);
            CustomBottomViewBinding customBottomView = getCustomBottomView();
            customBottomView.viewTv.setText(bottomNames[i]);
            customBottomView.viewLogo.setImageResource(bottomDrawables[i]);
            View bottomRootView = customBottomView.getRoot();
            bottomRootView.setTag(i);
            linearLayout.setTag(i);
            linearLayout.addView(bottomRootView);
            linearLayout.setOnClickListener(bottomViewClickListener);
            bottomSheetReturnBinding.bottomRow.addView(linearLayout, params);
        }
    }


    private View.OnClickListener bottomViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer tag = (Integer) view.getTag();
            String[] bottomOptions;
            int[] topDrawables;
            changeBackgroundText(tag, view);

            if (tag == 0) {
                bottomOptions = new String[2];
                bottomOptions[0] = "Call";
                bottomOptions[1] = "Location";

                topDrawables = new int[2];
                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_location;
            } else if (tag == 1) {
                bottomOptions = new String[4];
                bottomOptions[0] = "Details";
                bottomOptions[1] = "Price";
                bottomOptions[2] = "Warranty";
                bottomOptions[3] = "Return policy";

                topDrawables = new int[4];
                topDrawables[0] = R.drawable.ic_option_details;
                topDrawables[1] = R.drawable.ic_option_warranty;
                topDrawables[2] = R.drawable.ic_option_warranty;
                topDrawables[3] = R.drawable.ic_option_warranty;
            } else if (tag == 2) {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                /*bottomOptions = new String[3];
                topDrawables = new int[3];
                bottomOptions[0] = "Call";
                bottomOptions[1] = "Request Installation";
                bottomOptions[2] = "Share Customer Location";


                topDrawables[0] = R.drawable.ic_option_call;
                topDrawables[1] = R.drawable.ic_option_accept_request;
                topDrawables[2] = R.drawable.ic_option_location;*/
            } else {
                bottomOptions = new String[0];
                topDrawables = new int[0];
                /*bottomOptions = new String[4];
                topDrawables = new int[4];
                bottomOptions[0] = "Dispatches On";
                bottomOptions[1] = "Dispatched";
                bottomOptions[2] = "Delivered";
                bottomOptions[3] = "Installed";


                topDrawables[0] = R.drawable.ic_option_delivery_status;
                topDrawables[1] = R.drawable.ic_option_delivery_status;
                topDrawables[2] = R.drawable.ic_option_delivery_status;
                topDrawables[3] = R.drawable.ic_option_delivery_status;*/
            }
            bottomSheetReturnBinding.topRow.removeAllViews();
            int length1 = bottomOptions.length;
            bottomSheetReturnBinding.topRow.setVisibility(View.VISIBLE);
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
                linearLayout.addView(topRootView);
                linearLayout.setOnClickListener(topViewClickListener);
                bottomSheetReturnBinding.topRow.addView(linearLayout, params);
            }
        }
    };

    private void onOpenAlert(String messageInfo) {
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
                .button2Text(getString(R.string.action_cancel))
                .build();
        detailsDialog.showDialog();
    }

    private void changeBackgroundText(Integer tag, View view) {

        for (int i = 0; i < bottomSheetReturnBinding.bottomRow.getChildCount(); i++) {
//            if (view.equals(bottomSheetInterestBinding.bottomRow.getChildAt(i))) {
            LinearLayout childAt = (LinearLayout) bottomSheetReturnBinding.
                    bottomRow.getChildAt(i);
            if (view == childAt) {
                childAt.setBackgroundColor(
                        ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            } else {
                childAt.setBackgroundColor(
                        ContextCompat.getColor(getActivity(), R.color.white));
            }
        }
    }

    private View.OnClickListener topViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewById = (TextView) view.findViewById(R.id.view_tv);
            String topClickedText = viewById.getText().toString();
            showErrorMessage(topClickedText);
            Integer tag = (Integer) view.getTag();
            ReturnHistoryResponse itemFromPosition = returnAdapter.getItemFromPosition(
                    productSelectedPosition);
            if (tag == 0 && topClickedText.equals("Call")) {
                callPhoneNumber(itemFromPosition.getMobileNumber());
            } else if (tag == 1 && topClickedText.equals("Location")) {
                showLocationDialog();
            } else if (tag == 0 && topClickedText.equals("Details")) {
                onOpenAlert(itemFromPosition.getInformation());
            }  else if (tag == 1 && topClickedText.equals("price")) {
                onOpenAlert(itemFromPosition.getPrice());
            }

            else if (tag == 2 && topClickedText.equals("Warranty")) {
                /*String dateString = getString(R.string.hint_warranty_date,
                        DateUtils.convertMillisToStringFormat(
                                Long.parseLong(itemFromPosition.getWarrantyEndDate()),
                                DateFormatterConstants.DD_E_MMMM_YYYY));
                onOpenAlert(dateString);*/
            }
            else if (tag == 3 && topClickedText.equals("Return policy")) {
                /*String dateString = getString(R.string.hint_warranty_date,
                        DateUtils.convertMillisToStringFormat(
                                Long.parseLong(itemFromPosition.getWarrantyEndDate()),
                                DateFormatterConstants.DD_E_MMMM_YYYY));
                onOpenAlert(dateString);*/
            }


        }
    };

    private void showLocationDialog() {
        ReturnHistoryResponse itemFromPosition = returnAdapter.getItemFromPosition(
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

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    returnAdapter.clearData();
                    returnPresenter.returnHistory(userId);

                }
            };


    @Override
    public void loadReturnHistory(List<ReturnHistoryResponse> returnHistoryResponseList) {
        if (returnHistoryResponseList == null) {
            returnHistoryResponseList = new ArrayList<>();
        }
        returnAdapter.setData(returnHistoryResponseList);
        dismissSwipeRefresh();

    }

    @Override
    public void onSearchClickListerner(String searchableText, String searchType) {
        AppUtils.hideSoftKeyboard(getActivity(), rootView);
        returnAdapter.searchData(searchableText, searchType);

    }
}
