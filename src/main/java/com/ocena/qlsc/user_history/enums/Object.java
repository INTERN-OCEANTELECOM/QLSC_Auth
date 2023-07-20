package com.ocena.qlsc.user_history.enums;

public enum Object {
    USER("Người Dùng"),
    PO("PO"),
    PO_DETAIL("Hàng Hóa"),
    PRODUCT("Sản Phẩm"),
    ;
    private final String value;

    Object(String value) {
        this.value = value;
    }
}
