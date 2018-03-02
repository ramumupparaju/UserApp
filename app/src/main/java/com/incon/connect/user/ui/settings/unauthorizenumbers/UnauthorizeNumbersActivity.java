package com.incon.connect.user.ui.settings.unauthorizenumbers;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.FeedbackData;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.custom.view.AppAlertVerticalTwoButtonsDialog;
import com.incon.connect.user.databinding.ActivityUnauthorizenumbersBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.settings.unauthorizenumbers.adapter.UnauthorizeSeNumbersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY HOME on 02-Mar-18.
 */

public class UnauthorizeNumbersActivity extends BaseActivity {
    private UnauthorizeSeNumbersAdapter unauthorizeNumbersAdapter;

    private ActivityUnauthorizenumbersBinding binding;
    private AppAlertVerticalTwoButtonsDialog deleteDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_unauthorizenumbers;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setActivity(this);

        initViews();
        initializeToolbar();

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
                showDeleteDialog();
            }
        });
    }

    private void showDeleteDialog() {
        deleteDialog = new AppAlertVerticalTwoButtonsDialog.AlertDialogBuilder(this, new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                deleteDialog.dismiss();
                                break;
                            case AlertDialogCallback.CANCEL:
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

    private void initViews() {
        List<FeedbackData> feedbackData = new ArrayList<>();
        feedbackData.add(new FeedbackData("test1", "7779992501"));
        feedbackData.add(new FeedbackData("test2", "9638527412"));
        feedbackData.add(new FeedbackData("test3", "7799050905"));

        unauthorizeNumbersAdapter = new UnauthorizeSeNumbersAdapter(feedbackData);
        binding.recyclerviewUnauthorize.setAdapter(unauthorizeNumbersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        Context context = binding.getRoot().getContext();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_divider));
        binding.recyclerviewUnauthorize.addItemDecoration(dividerItemDecoration);
        binding.recyclerviewUnauthorize.setLayoutManager(linearLayoutManager);
    }

    //recyclerview click event
    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
        }
    };
}
