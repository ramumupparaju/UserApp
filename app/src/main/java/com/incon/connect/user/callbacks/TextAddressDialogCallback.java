package com.incon.connect.user.callbacks;

/**
 * Created by PC on 11/8/2017.
 */

public interface TextAddressDialogCallback extends AlertDialogCallback {

    void enteredTextName(String nameString);
    void enteredTextAddress(String addressString);
}
