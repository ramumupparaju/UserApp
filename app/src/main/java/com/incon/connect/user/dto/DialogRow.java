package com.incon.connect.user.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;

/**
 * Created by PC on 1/5/2018.
 */

public class DialogRow {
    private String leftTv;
    private String rightTv;

    public DialogRow(String leftTv, String rightTv) {
        this.leftTv = leftTv;
        this.rightTv = rightTv;
    }

    public String getLeftTv() {
        return leftTv;
    }

    public void setLeftTv(String leftTv) {
        this.leftTv = leftTv;
    }

    public String getRightTv() {
        return rightTv;
    }

    public void setRightTv(String rightTv) {
        this.rightTv = rightTv;
    }
}