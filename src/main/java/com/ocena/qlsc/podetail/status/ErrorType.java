package com.ocena.qlsc.podetail.status;

public interface ErrorType {
    public static final String RECORD_EXISTED = "Bản ghi đã tồn tại";
    public static final String DATA_NOT_FOUND = "Dữ liệu không tồn tại";
    public static final String DATA_NOT_MAP = "Dự liệu không đúng định dạng";
    public static final String DATA_NOT_CORRECT = "Dữ liêu nhập không đúng";
    public static final String HEADER_DATA_WRONG = "Header nhập không đúng";
    public static final String DATA_SUCCESS = "Import thành công";
    public static final String FILE_NOT_FORMAT = "File không đúng định dạng";
}
