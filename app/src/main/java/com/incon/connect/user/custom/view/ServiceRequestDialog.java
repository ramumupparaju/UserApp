package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.callbacks.ServiceRequestCallback;
import com.incon.connect.user.databinding.DialogServiceRequestBinding;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import static com.incon.connect.user.AppConstants.COMMA_SEPARATOR;

/**
 * Created by PC on 12/26/2017.
 */

public class ServiceRequestDialog extends Dialog implements View.OnClickListener {
    private DialogServiceRequestBinding binding;
    private final Context context;
    private final ServiceRequestCallback serviceRequestCallback;
    private EditText editTextNotes;
    private ServiceRequest serviceRequest;

    private final List<ServiceCenterResponse> serviceCentersList;
    private final List<UsersListOfServiceCenters> usersList;
    private String[] problemsArray;
    private int serviceCenterSelectedPos = 0;
    private int usersSelectedPos = 0;
    private int problemSelectedPosition = 0;


    public ServiceRequestDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.usersList = builder.usersList;
        this.problemsArray = builder.problemsArray;
        this.serviceRequestCallback = builder.callback;
        this.serviceCentersList = builder.serviceCenterResponseList;
        serviceRequest = new ServiceRequest();
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
        loadServiceCenterSpinner();
        loadUsersSpinner();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }

    private void loadServiceCenterSpinner() {
        String[] stringServiceCentersList = new String[serviceCentersList.size()];
        for (int i = 0; i < serviceCentersList.size(); i++) {
            stringServiceCentersList[i] = serviceCentersList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.view_spinner, stringServiceCentersList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerService.setAdapter(arrayAdapter);
        binding.spinnerService.setText(stringServiceCentersList[0]); //setting user name with index o
        binding.spinnerService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (serviceCenterSelectedPos != position) {
                    serviceCenterSelectedPos = position;
                    serviceRequestCallback.getUsersListFromServiceCenterId(serviceCentersList.get(serviceCenterSelectedPos).getId());
                }

                //For avoiding double tapping issue
                if (binding.spinnerService.getOnItemClickListener() != null) {
                    binding.spinnerService.onItemClick(parent, view, position, id);
                }
            }
        });
    }

    private void loadUsersSpinner() {
        if (usersList.size() == 0) {
            binding.spinnerUsers.setVisibility(View.GONE);
            return;
        }
        binding.spinnerUsers.setVisibility(View.VISIBLE);
        String[] stringUsersList = new String[usersList.size()];
        for (int i = 0; i < usersList.size(); i++) {
            stringUsersList[i] = usersList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.view_spinner, stringUsersList);
        arrayAdapter.setDropDownViewResource(R.layout.view_spinner);
        binding.spinnerUsers.setAdapter(arrayAdapter);
        binding.spinnerUsers.setText(stringUsersList[0]); //setting user name with index o
        binding.spinnerUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (usersSelectedPos != position) {
                    usersSelectedPos = position;
                }

                //For avoiding double tapping issue
                if (binding.spinnerUsers.getOnItemClickListener() != null) {
                    binding.spinnerUsers.onItemClick(parent, view, position, id);
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

    public void setUsersData(List<UsersListOfServiceCenters> usersList) {
        this.usersList.clear();
        usersSelectedPos = 0;
        this.usersList.addAll(usersList);
        loadUsersSpinner();
    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final ServiceRequestCallback callback;
        private String[] problemsArray;
        private List<UsersListOfServiceCenters> usersList;
        private ArrayList<ServiceCenterResponse> serviceCenterResponseList;

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

        public AlertDialogBuilder loadServiceCentersData(ArrayList<ServiceCenterResponse> serviceCenterResponseList) {
            this.serviceCenterResponseList = serviceCenterResponseList;
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
                if (validateFields()) {
                    serviceRequestCallback.doServiceRequestApi(serviceRequest);
                    serviceRequestCallback.alertDialogCallback(ServiceRequestCallback.OK);
                }
                break;
            default:
                break;
        }

    }

    private boolean validateFields() {

        String selectedTime = binding.edittextTime.getText().toString();
        String selectedDate = binding.edittextDate.getText().toString();
        int selectedPriority = binding.radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedPriority);

        //no need to check users data
        if (serviceCenterSelectedPos == -1) {
            //todo show toast as select users
            AppUtils.shortToast(getContext(), context.getString(R.string.user_selecte));
            return false;
        } else if (TextUtils.isEmpty(selectedDate)) {
            // todo show toast as select date
             AppUtils.shortToast(getContext(), context.getString(R.string.date_selecte));

            return false;

        } else if (TextUtils.isEmpty(selectedTime)) {
            AppUtils.shortToast(getContext(), context.getString(R.string.time_selecte));

            return false;
        } else {

            String[] selectedTimeArray = new String[0];
            if (selectedTime.equalsIgnoreCase(context.getString(R.string.time_10_12))) {
                selectedTimeArray = AppConstants.ServiceConstants.TIME_10_12.split(COMMA_SEPARATOR);
            } else if (selectedTime.equalsIgnoreCase(context.getString(R.string.time_12_15))) {
                selectedTimeArray = AppConstants.ServiceConstants.TIME_12_15.split(COMMA_SEPARATOR);
            } else if (selectedTime.equalsIgnoreCase(context.getString(R.string.time_15_17))) {
                selectedTimeArray = AppConstants.ServiceConstants.TIME_15_17.split(COMMA_SEPARATOR);
            }
            serviceRequest.setServiceCenterId(serviceCentersList.get(serviceCenterSelectedPos).getId());
            if (usersSelectedPos != -1) {
                serviceRequest.setPreferredUser(usersList.get(usersSelectedPos).getId());
            }
            String dateFromString = selectedDate + " " + selectedTimeArray[0];
            String dateToString = selectedDate + " " + selectedTimeArray[1];
            serviceRequest.setComplaint(binding.spinnerProblem.getText().toString());
            serviceRequest.setComments(binding.edittextComment.getText().toString());
            serviceRequest.setPriority(Integer.valueOf(radioButton.getTag().toString()));
            serviceRequest.setPreferredDateFrom(String.valueOf(DateUtils.convertStringFormatToMillis(dateFromString, AppConstants.DateFormatterConstants.LOCAL_DATE_DD_MM_YYYY_HH_MM)));
            serviceRequest.setPreferredDateTo(String.valueOf(DateUtils.convertStringFormatToMillis(dateToString, AppConstants.DateFormatterConstants.LOCAL_DATE_DD_MM_YYYY_HH_MM)));
            return true;

        }
    }
}
