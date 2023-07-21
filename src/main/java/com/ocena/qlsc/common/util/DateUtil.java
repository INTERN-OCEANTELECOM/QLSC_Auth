package com.ocena.qlsc.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static String convertObjectToDateFormat(Object obj) {
        if(obj == null)
            return "null";
        if(obj instanceof Long && (Long) obj > 900000000000L) {
            LocalDate date = Instant.ofEpochMilli((Long) obj).atZone(ZoneId.systemDefault()).toLocalDate();
            return date.format(formatter);
        }
        return obj.toString();
    }
}
