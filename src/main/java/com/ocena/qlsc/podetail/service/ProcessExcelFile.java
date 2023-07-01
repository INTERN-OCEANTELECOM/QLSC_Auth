package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.podetail.status.ErrorType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
public class ProcessExcelFile {

    public boolean isValidHeader(String cellValue, String regex) {
        return cellValue != null && cellValue.toLowerCase().matches(regex);
    }

    public ErrorResponseImport validateHeaderValue(Row row, HashMap<Integer, String> map) {
        if(row != null) {
            for(Integer key : map.keySet()) {
                System.out.println(row.getCell(key).getStringCellValue());
                System.out.println(map.get(key));
                if(!isValidHeader(row.getCell(key).getStringCellValue(), map.get(key))) {
                    return new ErrorResponseImport(ErrorType.HEADER_DATA_WRONG, " Cột Header thứ " + key + " sai");
                }
            }
        }
        if (row.getLastCellNum() > map.size()){
            return new ErrorResponseImport(ErrorType.HEADER_DATA_WRONG, "Header không đúng! Hãy kiểm tra lại");
        }
        return null;
    }

    private boolean isNumericCell(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC;
    }

    public Object validateNumbericColumns(Row row, int rowIndex, int... columnIndexes) {
        for (int columnIndex : columnIndexes) {
            Cell cell = row.getCell(columnIndex);
            if (!isNumericCell(cell)) {
                return new ErrorResponseImport(ErrorType.DATA_NOT_MAP, rowIndex,
                        "Hàng " + rowIndex + " Cột " + (columnIndex + 1) + " không phải kiểu numberic");
            }
        }
        return null;
    }

    public Object processExcelFile(MultipartFile file) {
        List<ErrorResponseImport> listError = new ArrayList<>();

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            listError.add(new ErrorResponseImport(ErrorType.FILE_NOT_FORMAT, "File không đúng định dạng"));
            return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua hàng đầu tiên
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            return rowIterator;
        } catch (IOException e) {
            listError.add(new ErrorResponseImport(ErrorType.FILE_NOT_FORMAT, "File không đúng định dạng"));
            // Thực hiện các xử lý khác với dữ liệu Excel tại đây
            return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }
    }
}
