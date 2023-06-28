package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.model.PoDetailMapper;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.status.RepairCategory;
import com.ocena.qlsc.product.dto.ErrorResponse;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.repository.ProductRepository;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
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
    public ListResponse<ErrorResponse> importPOStatus(MultipartFile file) {
        return null;
    }

    @Override
    public ListResponse<ErrorResponse> importPODetail(MultipartFile file) {
        List<ErrorResponse> listError = new ArrayList<>();
        Integer insertAmount = 0;
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();
            List<PoDetail> listInsertPoDetail = new ArrayList<>();
            List<Product> listAllProduct = productRepository.findAll();

            //Check Cell
            Row header = sheet.getRow(1);
            if (header.getLastCellNum() > 7){
                return ResponseMapper.toListResponseSuccess(null);
            }

            // Bỏ qua hàng đầu tiên
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            // Bo qua hang thu hai
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            // Đọc từng hàng trong sheet và lưu vào database
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Object data = getDataFromRow(row);
                if (data instanceof Boolean) {
                    Boolean isEnd = (Boolean) data;
                    if (isEnd) {
                        break;
                    }
                } else if (data instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) data;
                    listError.add(errorResponse);
                } else {
                    PoDetail poDetail = (PoDetail) data;
                    boolean isProductExist = listAllProduct.stream()
                            .anyMatch(p -> p.getProductId().equals(poDetail.getProduct().getProductId()));

                    if (isProductExist) {
                        // Co san pham thi
                        Optional<PoDetail> existPODetail = poDetailRepository.findByPoDetailId(poDetail.getPoDetailId());
                        if (existPODetail.isPresent()) {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    poDetail.getProduct().getProductId(), "OrderID: " + poDetail.getPoDetailId() + " đã tồn tại nên không thể import");
                            listError.add(errorResponse);
                        } else {
                            if (!poRepository.existsByPoNumber(poDetail.getPo().getPoNumber())) {
                                ErrorResponse errorResponse = new ErrorResponse(
                                        poDetail.getProduct().getProductId(), "PO san pham " + poDetail.getProduct().getProductId() + " Khong hop le");
                                listError.add(errorResponse);
                            } else if ( !listInsertPoDetail.stream().anyMatch(poDetail1 -> poDetail1.getPoDetailId().equals(poDetail.getPoDetailId()))){
                                listInsertPoDetail.add(poDetail);
                                insertAmount++;
                            } else {
                                ErrorResponse errorResponse = new ErrorResponse(
                                        poDetail.getProduct().getProductId(), "PoDetail " + poDetail.getProduct().getProductId() + " Da Ton Tai");
                                listError.add(errorResponse);
                            }
                        }
                    } else {
                        ErrorResponse errorResponse = new ErrorResponse(
                                poDetail.getProduct().getProductId(), "Khong co san pham co ID la: " + poDetail.getProduct().getProductId());
                        listError.add(errorResponse);
                    }
                }
            }
            poDetailRepository.saveAllAndFlush(listInsertPoDetail);
        }
        catch (Exception ex) {
            System.out.println("Lỗi: " + ex.getMessage());
        }
        listError.add(0, new ErrorResponse(insertAmount, "So luong import thanh cong " + insertAmount + " hang"));
        return ResponseMapper.toListResponseSuccess(listError);
    }

    public Object getDataFromRow(Row row) {
        if (row.getCell(0).getCellType() == CellType.STRING) {
            return false;
        }
        Long Id = Math.round(row.getCell(0).getNumericCellValue());

        if(row.getCell(1).getCellType() != CellType.NUMERIC) {
            return new ErrorResponse("Hàng" + Id, "Có ID không phải là numberic");
        }

        // La numberic roi thi xu ly
        Long productId = Long.valueOf((long) row.getCell(1).getNumericCellValue());
        String serialNumber = row.getCell(2).getStringCellValue();
        String poNumber = row.getCell(3).getStringCellValue();
        String bbbgNumber = row.getCell(4).getStringCellValue();
        Long importDate = row.getCell(5).getDateCellValue().getTime();
        Integer repairCate = (int) row.getCell(6).getNumericCellValue();

        String orderId = poNumber + "-" + productId + "-" + serialNumber;
        if(!(repairCate >= 0 && repairCate <= RepairCategory.values().length)) {
            return new ErrorResponse("Hàng" + Id, " Có trạng thái sản xuất không hợp lệ");
        }

        System.out.println("PO có tồn tại" + poRepository.findByPoNumber(poNumber));
        return PoDetail.builder()
                .poDetailId(orderId)
                .serialNumber(serialNumber)
                .bbbgNumber(bbbgNumber)
                .importDate(importDate)
                .repairCategory(RepairCategory.values()[repairCate])
                .product(new Product(productId))
                .po(new Po(poNumber))
                .build();
    }
}
