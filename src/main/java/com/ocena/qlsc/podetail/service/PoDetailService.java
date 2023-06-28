package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.podetail.config.Mapper;
import com.ocena.qlsc.podetail.dto.PoDetailRequest;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.model.PoDetailMapper;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.status.ErrorType;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import com.ocena.qlsc.podetail.status.regex.Regex;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.repository.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@Service
public class PoDetailService extends BaseServiceImpl<PoDetail, PoDetailResponse> implements  IPoDetail {
    @Autowired
    PoDetailMapper poDetailMapper;

    @Autowired
    PoDetailRepository poDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PoRepository poRepository;

    @Autowired
    Mapper mapper;

    @Override
    protected BaseRepository<PoDetail> getBaseRepository() {
        return poDetailRepository;
    }

    @Override
    protected BaseMapper<PoDetail, PoDetailResponse> getBaseMapper() {
        return poDetailMapper;
    }

    @Override
    protected Function<String, Optional<PoDetail>> getFindByFunction() {
        return null;
    }

    @Override
    protected Page<PoDetail> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<PoDetail> getListSearchResults(String keyword) {
        return null;
    }

    @Override
    public ListResponse<ErrorResponseImport> importPOStatus(MultipartFile file) {
        List<ErrorResponseImport> listError = new ArrayList<>();
        Integer updateAmount = 0;

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();
            List<PoDetail> listUpdatePoDetailStatus = new ArrayList<>();
            List<Product> listAllProduct = productRepository.findAll();

            // Bỏ qua hàng đầu tiên
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            // Hang thu hai
            if (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ErrorResponseImport errorResponseImport = validateHeaderValue(row, Regex.importPORepairStatus);
                if(errorResponseImport != null) {
                    listError.add(errorResponseImport);
                    return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
                }
            }
            // Đọc từng hàng trong sheet và lưu vào database
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int rowIndex = row.getRowNum() + 1;

                Object data = readExcelForUpdateStatus(row, rowIndex);

                if (data instanceof ErrorResponseImport) {
                    ErrorResponseImport errorResponseImport = (ErrorResponseImport) data;
                    listError.add(errorResponseImport);
                }
                else {
                    PoDetail poDetail = (PoDetail) data;
                    boolean isProductExist = listAllProduct.stream()
                            .anyMatch(p -> p.getProductId().equals(poDetail.getProduct().getProductId()));

                    if (isProductExist) {
                        // Co san pham thi
                        Optional<PoDetail> existPODetail = poDetailRepository.findByPoDetailId(poDetail.getPoDetailId());

                        if (!poRepository.existsByPoNumber(poDetail.getPo().getPoNumber())) {
                            ErrorResponseImport errorResponseImport = new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                                    rowIndex, "Podetail: " + poDetail.getProduct().getProductId() + " có PO không tồn tại");
                            listError.add(errorResponseImport);
                            continue;
                        }

                        if (existPODetail.isPresent()) {
                            existPODetail.get().setRepairStatus(poDetail.getRepairStatus());
                            listUpdatePoDetailStatus.add(existPODetail.get());
                            updateAmount++;
                        }  else {
                            ErrorResponseImport errorResponseImport = new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                                    rowIndex, "Podetail: " + poDetail.getProduct().getProductId() + " có id không tồn tại");
                            listError.add(errorResponseImport);
                        }
                    } else {
                        ErrorResponseImport errorResponseImport = new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                                rowIndex, "PoDetail: " + poDetail.getPoDetailId() + " có ProductID không tồn tại");
                        listError.add(errorResponseImport);
                    }
                }
            }
            for (PoDetail a:listUpdatePoDetailStatus){
                System.out.println("PO là: " + a.toString());
            }
            poDetailRepository.saveAllAndFlush(listUpdatePoDetailStatus);
            listError.add(0, new ErrorResponseImport(ErrorType.DATA_SUCCESS, updateAmount + " Import thành công"));
        }
        catch (Exception ex) {
            System.out.println("Lỗi: " + ex.getMessage());
        }
        return ResponseMapper.toListResponseSuccess(listError);
    }

    private ListResponse importData(Object data){
        return null;
    }
    @Override
    public ListResponse<ErrorResponseImport> importPODetail(MultipartFile file) throws IOException {
        List<ErrorResponseImport> listError = new ArrayList<>();
        Integer insertAmount = 0;
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();
            List<PoDetail> listInsertPoDetail = new ArrayList<>();
            List<Product> listAllProduct = productRepository.findAll();

            // Bỏ qua hàng đầu tiên
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            // Hang thu hai
            if (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ErrorResponseImport errorResponseImport = validateHeaderValue(row, Regex.importPOHeader);
                if(errorResponseImport != null) {
                    listError.add(errorResponseImport);
                    return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
                }
            }
            // Đọc từng hàng trong sheet và lưu vào database
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int rowIndex = row.getRowNum() + 1;

                Object data = readExcelRowData(row, rowIndex);

                if (data instanceof ErrorResponseImport) {
                    ErrorResponseImport errorResponseImport = (ErrorResponseImport) data;
                    listError.add(errorResponseImport);
                }
                else {
                    PoDetail poDetail = (PoDetail) data;
                    boolean isProductExist = listAllProduct.stream()
                            .anyMatch(p -> p.getProductId().equals(poDetail.getProduct().getProductId()));

                    if (isProductExist) {
                        // Co san pham thi
                        Optional<PoDetail> existPODetail = poDetailRepository.findByPoDetailId(poDetail.getPoDetailId());
                        if (existPODetail.isPresent() || listInsertPoDetail.stream()
                                .anyMatch(value -> value.getPoDetailId().equals(poDetail.getPoDetailId()))) {
                            ErrorResponseImport errorResponseImport = new ErrorResponseImport(ErrorType.RECORD_EXISTED,
                                    rowIndex, "PoDetail: " + poDetail.getPoDetailId() + " đã tồn tại nên không thể import");
                            listError.add(errorResponseImport);
                            continue;
                        }
                        if (!poRepository.existsByPoNumber(poDetail.getPo().getPoNumber())) {
                            ErrorResponseImport errorResponseImport = new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                                    rowIndex, "Podetail: " + poDetail.getProduct().getProductId() + " có PO không tồn tại");
                            listError.add(errorResponseImport);
                            continue;
                        }
                        listInsertPoDetail.add(poDetail);
                        insertAmount++;
                    } else {
                        ErrorResponseImport errorResponseImport = new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                                rowIndex, "PoDetail: " + poDetail.getPoDetailId() + " có ProductID không tồn tại");
                        listError.add(errorResponseImport);
                    }
                }
            }
            poDetailRepository.saveAllAndFlush(listInsertPoDetail);
            listError.add(0, new ErrorResponseImport(ErrorType.DATA_SUCCESS, insertAmount + " Import thành công"));
        }
        catch (Exception ex) {
            listError.add(new ErrorResponseImport(ErrorType.FILE_NOT_FORMAT, "File không đúng định dạng"));
            return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }
        return ResponseMapper.toListResponseSuccess(listError);
    }

    public boolean isValidHeader(String cellValue, String regex) {
//        System.out.println(cellValue != null && cellValue.toLowerCase().matches(regex));
        return cellValue != null && cellValue.toLowerCase().matches(regex);
    }

    public ErrorResponseImport validateHeaderValue(Row row, HashMap<Integer, String> map) {
        if(row != null) {
            for(Integer key : map.keySet()) {
//                System.out.println(row.getCell(key).getStringCellValue());
//                System.out.println(map.get(key));
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
                        "Hàng " + rowIndex + " Cột " + columnIndex + " không phải kiểu numberic");
            }
        }
        return null;
    }

    public Object readExcelRowData(Row row, int rowIndex) {
        Long Id = Math.round(row.getCell(0).getNumericCellValue());
        ErrorResponseImport errorResponseImport = (ErrorResponseImport) validateNumbericColumns(row, rowIndex, 0, 1, 6);
        if (errorResponseImport != null) {
            return errorResponseImport;
        }

        // La numberic roi thi xu ly
        Long productId = Long.valueOf((long) row.getCell(1).getNumericCellValue());
        String serialNumber = row.getCell(2).getStringCellValue();
        String poNumber = row.getCell(3).getStringCellValue();
        String bbbgNumber = row.getCell(4).getStringCellValue();
        Long importDate = row.getCell(5).getDateCellValue().getTime();
        Short repairCate = (short) row.getCell(6).getNumericCellValue();

        String poDetailId = poNumber + "-" + productId + "-" + serialNumber;

        PoDetailRequest poDetailRequest = PoDetailRequest.builder()
                .poDetailId(poDetailId)
                .product(new ProductDTO(productId))
                .serialNumber(serialNumber)
                .bbbgNumber(bbbgNumber)
                .importDate(importDate)
                .repairCategory(repairCate)
                .po(new PoDTO(poNumber))
                .build();

        List<String> resultError = validationRequest(poDetailRequest);

        if(resultError == null) {
            return mapper.convertTo(poDetailRequest, PoDetail.class);
        } else {
            return new ErrorResponseImport(ErrorType.DATA_NOT_MAP, rowIndex, resultError.get(0));
        }
    }

    private  Object readExcelForUpdateStatus(Row row, int rowIndex) {
        Long Id = Math.round(row.getCell(0).getNumericCellValue());
        ErrorResponseImport errorResponseImport = (ErrorResponseImport) validateNumbericColumns(row, rowIndex, 0, 1, 4);
        if (errorResponseImport != null) {
            return errorResponseImport;
        }

        // La numberic roi thi xu ly
        Long productId = Long.valueOf((long) row.getCell(1).getNumericCellValue());
        String serialNumber = row.getCell(2).getStringCellValue();
        String poNumber = row.getCell(3).getStringCellValue();
        Short status = (short) row.getCell(4).getNumericCellValue();

        String poDetailId = poNumber + "-" + productId + "-" + serialNumber;

        PoDetailRequest poDetailRequest = PoDetailRequest.builder()
                .poDetailId(poDetailId)
                .repairStatus(status)
                .product(new ProductDTO(productId))
                .po(new PoDTO(poNumber))
                .build();

        List<String> resultError = validationRequest(poDetailRequest);

        if (resultError == null) {
            return mapper.convertTo(poDetailRequest, PoDetail.class);
        } else {
            return new ErrorResponseImport(ErrorType.DATA_NOT_MAP, rowIndex, resultError.get(0));
        }
    }
}
