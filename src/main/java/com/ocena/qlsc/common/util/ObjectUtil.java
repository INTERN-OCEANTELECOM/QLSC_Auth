package com.ocena.qlsc.common.util;

public class ObjectUtil {
    public static boolean notEqual(Object obj1, Object obj2) {
        System.out.println("oldFieldValue: " + obj1);
        System.out.println("newFieldValue: " + obj2);
        if(obj1 == null && obj2 == null)
            return false;
        if(obj2 == null) {
            return !obj1.equals(obj2);
        }
        System.out.println(!obj2.equals(obj1));
        return !obj2.equals(obj1);
    }
}
