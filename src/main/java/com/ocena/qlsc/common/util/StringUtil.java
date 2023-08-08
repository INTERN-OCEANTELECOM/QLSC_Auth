package com.ocena.qlsc.common.util;

public class StringUtil {
    public static String cutSubString(String str, String subStr) {
        return str.replace(subStr, "");
    }

    public static String convertValueToFormattedString(Object obj, String name) {
        if(obj == null)
            return "N/A";
        if(obj instanceof Long)
            return DateUtil.convertObjectToDateFormat(obj);
        if(obj instanceof Short)
            return EnumUtil.getEnumValueNameByIndex(obj, name);
        return obj.toString();
    }
}
