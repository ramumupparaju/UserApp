package com.incon.connect.user.ui.pasthistory;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.ServiceRequest;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.apimodel.components.status.StatusList;
import com.incon.connect.user.callbacks.IStatusClickCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.custom.view.AppStatusDialog;
import com.incon.connect.user.databinding.ActivityPastHistoryBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.status.adapter.adapter.ServiceStatusAdapter;

import java.util.List;

import static com.incon.connect.user.AppUtils.getStatusName;


/**
 * Created by PC on 11/15/2017.
 */

public class PastHistoryActivity extends BaseActivity {
    private ActivityPastHistoryBinding binding;
    private ServiceStatusAdapter serviceStatusAdapter;
    private List<ServiceStatus> serviceStatusList;
    private AppStatusDialog statusDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_past_history;
    }

    @Override
    protected void initializePresenter() {
    }


    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        // Make us non-modal, so that others can receive touch events.
        Window window = getWindow();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        Drawable drawable = getResources().getDrawable(R.drawable.bg_round_corners);
        InsetDrawable inset = new InsetDrawable(drawable, 0, 100, 0, 100);
        getWindow().setBackgroundDrawable(inset);


        binding = DataBindingUtil.setContentView(this, getLayoutId());
        Bundle bundle = getIntent().getExtras();
        serviceStatusList = bundle.getParcelableArrayList(BundleConstants.SERVICE_STATUS_RESPONSE);

        initViews();
    }

    private void initViews() {
        serviceStatusAdapter = new ServiceStatusAdapter(this, serviceStatusList);
        serviceStatusAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.serviceRequestsRecyclerview.setAdapter(serviceStatusAdapter);
        binding.serviceRequestsRecyclerview.setLayoutManager(linearLayoutManager);

        setListUi();
    }

    private void setListUi() {
        if (serviceStatusList.size() == 0) {
            binding.emptyData.setVisibility(View.VISIBLE);
            binding.serviceRequestsRecyclerview.setVisibility(View.GONE);
        } else {
            binding.serviceRequestsRecyclerview.setVisibility(View.VISIBLE);
            binding.emptyData.setVisibility(View.GONE);
        }
    }

    //recyclerview click event
    private IStatusClickCallback iClickCallback = new IStatusClickCallback() {
        @Override
        public void onClickStatusButton(int statusType) {
        }

        @Override
        public void onClickStatus(int productPosition, int statusPosition) {
            ServiceStatus serviceStatus = serviceStatusList.get(productPosition);

            List<StatusList> statusList = serviceStatus.getStatusList();
            StatusList status = statusList.get(statusPosition);
            ServiceRequest serviceRequest = status.getRequest();
            String phoneNumber = TextUtils.isEmpty(status.getAssignedUser().getMobileNumber()) ? status.getServiceCenter().getContactNo() : status.getAssignedUser().getMobileNumber();
            showStatusDialog(getStatusName(serviceRequest.getStatus()), AppUtils.formattedDescription(status), phoneNumber);

        }

        @Override
        public void onClickPosition(int position) {

        }
    };

    public void showStatusDialog(String title, String messageInfo, String phoneNumber) {
        statusDialog = new AppStatusDialog.AlertDialogBuilder(this, new
                TextAlertDialogCallback() {
                    @Override
                    public void enteredText(String phoneNumber) {
                        AppUtils.callPhoneNumber(PastHistoryActivity.this, phoneNumber);
                        statusDialog.dismiss();
                    }

                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case TextAlertDialogCallback.OK:
                                break;
                            default:
                                break;
                        }
                    }
                }).title(title).description(messageInfo).phoneNumber(phoneNumber)
                .build();
        statusDialog.showDialog();
        statusDialog.setCancelable(true);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

}
