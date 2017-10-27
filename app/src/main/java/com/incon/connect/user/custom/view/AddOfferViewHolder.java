package com.incon.connect.user.custom.view;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by PC on 9/28/2017.
 */

public class AddOfferViewHolder {
    private CheckBox checkBox;
    private TextView textView;

    public AddOfferViewHolder() {
    }

    public AddOfferViewHolder(TextView textView, CheckBox checkBox) {
        this.checkBox = checkBox;
        this.textView = textView;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
