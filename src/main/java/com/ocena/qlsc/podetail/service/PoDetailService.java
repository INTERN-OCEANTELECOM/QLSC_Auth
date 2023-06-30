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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Service
public class PoDetailService extends BaseServiceImpl<PoDetail, PoDetailResponse> implements IPoDetail {
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

    @Autowired
    ProcessExcelFile processExcelFile;

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
        return poDetailRepository.searchPoDetail(
                searchKeywordDto.getKeyword().get(0),
                searchKeywordDto.getKeyword().get(1),
                searchKeywordDto.getKeyword().get(2),
                searchKeywordDto.getKeyword().get(3),
                searchKeywordDto.getKeyword().get(4),
                searchKeywordDto.getKeyword().get(5),
                searchKeywordDto.getKeyword().get(6),
                searchKeywordDto.getKeyword().get(7),
                searchKeywordDto.getKeyword().get(8),
                pageable);
    }

    @Override
    protected List<PoDetail> getListSearchResults(String keyword) {
        return null;
    }

    public boolean checkUpdatePoDetail(List<ErrorResponseImport> listError, PoDetail poDetail, String typeUpdate, Integer rowIndex) {
        if(typeUpdate.equals("exportPartner")) {
            if(poDetail.getRepairStatus() == null) {
                listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                        rowIndex, "Podetail: " + poDetail.getProduct().getProductId() + " phải cập nhật trang thái SC " +
                        "trước khi cập nhật trạng thái xuất kho"));
                return false;
            }

        }
        if(typeUpdate.equals("kcsVT")) {
            if(poDetail.getRepairStatus() == null || poDetail.getExportPartner() == null) {
                listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                        rowIndex, "Podetail: " + poDetail.getProduct().getProductId() + " phải cập nhật trang thái SC " +
                        "và trạng thái xuất kho trước khi cập nhật KCS VT"));
                return false;
            }
        }
        return true;
    }


    public ListResponse<ErrorResponseImport> processFileUpdatePoDetail(MultipartFile file, String typeUpdate) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        List<ErrorResponseImport> listError = new ArrayList<>();
        List<PoDetail> listUpdatePoDetailStatus = new ArrayList<>();


        Object dataFile = processExcelFile.processExcelFile(file);
        if(processExcelFile.processExcelFile(file) instanceof ListResponse) {
            return (ListResponse) dataFile;
        }
        Iterator<Row> rowIterator = (Iterator<Row>) dataFile;


        // Hang thu hai
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Regex regex = new Regex();
            Field field = Regex.class.getDeclaredField(typeUpdate + "Map");
            ErrorResponseImport errorResponseImport = processExcelFile.validateHeaderValue(row, (HashMap<Integer, String>) field.get(regex));
            if (errorResponseImport != null) {
                listError.add(errorResponseImport);
                return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
            }
        }



        // Đọc từng hàng trong sheet và lưu vào database
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int rowIndex = row.getRowNum() + 1;

            Object data = readExcelUpdatePO(row, rowIndex, typeUpdate);

            if (data instanceof ErrorResponseImport) {
                ErrorResponseImport errorResponseImport = (ErrorResponseImport) data;
                listError.add(errorResponseImport);
            } else {
                PoDetailResponse poDetailResponse = (PoDetailResponse) data;

                Optional<PoDetail> existPODetail = poDetailRepository.findByPoDetailId(poDetailResponse.getPoDetailId());


                if (!existPODetail.isPresent()) {
                    ErrorResponseImport errorResponseImport = new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                            rowIndex, "Podetail: " + poDetailResponse.getPoDetailId() + " không tồn tại");
                    listError.add(errorResponseImport);
                    continue;
                }
                PoDetail poDetail = existPODetail.get();
                if(!checkUpdatePoDetail(listError, poDetail, typeUpdate, rowIndex)) {
                    continue;
                }


                Field field = PoDetailResponse.class.getDeclaredField(typeUpdate);
                field.setAccessible(true);
                Short value = (Short) field.get(poDetailResponse);
                System.out.println("value: " + value);

                String setterMethod = "set" + typeUpdate.substring(0, 1).toUpperCase()
                        + typeUpdate.substring(1);
                System.out.println(setterMethod);
                Method setter = poDetail.getClass().getMethod(setterMethod.toString(), Short.class);
                setter.invoke(poDetail, value);

                System.out.println(poDetail);
                listUpdatePoDetailStatus.add(poDetail);
            }
        }
        poDetailRepository.saveAll(listUpdatePoDetailStatus);
        listError.add(0, new ErrorResponseImport(ErrorType.DATA_SUCCESS, listUpdatePoDetailStatus.size() + " Import thành công"));

        return ResponseMapper.toListResponseSuccess(listError);
    }

    public ListResponse<ErrorResponseImport> importPODetail(MultipartFile file) throws IOException {
        List<ErrorResponseImport> listError = new ArrayList<>();
        List<PoDetail> listInsertPoDetail = new ArrayList<>();
        List<Product> listAllProduct = productRepository.findAll();

        Object dataFile = processExcelFile.processExcelFile(file);
        if(processExcelFile.processExcelFile(file) instanceof ListResponse) {
            return (ListResponse) dataFile;
        }
        Iterator<Row> rowIterator = (Iterator<Row>) processExcelFile.processExcelFile(file);

        // Hang thu hai
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            ErrorResponseImport errorResponseImport = processExcelFile.validateHeaderValue(row, Regex.importPOHeader);
            if (errorResponseImport != null) {
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
            } else {
                PoDetailResponse poDetailResponse = (PoDetailResponse) data;

                boolean isProductExist = listAllProduct.stream()
                        .anyMatch(p -> p.getProductId().equals(poDetailResponse.getProduct().getProductId()));

                if (isProductExist) {
                    // Co san pham thi
                    Optional<PoDetail> existPODetail = poDetailRepository.findByPoDetailId(poDetailResponse.getPoDetailId());
                    if (existPODetail.isPresent() || listInsertPoDetail.stream()
                            .anyMatch(value -> value.getPoDetailId().equals(poDetailResponse.getPoDetailId()))) {
                        listError.add(new ErrorResponseImport(ErrorType.RECORD_EXISTED,
                                rowIndex, "PoDetail: " + poDetailResponse.getPoDetailId() + " đã tồn tại nên không thể import"));
                        continue;
                    }
                    if (!poRepository.existsByPoNumber(poDetailResponse.getPo().getPoNumber())) {
                        listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                                rowIndex, "Podetail: " + poDetailResponse.getProduct().getProductId() + " có PO không tồn tại"));
                        continue;
                    }
                    PoDetail poDetail = getBaseMapper().dtoToEntity(poDetailResponse);
                    System.out.println(poDetail);
                    listInsertPoDetail.add(poDetail);
                } else {
                    listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                            rowIndex, "PoDetail: " + poDetailResponse.getPoDetailId() + " có ProductID không tồn tại"));
                }
            }
        }
        poDetailRepository.saveAllAndFlush(listInsertPoDetail);
        listError.add(0, new ErrorResponseImport(ErrorType.DATA_SUCCESS, listInsertPoDetail.size() + " Import thành công"));

        return ResponseMapper.toListResponseSuccess(listError);
    }

    public boolean isValidHeader(String cellValue, String regex) {
        return cellValue != null && cellValue.toLowerCase().matches(regex);
    }

    public ErrorResponseImport validateHeaderValue(Row row, HashMap<Integer, String> map) {
        if(row != null) {
            for(Integer key : map.keySet()) {
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
        ErrorResponseImport errorResponseImport = (ErrorResponseImport) processExcelFile.validateNumbericColumns(row, rowIndex, 0, 1, 6);
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

        PoDetailResponse poDetailResponse = PoDetailResponse.builder()
                .poDetailId(poDetailId)
                .product(new ProductDTO(productId))
                .serialNumber(serialNumber)
                .bbbgNumber(bbbgNumber)
                .importDate(importDate)
                .repairCategory(repairCate)
                .po(new PoDTO(poNumber))
                .build();

        List<String> resultError = validationRequest(poDetailResponse);

        if(resultError == null) {
//            return mapper.convertTo(poDetailRequest, PoDetail.class);
            return poDetailResponse;
        } else {
            return new ErrorResponseImport(ErrorType.DATA_NOT_MAP, rowIndex, resultError.get(0));
        }
    }

    private Object readExcelUpdatePO(Row row, int rowIndex, String attribute) {
        Long Id = Math.round(row.getCell(0).getNumericCellValue());
        ErrorResponseImport errorResponseImport = (ErrorResponseImport) processExcelFile.validateNumbericColumns(row, rowIndex, 0, 1, 4);
        if (errorResponseImport != null) {
            return errorResponseImport;
        }

        // La numberic roi thi xu ly
        Long productId = Long.valueOf((long) row.getCell(1).getNumericCellValue());
        String serialNumber = row.getCell(2).getStringCellValue();
        String poNumber = row.getCell(3).getStringCellValue();
        Short status = (short) row.getCell(4).getNumericCellValue();

        String poDetailId = poNumber + "-" + productId + "-" + serialNumber;


        PoDetailResponse poDetailResponse = PoDetailResponse.builder()
                .product(new ProductDTO(productId))
                .poDetailId(poDetailId)
                .po(new PoDTO(poNumber))
                .build();

        try {
            String setterMethod = "set" + attribute.substring(0, 1).toUpperCase()
                    + attribute.substring(1);
            System.out.println(setterMethod);
            Method setter = poDetailResponse.getClass().getMethod(setterMethod.toString(), Short.class);
            setter.invoke(poDetailResponse, status);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(e.getMessage());
        }


        List<String> resultError = validationRequest(poDetailResponse);

        if (resultError == null) {
//            return mapper.convertTo(poDetailRequest, PoDetail.class);
            return poDetailResponse;
        } else {
            return new ErrorResponseImport(ErrorType.DATA_NOT_MAP, rowIndex, resultError.get(0));
        }
    }
}
