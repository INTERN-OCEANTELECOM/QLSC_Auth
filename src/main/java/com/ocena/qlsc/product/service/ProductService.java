package com.ocena.qlsc.product.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import com.ocena.qlsc.podetail.service.PoDetailService;
import com.ocena.qlsc.podetail.status.ErrorType;
import com.ocena.qlsc.podetail.status.regex.Regex;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.mapper.ProductMapper;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


@Service
public class ProductService extends BaseServiceImpl<Product, ProductDTO> implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    PoDetailService poDetailService;

    @Override
    protected BaseRepository<Product> getBaseRepository() {
        return productRepository;
    }

    @Override
    protected BaseMapper<Product, ProductDTO> getBaseMapper() {
        return productMapper;
    }

    @Override
    protected Function<String, Optional<Product>> getFindByFunction() {
        return null;
    }

    @Override
    protected Page<Product> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        String propertySearch = searchKeywordDto.getProperty();

        if (propertySearch.equals("productId")){
            return productRepository.searchProduct(searchKeywordDto.getKeyword(), null, pageable);
        } else if (propertySearch.equals("productName")){
            return productRepository.searchProduct(null, searchKeywordDto.getKeyword(), pageable);
        }

        return productRepository.searchProduct(searchKeywordDto.getKeyword(), searchKeywordDto.getKeyword(), pageable);
    }

    @Override
    protected List<Product> getListSearchResults(String keyword) {
        return null;
    }

    @Override
    @Transactional
    public ListResponse importProducts(MultipartFile file) {
        List<ErrorResponseImport> listError = new ArrayList<>();
        List<Product> listInsert = new ArrayList<>();

        //Check file Excel

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua hàng đầu tiên
            if (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ErrorResponseImport errorResponseImport = poDetailService.validateHeaderValue(row, Regex.importProduct);
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
                } else {
                    Product product = (Product) data;
                    if (!productRepository.existsProductByProductId(product.getProductId()) &&
                            !listInsert.stream().anyMatch(products -> products.getProductId().equals(product.getProductId()))) {
                        listInsert.add(product);
                    } else {
                        listError.add(new ErrorResponseImport(ErrorType.RECORD_EXISTED, rowIndex,
                                "Mã hàng hóa " + product.getProductId() + " đã tồn tại"));
                    }
                }
            }
            productRepository.saveAll(listInsert);
            listError.add(0, new ErrorResponseImport(ErrorType.DATA_SUCCESS, listInsert.size() + " hàng Insert thành công"));
        } catch (Exception ex) {
            listError.add(new ErrorResponseImport(ErrorType.FILE_NOT_FORMAT, "File không đúng định dạng"));
            return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }

        return ResponseMapper.toListResponseSuccess(listError);
    }

    public Object readExcelRowData(Row row, int rowIndex) {
        ErrorResponseImport errorResponseImport = (ErrorResponseImport)
                poDetailService.validateNumbericColumns(row, rowIndex, 0);
        if (errorResponseImport != null) {
            return errorResponseImport;
        }
        Long productId = Math.round(row.getCell(0).getNumericCellValue());
        String productName = row.getCell(1).getStringCellValue();
        ProductDTO productDTO = new ProductDTO(productId, productName);
        List<String> resultError = validationRequest(productDTO);
        if(resultError == null) {
            return getBaseMapper().dtoToEntity(productDTO);
        } else {
            return new ErrorResponseImport(ErrorType.DATA_NOT_MAP, rowIndex, resultError.get(0));
        }
    }
}
