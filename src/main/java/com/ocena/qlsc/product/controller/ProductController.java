package com.ocena.qlsc.product.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.product.dto.ProductImageDto;
import com.ocena.qlsc.product.dto.ProductRequest;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.dto.ProductDto;
import com.ocena.qlsc.product.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/product")
//@CrossOrigin(value = "*")
public class ProductController extends BaseApiImpl<Product, ProductDto> {

    @Autowired
    ProductService productService;

    @Override
    protected BaseService<Product, ProductDto> getBaseService() {
        return productService;
    }

    @Override
    @ApiShow
    @CacheEvict(value = {"findAllProduct"}, allEntries = true)
    public DataResponse<ProductDto> add(@Valid ProductDto productDto) {
        return super.add(productDto);
    }

    @ApiShow
    @PostMapping("/create")
        public DataResponse<ProductDto> createProduct(@ModelAttribute ProductDto productDto,
                                                      @RequestParam("files") List<MultipartFile> files) {
        return productService.createProduct(files, productDto);
    }

    @Override
    @ApiShow
    public ListResponse<ProductDto> getAll() {
        return productService.getAllProduct();
    }

    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @GetMapping("/get-by-pages")
    public ListResponse<ProductDto> getProductByPage(@Param("page") int page, @Param("size") int size) {
        return productService.getProductByPage(page, size);
    }

    @Override
    @ApiShow
    public ListResponse<ProductDto> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return super.searchByKeyword(searchKeywordDto);
    }

    @Override
    @ApiShow
    @CacheEvict(value = {"findAllProduct"}, allEntries = true)
    public DataResponse<ProductDto> update(@Valid ProductDto objectDTO, String key) {
        return super.update(objectDTO, key);
    }
}
