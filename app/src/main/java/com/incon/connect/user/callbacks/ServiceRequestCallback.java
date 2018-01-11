package com.incon.connect.user.callbacks;

import com.incon.connect.user.dto.servicerequest.ServiceRequest;

/**
 * Created by PC on 12/26/2017.
 */

public interface ServiceRequestCallback extends AlertDialogCallback {

    void getUsersListFromServiceCenterId(int serviceCenterId);
    void dateClicked(String date);
    void timeClicked();
    void enteredText(String comment);

    void doServiceRequestApi(ServiceRequest serviceRequest);
}
