package com.ocena.qlsc.common.util;

import com.ocena.qlsc.podetail.enums.KSCVT;
import com.ocena.qlsc.podetail.enums.Priority;
import com.ocena.qlsc.podetail.enums.RepairCategory;
import com.ocena.qlsc.podetail.enums.RepairStatus;
import jakarta.validation.constraints.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
    public static @NotNull Date getNowDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getCurrentDateByMMYYYY() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
        return dateFormat.format(getNowDate());
    }

    public static String getCurrentDateByDDMMYYYYhhmm() {
        // Định dạng ngày giờ thành chuỗi "dd/MM/yyyy hh:mm:ss"
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm");
        return dateFormat.format(getNowDate());
    }

    public static String convertObjectToDateFormat(Object obj, String name){
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if(obj == null)
            return "N/A";
        if(obj instanceof Long && (Long) obj > 900000000000L) {
            LocalDate date = Instant.ofEpochMilli((Long) obj).atZone(ZoneId.systemDefault()).toLocalDate();
            return date.format(formatter);
        }

        Map<String, Enum[]> enumMap = new HashMap<>();
        enumMap.put("repairCategory", RepairCategory.values());
        enumMap.put("repairStatus", RepairStatus.values());
        enumMap.put("kcsVT", KSCVT.values());
        enumMap.put("priority", Priority.values());

        if (enumMap.containsKey(name)) {
            Enum[] values = enumMap.get(name);
            if (obj instanceof Short) {
                short index = (Short) obj;
                if (index >= 0 && index < values.length) {
                    return values[index].name();
                }
            }
        }

        return obj.toString();
    }

    public static Long getDateFormatValid(String dateString) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        sdf1.setLenient(false);

        try {
            return sdf1.parse(dateString).getTime();
        } catch (ParseException e) {
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
            sdf2.setLenient(false);
            try {
                return sdf2.parse(dateString).getTime();
            } catch (ParseException ex) {
                return -1L;
            }
        }
    }
}
