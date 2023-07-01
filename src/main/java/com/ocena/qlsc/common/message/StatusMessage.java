package com.ocena.qlsc.common.message;

public interface StatusMessage {
    public static final String REQUEST_SUCCESS = "REQUEST SUCCESS";

    public static final String DATA_NOT_FOUND = "REQUEST SUCCESS! NOT FOUND ANY DATA";

    public static final String DATA_NOT_MAP = "REQUEST FAILURE! DATA NOT MAPPING";

    public static final String LOCK_ACCESS = "YOUR ACCOUNT IS TEMPORARILY LOCKED";

    public static final String NOT_IMPLEMENTED = "CANNOT ACCEPT THE REQUIRED ACTION";

    public static final String ACCESS_DENIED = "YOUR ACCOUNT DOES NOT HAVE PERMISSION";
}
