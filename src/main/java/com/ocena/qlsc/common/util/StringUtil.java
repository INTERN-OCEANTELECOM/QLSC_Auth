package com.ocena.qlsc.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static String cutSubString(String str, String subStr) {
        return str.replace(subStr, "");
    }

    public static List<String> splitStringToList(String input) {
        return input != null
                ? Arrays.stream(input.trim().split("\\s+")).toList()
                : new ArrayList<>();
    }
}
