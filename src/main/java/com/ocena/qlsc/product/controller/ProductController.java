package com.ocena.qlsc.product.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.product.dto.ErrorResponse;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/product")
@CrossOrigin(value = "*")
public class ProductController extends BaseApiImpl<Product, ProductDTO> {

    @Autowired
    ProductService productService;

    @Override
    protected BaseService<Product, ProductDTO> getBaseService() {
        return productService;
    }

    @Override
    protected Function<String, Optional<Product>> getFindByFunction() {
        return null;
    }

    @Override
    public DataResponse<Product> add(ProductDTO objectDTO) {
        return super.add(objectDTO);
    }

    @Override
    public ListResponse<ProductDTO> getAll() {
        return super.getAll();
    }

    @GetMapping
    public ListResponse<ProductDTO> getProducts(@RequestParam("page") int page,
                                                      @RequestParam("size") int size) {
        return super.getAllByPage(page, size);
    }

    @PostMapping("/import")
    public ListResponse<ErrorResponse> importProducts(@RequestParam("file") MultipartFile file) {
        return productService.importProducts(file);
    }

    @Override
    public ListResponse<Product> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return super.searchByKeyword(searchKeywordDto);
    }
}
