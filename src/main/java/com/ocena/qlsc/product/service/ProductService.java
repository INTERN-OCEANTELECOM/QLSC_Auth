package com.ocena.qlsc.product.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.product.dto.ErrorResponse;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.mapper.ProductMapper;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        List<ErrorResponse> listError = new ArrayList<>();
        List<Product> listInsert = new ArrayList<>();

        //Check file Excel
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseMapper.toListResponseSuccess(null);
        }

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua hàng đầu tiên
            Row header = sheet.getRow(0);
            rowIterator.next();

            // Đọc từng hàng trong sheet và lưu vào database
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int rowIndex = row.getRowNum() + 1;

                if (row.getCell(0).getCellType() != CellType.NUMERIC) {
                    listError.add(new ErrorResponse("Hàng " + rowIndex, "Có ID sản phẩm không phải là numberic"));
                } else {
                    // Check cell
                    if (header.getLastCellNum() > 2){
                        return ResponseMapper.toListResponseSuccess(null);
                    }

                    Long productId = Math.round(row.getCell(0).getNumericCellValue());
                    String productName = row.getCell(1).getStringCellValue();
                    Product product = new Product(productId, productName);

                    List<String> errors = validationRequest(product);
                    if(errors != null) {
                        listError.add(new ErrorResponse(rowIndex, errors.get(0)));
                    } else {
                        if (!productRepository.existsProductByProductId(productId) &&
                            !listInsert.stream().anyMatch(products -> products.getProductId().equals(productId))) {
                            listInsert.add(product);
                        } else {
                            listError.add(new ErrorResponse("Hàng " + rowIndex, "Có ID sản phẩm đã tồn tại"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseMapper.toListResponseSuccess(null);
        }

        productRepository.saveAll(listInsert);
        listError.add(new ErrorResponse(listInsert.size() + " Hàng", "Insert thành công"));

        return ResponseMapper.toListResponseSuccess(listError);
    }
}
