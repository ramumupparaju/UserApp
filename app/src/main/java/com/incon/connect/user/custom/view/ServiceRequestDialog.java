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
import com.incon.connect.user.callbacks.ServiceRequestCallback;
import com.incon.connect.user.databinding.DialogServiceRequestBinding;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;

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
    private ServiceRequest serviceRequest;

    public ServiceRequestDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
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
//        loadDateAndTimePicker();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
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
                    if (problemSelectedPosition == 3) {
                        //TODO have to show others edit text
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


        public AlertDialogBuilder(Context context, ServiceRequestCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder problemsArray(String[] problemsArray) {
            this.problemsArray = problemsArray;
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
