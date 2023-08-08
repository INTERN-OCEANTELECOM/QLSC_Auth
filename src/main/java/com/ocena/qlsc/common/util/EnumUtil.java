package com.ocena.qlsc.common.util;

import com.ocena.qlsc.podetail.enums.KSCVT;
import com.ocena.qlsc.podetail.enums.Priority;
import com.ocena.qlsc.podetail.enums.RepairCategory;
import com.ocena.qlsc.podetail.enums.RepairStatus;

import java.util.HashMap;
import java.util.Map;

public class EnumUtil {
    public static String getEnumValueNameByIndex(Object obj, String name) {
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
}
