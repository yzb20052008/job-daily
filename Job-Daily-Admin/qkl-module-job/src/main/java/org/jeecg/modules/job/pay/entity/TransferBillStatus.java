package org.jeecg.modules.job.pay.entity;

import com.google.gson.annotations.SerializedName;

public enum TransferBillStatus {

    @SerializedName("ACCEPTED")
    ACCEPTED,
    @SerializedName("PROCESSING")
    PROCESSING,
    @SerializedName("WAIT_USER_CONFIRM")
    WAIT_USER_CONFIRM,
    @SerializedName("TRANSFERING")
    TRANSFERING,
    @SerializedName("SUCCESS")
    SUCCESS,
    @SerializedName("FAIL")
    FAIL,
    @SerializedName("CANCELING")
    CANCELING,
    @SerializedName("CANCELLED")
    CANCELLED
}
