package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.podetail.config.Mapper;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.enums.RepairStatus;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.model.PoDetailMapper;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.status.ErrorType;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import com.ocena.qlsc.podetail.status.RegexConstants;
import com.ocena.qlsc.podetail.status.UpdateField;
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
    public List<String> validationRequest(Object object) {
        return super.validationRequest(object);
    }

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
        return poDetailRepository::findByPoDetailId;
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
        if(typeUpdate.equals(UpdateField.ExportPartner)) {
            if(poDetail.getRepairStatus() == null) {
                listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                        rowIndex, "Podetail: " + poDetail.getPoDetailId() + " phải cập nhật trang thái SC " +
                        "trước khi cập nhật trạng thái xuất kho"));
                return false;
            }
        }
        if(typeUpdate.equals(UpdateField.KSCVT)) {
            if(poDetail.getExportPartner() == null) {
                listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                        rowIndex, "Podetail: " + poDetail.getPoDetailId() + " phải cập nhật trang thái SC " +
                        "và trạng thái xuất kho trước khi cập nhật KCS VT"));
                return false;
            }
            if(poDetail.getRepairStatus() != RepairStatus.SC_XONG.ordinal()) {
                listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                        rowIndex, "Podetail: " + poDetail.getPoDetailId() + " có trạng thái SC là " +
                        RepairStatus.values()[poDetail.getRepairStatus()].name()));
                return false;
            }
        }
        if(typeUpdate.equals(UpdateField.WarrantyPeriod)) {
            if(poDetail.getKcsVT() == null) {
                listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                        rowIndex, "Podetail: " + poDetail.getPoDetailId() + " phải cập nhật trang thái KSC VT " +
                        "trước khi cập nhật thông tin bảo hành"));
                return false;
            }
            if(poDetail.getRepairStatus() != RepairStatus.SC_XONG.ordinal()) {
                listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                        rowIndex, "Podetail: " + poDetail.getPoDetailId() + " có trạng thái SC là " +
                        RepairStatus.values()[poDetail.getRepairStatus()].name()));
                return false;
            }
        }
        return true;
    }

    public ListResponse<ErrorResponseImport> processFileUpdatePoDetail(MultipartFile file, String typeUpdate) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        List<ErrorResponseImport> listError = new ArrayList<>();

        List<PoDetail> listUpdatePoDetail = new ArrayList<>();

        Object dataFile = processExcelFile.processExcelFile(file);
        if(dataFile instanceof ListResponse) {
            return (ListResponse) dataFile;
        }
        Iterator<Row> rowIterator = (Iterator<Row>) dataFile;


        // Hang thu hai
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            RegexConstants regex = new RegexConstants();
            Field field = RegexConstants.class.getDeclaredField(typeUpdate + "Map");
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
                String setterMethod = "set" + typeUpdate.substring(0, 1).toUpperCase()
                        + typeUpdate.substring(1);
                if(!typeUpdate.equals(UpdateField.WarrantyPeriod)) {
                    Short value = (Short) field.get(poDetailResponse);

                    Method setter = poDetail.getClass().getMethod(setterMethod, Short.class);
                    setter.invoke(poDetail, value);
                } else {
                    Long value = (Long) field.get(poDetailResponse);
                    Method setter = poDetail.getClass().getMethod(setterMethod, Long.class);
                    setter.invoke(poDetail, value);
                }
                listUpdatePoDetail.add(poDetail);
            }
        }
        poDetailRepository.saveAllAndFlush(listUpdatePoDetail);
        listError.add(0, new ErrorResponseImport(ErrorType.DATA_SUCCESS, listUpdatePoDetail.size() + " Import thành công"));

        return ResponseMapper.toListResponseSuccess(listError);
    }

    public ListResponse<ErrorResponseImport> importPODetail(MultipartFile file) throws IOException {
        List<ErrorResponseImport> listError = new ArrayList<>();
        List<PoDetail> listInsertPoDetail = new ArrayList<>();

        Object dataFile = processExcelFile.processExcelFile(file);
        if(processExcelFile.processExcelFile(file) instanceof ListResponse) {
            return (ListResponse) dataFile;
        }
        Iterator<Row> rowIterator = (Iterator<Row>) processExcelFile.processExcelFile(file);

        // Hang thu hai
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            ErrorResponseImport errorResponseImport = processExcelFile.validateHeaderValue(row, RegexConstants.importPOHeader);
            if (errorResponseImport != null) {
                listError.add(errorResponseImport);
                return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
            }
        }
        List<Product> listAllProduct = productRepository.findAll();

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

                System.out.println("Size: " + listAllProduct.size());

                boolean isProductExist = listAllProduct.stream()
                        .anyMatch(p -> p.getProductId().equals(poDetailResponse.getProduct().getProductId()));

                if (isProductExist) {
                    // Co san pham thi
                    Optional<Po> isExistPoByPoNumber = poRepository.findByPoNumber(poDetailResponse.getPo().getPoNumber());

                    Optional<PoDetail> existPODetail = poDetailRepository.findByPoDetailId(poDetailResponse.getPoDetailId());
                    if (existPODetail.isPresent() || listInsertPoDetail.stream()
                            .anyMatch(value -> value.getPoDetailId().equals(poDetailResponse.getPoDetailId()))) {
                        listError.add(new ErrorResponseImport(ErrorType.RECORD_EXISTED,
                                rowIndex, "PoDetail: " + poDetailResponse.getPoDetailId() + " đã tồn tại nên không thể import"));
                        continue;
                    }
                    if (isExistPoByPoNumber.isEmpty()) {
                        listError.add(new ErrorResponseImport(ErrorType.DATA_NOT_FOUND,
                                rowIndex, "Podetail: " + poDetailResponse.getPoDetailId() + " có PO không tồn tại"));
                        continue;
                    }
                    Integer quantity = isExistPoByPoNumber.get().getQuantity();
                    Long countPoDetailByPoNumber = poDetailRepository.countByPoNumber(poDetailResponse.getPo().getPoNumber());
                    if(countPoDetailByPoNumber + listInsertPoDetail.size() >= quantity) {
                        listError.add(new ErrorResponseImport("PoNumber " + poDetailResponse.getPo().getPoNumber(),
                                "Đã đủ số lượng nên không thể import nữa"));
                        break;
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


        String poDetailId = poNumber + "-" + productId + "-" + serialNumber;

        PoDetailResponse poDetailResponse = PoDetailResponse.builder()
                .product(new ProductDTO(productId))
                .poDetailId(poDetailId)
                .po(new PoDTO(poNumber))
                .build();

        String setterMethod = "set" + attribute.substring(0, 1).toUpperCase()
                + attribute.substring(1);
        Method setter = null;
        try {
            if (row.getCell(4).getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(row.getCell(4))){
                setter = poDetailResponse.getClass().getMethod(setterMethod.toString(), Long.class);
                setter.invoke(poDetailResponse, row.getCell(4).getDateCellValue().getTime());
            } else {
                setter = poDetailResponse.getClass().getMethod(setterMethod.toString(), Short.class);
                setter.invoke(poDetailResponse, (short) row.getCell(4).getNumericCellValue());
            }
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

    @Override
    public DataResponse<PoDetailResponse> updatePoDetail(PoDetailResponse poDetailResponse, String key) {
        List<String> result = validationRequest(poDetailResponse);

        if((result != null)) {
            return ResponseMapper.toDataResponse(result, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }

        //get Po by PoDetailResponse
        Optional<PoDetail> poDetail = poDetailRepository.findByPoDetailId(key);

        //set new Data
        if (poDetail.isPresent()){
            poDetail.get().setImportDate(poDetailResponse.getImportDate());
            poDetail.get().setRepairCategory(poDetailResponse.getRepairCategory());
            poDetail.get().setRepairStatus((poDetailResponse.getRepairStatus()));
            poDetail.get().setExportPartner(poDetailResponse.getExportPartner());
            poDetail.get().setKcsVT(poDetailResponse.getKcsVT());
            poDetail.get().setWarrantyPeriod(poDetailResponse.getWarrantyPeriod());

            poDetailRepository.save(poDetail.get());

            return ResponseMapper.toDataResponse("", StatusCode.REQUEST_SUCCESS, StatusMessage.REQUEST_SUCCESS);
        }

        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
    }
}
