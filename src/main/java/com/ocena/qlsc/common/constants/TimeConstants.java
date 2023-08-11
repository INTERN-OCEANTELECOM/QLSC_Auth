package com.ocena.qlsc.common.constants;

public interface TimeConstants {
    public static final int LOGIN_ATTEMPTS = 4;
    public static final int LOCK_TIME = 60;
    public static final int PO_UPDATE_TIME = 86400000; // 24 hours
    public static final Long REPAIR_HISTORY_UPDATE_TIME = 2592000000L; // 7 days
    public static final String MESSAGE_SUCCESS_OTP = "OTP Has Been Sent!!!";
}