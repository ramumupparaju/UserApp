package com.incon.connect.user.ui.settings.unauthorizenumbers;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertVerticalTwoButtonsDialog;
import com.incon.connect.user.databinding.ActivityUnauthorizeSeEditBinding;
import com.incon.connect.user.ui.BaseActivity;

/**
 * Created by MY HOME on 03-Mar-18.
 */

public class UnauthorizeSEEditActivity extends BaseActivity {
    private ActivityUnauthorizeSeEditBinding binding;
    private AddServiceEngineer serviceEngineer;
    private AppAlertVerticalTwoButtonsDialog deleteDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_unauthorize_se_edit;
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setActivity(this);
        initializeToolbar();

        binding.toolbar.toolbarTitleTv.setText(getString(R.string.title_menu_manage_unauthorize_numbers));

        Intent bundle = getIntent();
        if (bundle != null) {
            serviceEngineer = bundle.getParcelableExtra(IntentConstants.SERVICE_ENGINEER_DATA);
        }
        if (serviceEngineer != null) {
            binding.buttonSubmit.setText(getString(R.string.action_update));
            binding.toolbar.toolbarRightIv.setVisibility(View.VISIBLE);
        } else {
            binding.toolbar.toolbarRightIv.setVisibility(View.GONE);
            binding.buttonSubmit.setText(getString(R.string.action_submit));
            serviceEngineer = new AddServiceEngineer();
        }
        binding.setModel(serviceEngineer);

    }

    private void initializeToolbar() {
        binding.toolbar.toolbarLeftIv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.toolbar.toolbarLeftIv.setImageResource(R.drawable.ic_back_arrow);
        binding.toolbar.toolbarRightIv.setImageResource(R.drawable.ic_option_delete);
        binding.toolbar.toolbarLeftIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.toolbar.toolbarRightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSEDeleteDialog();
            }
        });

    }

    private void showSEDeleteDialog() {
        deleteDialog = new AppAlertVerticalTwoButtonsDialog.AlertDialogBuilder(this, new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                deleteDialog.dismiss();
                                break;
                            case AlertDialogCallback.CANCEL:
                                //todo have call delete api
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.dialog_delete))
                .button1Text(getString(R.string.action_cancel))
                .button2Text(getString(R.string.action_ok))
                .build();
        deleteDialog.showDialog();
        deleteDialog.setButtonBlueUnselectBackground();
        deleteDialog.setCancelable(true);
    }

    public void onSubmitClick() {

        //TODO have to api call for updation
    }
}
