package com.incon.connect.user.custom.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.TextAddressDialogCallback;
import com.incon.connect.user.databinding.ViewAddressBinding;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.ui.RegistrationMapActivity;

/**
 * Created by PC on 11/8/2017.
 */

public class AppAddressDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    private EditText editTextName; // required
    private EditText editTextAddress; // required
    Activity mDialog;
    AddUserAddress addUserAddress;
    ViewAddressBinding addressBinding;
    private final TextAddressDialogCallback mAlertDialogCallback; // required
    private AppAddressDialog(AlertDialogBuilder builder) {
        super(builder.context);
        mDialog = (Activity) builder.context;
        this.context = builder.context;
        this.mAlertDialogCallback = (TextAddressDialogCallback) builder.callback;
    }

    public void showDialog() {
        ViewAddressBinding addressBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_address, null, false);
        addressBinding.setAppAddressDialog(AppAddressDialog.this);
        View contentView = addressBinding.getRoot();
        editTextName = addressBinding.edittextUsername;
        editTextAddress = addressBinding.edittextAddress;
        addressBinding.includeRegisterBottomButtons.buttonLeft.setText(
                context.getString(R.string.action_back));
        addressBinding.includeRegisterBottomButtons.buttonRight.setText(
                context.getString(R.string.action_next));
        addressBinding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        addressBinding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }
    public void onAddressClick() {
        Intent addressIntent = new Intent(mDialog, RegistrationMapActivity.class);
        mDialog.startActivityForResult(addressIntent, AppConstants.RequestCodes.ADDRESS_LOCATION);
    }

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mDialog.onActivityResult();

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case AppConstants.RequestCodes.ADDRESS_LOCATION:
                    addUserAddress.setAddress(data.getStringExtra(
                            AppConstants.IntentConstants.ADDRESS_COMMA));
                    addUserAddress.setLocation(data.getStringExtra(
                            AppConstants.IntentConstants.LOCATION_COMMA));
                    addressBinding.setAddUserAddress(addUserAddress);
                    break;
                default:
                    break;
            }
        }
    }*/


    @Override
    public void onClick(View view) {
        if (mAlertDialogCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.button_left:
                mAlertDialogCallback.alertDialogCallback(TextAddressDialogCallback.CANCEL);
                break;
            case R.id.button_right:
                mAlertDialogCallback.enteredTextName(editTextName.getText().toString());
                mAlertDialogCallback.enteredTextAddress(editTextAddress.getText().toString());
                mAlertDialogCallback.alertDialogCallback(TextAddressDialogCallback.OK);
                break;
            default:
                break;
        }
    }
    public static class AlertDialogBuilder {
        private final Context context;
        private final TextAddressDialogCallback callback;
        private String title;


        public AlertDialogBuilder(Context context, TextAddressDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }


        public AlertDialogBuilder title(String title) {
            this.title = title;
            return this;
        }


        //Return the finally constructed User object
        public AppAddressDialog build() {
            AppAddressDialog dialog = new AppAddressDialog(this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }


    }



}
