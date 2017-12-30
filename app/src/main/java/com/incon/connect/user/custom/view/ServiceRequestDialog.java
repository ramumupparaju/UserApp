package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.callbacks.ServiceRequestCallback;
import com.incon.connect.user.databinding.DialogServiceRequestBinding;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;

import java.util.List;

/**
 * Created by PC on 12/26/2017.
 */

public class ServiceRequestDialog extends Dialog implements View.OnClickListener {
    private DialogServiceRequestBinding binding;
    private final Context context;
    private final ServiceRequestCallback serviceRequestCallback;
    private EditText editTextNotes;
    private String[] problemsArray;
    private int problemSelectedPosition = 0;
    private int usersSelectedPos = -1;
    private ServiceRequest serviceRequest;
    private final List<UsersListOfServiceCenters> usersList;

    public ServiceRequestDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.usersList = builder.usersList;
        this.problemsArray = builder.problemsArray;
        this.serviceRequestCallback = builder.callback;
    }

    public void showDialog() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_service_request, null, false);
        View contentView = binding.getRoot();
        editTextNotes = binding.edittextComment;
        binding.includeRegisterBottomButtons.buttonLeft.setText(context.getString(R.string.action_cancel));
        binding.includeRegisterBottomButtons.buttonRight.setText(context.getString(R.string.action_submit));
        binding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        binding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);
        binding.viewDate.setOnClickListener(this);
        binding.viewTime.setOnClickListener(this);
        loadProblemSpinner();
        loadUsersSpinner();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }

    private void loadUsersSpinner() {
        String[] stringUsersList = new String[usersList.size()];
        for (int i = 0; i < usersList.size(); i++) {
            stringUsersList[i] = usersList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.view_spinner, stringUsersList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerUsers.setAdapter(arrayAdapter);
        binding.spinnerUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (usersSelectedPos != position) {
                    usersSelectedPos = position;
                }
            }
        });

    }

    private void loadProblemSpinner() {
        Context context = binding.getRoot().getContext();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                R.layout.view_spinner, problemsArray);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerProblem.setAdapter(arrayAdapter);
        binding.spinnerProblem.setText(problemsArray[0]);
        binding.edittextOthersComments.setVisibility(View.GONE);

        binding.spinnerProblem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (problemSelectedPosition != position) {
                    problemSelectedPosition = position;
                    if (problemSelectedPosition == 3) { //todo
                        binding.edittextOthersComments.setVisibility(View.VISIBLE);
                    } else {
                        binding.edittextOthersComments.setVisibility(View.GONE);
                    }
                }

                //For avoiding double tapping issue
                if (binding.spinnerProblem.getOnItemClickListener() != null) {
                    binding.spinnerProblem.onItemClick(parent, view, position, id);
                }
            }
        });
    }

    public void setDateFromPicker(String dobInDD_mm_yyyy) {
        binding.edittextDate.setText(dobInDD_mm_yyyy);
    }

    public void setTimeFromPicker(String timeSlot) {
        binding.edittextTime.setText(timeSlot);
    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final ServiceRequestCallback callback;
        private String[] problemsArray;
        private List<UsersListOfServiceCenters> usersList;


        public AlertDialogBuilder(Context context, ServiceRequestCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder problemsArray(String[] problemsArray) {
            this.problemsArray = problemsArray;
            return this;
        }

        public AlertDialogBuilder loadUsersList(List<UsersListOfServiceCenters> usersList) {
            this.usersList = usersList;
            return this;

        }

        public ServiceRequestDialog build() {
            ServiceRequestDialog dialog = new ServiceRequestDialog(this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }
    }


    @Override
    public void onClick(View view) {
        if (serviceRequestCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.view_date:
                serviceRequestCallback.dateClicked("");
                break;
            case R.id.view_time:
                serviceRequestCallback.timeClicked();
                break;
            case R.id.button_left:
                serviceRequestCallback.alertDialogCallback(ServiceRequestCallback.CANCEL);
                break;
            case R.id.button_right:
                serviceRequestCallback.alertDialogCallback(ServiceRequestCallback.OK);
                break;
            default:
                break;
        }

    }
}
