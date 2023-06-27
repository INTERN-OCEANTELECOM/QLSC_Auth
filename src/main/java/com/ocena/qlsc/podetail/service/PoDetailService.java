package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.model.PoDetailMapper;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.status.RepairCategory;
import com.ocena.qlsc.product.dto.ErrorResponse;
import com.ocena.qlsc.product.model.Product;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class PoDetailService extends BaseServiceImpl<PoDetail, PoDetailResponse> implements  IPoDetail {
    @Autowired
    PoDetailMapper poDetailMapper;

    @Autowired
    PoDetailRepository poDetailRepository;
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
        if(!(repairCate >= 0 && repairCate <= RepairCategory.values().length)) {
            return new ErrorResponse("Hàng" + Id, " Có trạng thái sản xuất không hợp lệ");
        }

        return PoDetail.builder()
                .serialNumber(serialNumber)
                .bbbgNumber(bbbgNumber)
                .importDate(importDate)
                .repairCategory(RepairCategory.values()[repairCate])
                .product(new Product(productId))
                .po(new Po(poNumber))
                .build();
    }
}
