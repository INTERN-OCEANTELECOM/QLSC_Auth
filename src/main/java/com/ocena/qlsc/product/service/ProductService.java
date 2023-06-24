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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
    public ListResponse<ProductDTO> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        Page<ProductDTO> productDTOs = products.map(product
                -> productMapper.entityToDto(product));

        return ResponseMapper.toPagingResponse(productDTOs, StatusCode.REQUEST_SUCCESS, StatusMessage.REQUEST_SUCCESS);
    }

    @Override
    public ListResponse<ProductDTO> getAllProduct() {
        List<Product> listProduct = productRepository.findAll();

        List<ProductDTO> productDTOs = listProduct
                .stream()
                .map(product -> productMapper.entityToDto(product)).toList();

        return ResponseMapper.toListResponseSuccess(productDTOs);
    }

    @Override
    @Transactional
    public ListResponse importProducts(MultipartFile file) {
        List<ErrorResponse> listError = new ArrayList<>();
        Integer insertAmount = 0;
        List<Product> listInsert = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua hàng đầu tiên
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            int countProduct = 1;
            // Đọc từng hàng trong sheet và lưu vào database
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                countProduct++;
                if (row.getCell(0).getCellType() != CellType.BLANK) {
                    if (row.getCell(0).getCellType() == CellType.STRING) {
                        ErrorResponse errorResponse = new ErrorResponse(row.getCell(0).getStringCellValue(), "ID sản phẩm phải là Numberic");
                        listError.add(errorResponse);
                    } else {
                        Long productId = Math.round(row.getCell(0).getNumericCellValue());
                        String productName = row.getCell(1).getStringCellValue();

                        if (productName.isEmpty()) {
                            ErrorResponse errorResponse = new ErrorResponse(productId, "Tên sản phẩm rỗng");
                            listError.add(errorResponse);
                        } else {
                            if (!productRepository.existsProductByProductId(productId)) {
                                Product product = new Product(productId, productName);
                                listInsert.add(product);
                                insertAmount++;
                            } else {
                                ErrorResponse errorResponse = new ErrorResponse(productId, "Sản phẩm bị trùng ID");
                                listError.add(errorResponse);
                            }
                        }
                    }
                } else {
                    ErrorResponse errorResponse = new ErrorResponse(countProduct, "Sản phẩm tại hàng " + countProduct + " không có ID");
                    listError.add(errorResponse);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        productRepository.saveAllAndFlush(listInsert);
        listError.add(new ErrorResponse("Số Hàng Insert Thành Công", insertAmount.toString()));

        return ResponseMapper.toListResponseSuccess(listError);
    }
}
