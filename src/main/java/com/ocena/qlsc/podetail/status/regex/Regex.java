package com.ocena.qlsc.podetail.status.regex;

import jakarta.persistence.spi.PersistenceUnitTransactionType;

import java.util.*;

public interface Regex {
    public static final String regexRepairCategory = "(?i)\\s*H(?:[ẠA]|\\p{L})NG\\s*M(?:[ỤU]|\\p{L})C\\s*";

    public static final String regexSTT = "(?i)\\s*STT\\s*";

    public static final String regexSerialNumber = "(?i)\\s*S(?:[ỐÔÔ]|\\p{L})\\s*SERIAL\\s*H(?:[ỎO]|\\p{L})NG\\s*";

    public static final String regexProduct = "(?i)\\s*M(?:[ÃA]|\\p{L})\\s*HH\\s*";

    public static final String regexPo = "(?i)\\s*S(?:[ỐÔO]|\\p{L})\\s*PO\\s*";

    public static final String regexBbbgNumber = "(?i)\\s*S(?:[ỐÔO]|\\p{L})\\s*BBBG\\s*";

    public static final String regexImportDate = "(?i)\\s*NG(?:[ÀA]|\\p{L})Y\\s*NH(?:[ÂAẬ]|\\p{L})P\\s*";

    public static final String regexRepairStatus = "(?i)\\s*TR(?:[ẠA]|\\p{L})NG\\s*TH(?:[ÁA]|\\p{L})I\\s*SC\\s*";

    public static final String regexProductName = "(?i)\\s*T(?:[ÊE]|\\p{L})N\\s*THI(?:[ÊEẾ]|\\p{L})T\\s*B(?:[ỊI]|\\p{L})\\s*";


    public final HashMap<Integer, String> importPOHeader = new HashMap<>() {
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

    public final HashMap<Integer, String> importPORepairStatus = new HashMap<>() {
        {
            put(0, regexSTT);
            put(1, regexProduct);
            put(2, regexSerialNumber);
            put(3, regexPo);
            put(4, regexRepairStatus);
        }
    };

    public final HashMap<Integer, String> importProduct = new HashMap<>() {
        {
            put(0, regexProduct);
            put(1, regexProductName);
        }
    };
}
