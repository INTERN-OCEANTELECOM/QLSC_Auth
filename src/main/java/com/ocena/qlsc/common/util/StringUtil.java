package com.ocena.qlsc.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static String cutSubString(String str, String subStr) {
        return str.replace(subStr, "");
    }

    public static List<String> splitStringToList(String input) {
        return input != null && !input.trim().equals("")
                ? Arrays.stream(input.trim().split("\\s+")).toList()
                : new ArrayList<>();
    }

    public static List<String> splitStringToList2(String input) {
        return input != null && !input.trim().equals("")
                ? Arrays.stream(input.trim().split("\\s+")).toList()
                : null;
    }

    public static List<String> convertStringToList(String input) {
        List<String> stringList = new ArrayList<>();
        stringList.add(input);
        return stringList;
    }

    public static String convertValueToFormattedString(Object obj, String name) {
        if(obj == null)
            return "NOT_AVAILABLE";
        if(obj instanceof Long)
            return DateUtil.convertObjectToDateFormat(obj);
        if(obj instanceof Short)
            return EnumUtil.getEnumNameByValue(obj, name);
        return obj.toString();
    }

    public static String extractUserNameFromEmail(String email) {
        return email.substring(0, email.indexOf("@"));
    }

    public static boolean containsAlphabeticCharacters(String str) {
        return str.matches(".*[a-zA-Z].*");
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null & str.isEmpty();
    }
}
