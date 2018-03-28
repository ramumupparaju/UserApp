package com.incon.connect.user.callbacks;

public interface AlertDialogCallback {
    byte OK = 0;
    byte CANCEL = 1;
    byte EXTENDED_WARRANTY = 2;

    /**
     * Callback is used to identify which button is tapped in alert dialog box,
     * whether it is OK or CANCEL.
     *
     * @param dialogStatus button that is clicked
     */
    void alertDialogCallback(byte dialogStatus);
}
