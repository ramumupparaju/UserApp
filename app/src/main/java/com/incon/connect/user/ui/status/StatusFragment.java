package com.incon.connect.user.ui.status;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IStatusClickCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppEditTextDialog;
import com.incon.connect.user.databinding.FragmentStatusBinding;
import com.incon.connect.user.dto.updatestatus.UpDateStatus;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.status.adapter.adapter.ProductStatusAdapter;
import com.incon.connect.user.ui.status.adapter.adapter.ServiceStatusAdapter;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.util.ArrayList;

/**
 * Created by PC on 12/1/2017.
 */

public class StatusFragment extends BaseFragment implements StatusContract.View {
    private FragmentStatusBinding binding;
    private View rootView;
    private StatusPresenter statusPresenter;

    private ProductStatusAdapter productStatusAdapter;
    private ArrayList<ProductInfoResponse> productsList;

    private ServiceStatusAdapter serviceStatusAdapter;
    private ArrayList<ServiceStatus> serviceStatusList;

    private boolean isServiceRequest;

    private AppEditTextDialog acceptRejectApproveDialog;
    private UpDateStatus upDateStatus;

    @Override
    protected void initializePresenter() {
        statusPresenter = new StatusPresenter();
        statusPresenter.setView(this);
        setBasePresenter(statusPresenter);
    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.title_status));
    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_status,
                    container, false);
            binding.setFragment(this);
            rootView = binding.getRoot();
            initViews();
        }

        setTitle();
        return rootView;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    statusPresenter.fetchUserRequests(SharedPrefsUtils.loginProvider().getIntegerPreference
                            (LoginPrefs.USER_ID, DEFAULT_VALUE));
                }
            };


    public void onCheckedChanged(boolean checked) {
        this.isServiceRequest = checked;
        setListUi();
    }


    private void setListUi() {

        if (isServiceRequest && serviceStatusList.size() == 0) {
            binding.emptyData.setVisibility(View.VISIBLE);
        } else if (!isServiceRequest && productsList.size() == 0) {
            binding.emptyData.setVisibility(View.VISIBLE);
        } else {
            binding.emptyData.setVisibility(View.GONE);
        }
        binding.serviceRequestsRecyclerview.setVisibility(isServiceRequest ? View.VISIBLE : View.GONE);
        binding.productStatusRecyclerview.setVisibility(isServiceRequest ? View.GONE : View.VISIBLE);
    }

    private void initViews() {
        statusPresenter.fetchUserRequests(SharedPrefsUtils.loginProvider().getIntegerPreference
                (LoginPrefs.USER_ID, DEFAULT_VALUE));
        productsList = new ArrayList<>();
        serviceStatusList = new ArrayList<>();


        binding.swiperefresh.setOnRefreshListener(onRefreshListener);

        FragmentActivity activity = getActivity();
        serviceStatusAdapter = new ServiceStatusAdapter(getActivity(), serviceStatusList);
        serviceStatusAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.serviceRequestsRecyclerview.setAdapter(serviceStatusAdapter);
        binding.serviceRequestsRecyclerview.setLayoutManager(linearLayoutManager);

        productStatusAdapter = new ProductStatusAdapter(productsList);
        productStatusAdapter.setClickCallback(iClickCallback);
        linearLayoutManager = new LinearLayoutManager(activity);
        binding.productStatusRecyclerview.setAdapter(productStatusAdapter);
        binding.productStatusRecyclerview.setLayoutManager(linearLayoutManager);

        setListUi();
    }

    private void showApprovalDialog(final int statusType) {
        if (upDateStatus == null) {
            upDateStatus = new UpDateStatus();
        }
        String title;
        if (statusType == StatusConstants.ACCEPTED) {
            title = getString(R.string.action_accept);
        } else if (statusType == StatusConstants.REJECTED) {
            title = getString(R.string.action_reject);
        } else {
            title = getString(R.string.action_approval);
        }

        acceptRejectApproveDialog = new AppEditTextDialog.AlertDialogBuilder(getActivity(), new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String commentString) {
                        upDateStatus.setComments(commentString);
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                statusPresenter.upDateStatus(SharedPrefsUtils.loginProvider().getIntegerPreference(LoginPrefs.USER_ID, DEFAULT_VALUE), upDateStatus);
                                break;
                            case AlertDialogCallback.CANCEL:
                                acceptRejectApproveDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(title)
                .leftButtonText(getString(R.string.action_cancel))
                .rightButtonText(getString(R.string.action_submit))
                .build();
        acceptRejectApproveDialog.showDialog();
        acceptRejectApproveDialog.setCancelable(true);
    }

    //recyclerview click event
    private IStatusClickCallback iClickCallback = new IStatusClickCallback() {
        @Override
        public void onClickStatusButton(int statusType) {
            showApprovalDialog(statusType);
        }

        @Override
        public void onClickPosition(int position) {
            //TODO have to enable
            if (isServiceRequest) {
            } else {
               /* AddUser usersListOfServiceCenters = productsList.get(position);
                Intent intent = new Intent(AllUsersDesignationsActivity.this, AddUserActivity.class);
                intent.putParcelableArrayListExtra(IntentConstants.DESIGNATION_DATA, serviceStatusList);
                intent.putExtra(IntentConstants.USER_DATA, usersListOfServiceCenters);
                intent.putParcelableArrayListExtra(IntentConstants.USER_DATA_LIST, productsList);
                startActivityForResult(intent, RequestCodes.ADD_USER_DESIGNATION);*/
            }
        }
    };


    @Override
    public void loadServiceRequests(ArrayList<ProductInfoResponse> productStatusArrayList,
                                    ArrayList<ServiceStatus> serviceStatusArrayList) {
        if (productStatusArrayList == null) {
            productStatusArrayList = new ArrayList<>();
        }
        if (serviceStatusArrayList == null) {
            serviceStatusArrayList = new ArrayList<>();
        }

        this.productsList = productStatusArrayList;
        this.serviceStatusList = serviceStatusArrayList;

        productStatusAdapter.setData(productStatusArrayList);
        serviceStatusAdapter.setData(serviceStatusArrayList);

        dismissSwipeRefresh();

        setListUi();
    }


    private void dismissSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing()) {
            binding.swiperefresh.setRefreshing(false);
        }
    }
    @Override
    public void statusUpdated() {
        if (acceptRejectApproveDialog != null && acceptRejectApproveDialog.isShowing()) {
            acceptRejectApproveDialog.dismiss();
        }

        statusPresenter.fetchUserRequests(SharedPrefsUtils.loginProvider().getIntegerPreference
                (LoginPrefs.USER_ID, DEFAULT_VALUE));
    }
}





