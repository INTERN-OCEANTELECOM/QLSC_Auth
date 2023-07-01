package com.ocena.qlsc.podetail.status;

import java.util.*;

public final class RegexConstants {
    public static final String regexRepairCategory = "(?i)\\s*H(?:[ẠA]|\\p{L})NG\\s*M(?:[ỤU]|\\p{L})C\\s*";

    public static final String regexSTT = "(?i)\\s*STT\\s*";

    public static final String regexSerialNumber = "(?i)\\s*S(?:[ỐÔÔ]|\\p{L})\\s*SERIAL\\s*H(?:[ỎO]|\\p{L})NG\\s*";

    public static final String regexProduct = "(?i)\\s*M(?:[ÃA]|\\p{L})\\s*HH\\s*";

    public static final String regexPo = "(?i)\\s*S(?:[ỐÔO]|\\p{L})\\s*PO\\s*";

    public static final String regexBbbgNumber = "(?i)\\s*S(?:[ỐÔO]|\\p{L})\\s*BBBG\\s*";

    public static final String regexImportDate = "(?i)\\s*NG(?:[ÀA]|\\p{L})Y\\s*NH(?:[ÂAẬ]|\\p{L})P\\s*";

    public static final String regexRepairStatus = "(?i)\\s*TR(?:[ẠA]|\\p{L})NG\\s*TH(?:[ÁA]|\\p{L})I\\s*SC\\s*";

    public static final String regexProductName = "(?i)\\s*T(?:[ÊE]|\\p{L})N\\s*THI(?:[ÊEẾ]|\\p{L})T\\s*B(?:[ỊI]|\\p{L})\\s*";

    public static final String regexExportPartner = "(?i)\\s*XU(?:[ÂAẤ]|\\p{L})T\\s*kho\\s*tr(?:[ảa]|\\p{L})\\s*kh\\s*";

    public static final String regexKcsVT = "(?i)\\s*KSC\\s*VT\\s*";

    public static final String regexWarrantyPeriod = "(?i)\\s*B(?:[AẢ]|\\p{L})O\\s*H(?:[AÀ]|\\p{L})NH\\s*";

    public static final HashMap<Integer, String> importPOHeader = new HashMap<>() {
        {
            put(0, regexSTT);
            put(1, regexProduct);
            put(2, regexSerialNumber);
            put(3, regexPo);
            put(4, regexBbbgNumber);
            put(5, regexImportDate);
            put(6, regexRepairCategory);
        }
    };

    public static final HashMap<Integer, String> repairStatusMap = new HashMap<>() {
        {
            put(0, regexSTT);
            put(1, regexProduct);
            put(2, regexSerialNumber);
            put(3, regexPo);
            put(4, regexRepairStatus);
        }
    };

    public static final HashMap<Integer, String> exportPartnerMap = new HashMap<>() {
        {
            put(0, regexSTT);
            put(1, regexProduct);
            put(2, regexSerialNumber);
            put(3, regexPo);
            put(4, regexExportPartner);
        }
    };

    public static final HashMap<Integer, String> kcsVTMap = new HashMap<>() {
        {
            put(0, regexSTT);
            put(1, regexProduct);
            put(2, regexSerialNumber);
            put(3, regexPo);
            put(4, regexKcsVT);
        }
    };

    public static final HashMap<Integer, String> warrantyPeriodMap = new HashMap<>() {
        {
            put(0, regexSTT);
            put(1, regexProduct);
            put(2, regexSerialNumber);
            put(3, regexPo);
            put(4, regexWarrantyPeriod);
        }
    };


    public static final HashMap<Integer, String> importProduct = new HashMap<>() {
        {
            put(0, regexProduct);
            put(1, regexProductName);
        }
    };
}