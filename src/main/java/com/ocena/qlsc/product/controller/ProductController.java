package com.ocena.qlsc.product.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.product.dto.product.ProductRequest;
import com.ocena.qlsc.product.dto.product.ProductResponse;
import com.ocena.qlsc.product.model.Product;
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
public class ProductController extends BaseApiImpl<Product, ProductRequest, ProductResponse> {
    @Autowired
    ProductService productService;
    @Override
    protected BaseService<Product, ProductRequest, ProductResponse> getBaseService() {
        return productService;
    }
    @Override
    @ApiShow
    public DataResponse<ProductResponse> add(@Valid ProductRequest productRequest) {
        return super.add(productRequest);
    }
    @ApiShow
    @PostMapping("/create")
    @CacheEvict(value = {"findAllProduct"}, allEntries = true)
    public DataResponse<ProductResponse> createProduct(@ModelAttribute @Valid ProductRequest productRequest,
                                                  @RequestParam("files") List<MultipartFile> files) {
        return productService.createProduct(files, productRequest);
    }
    @ApiShow
    @PutMapping("/update-product/{productId}")
    @CacheEvict(value = {"findAllProduct"}, allEntries = true)
    public DataResponse<ProductResponse> updateProduct(@ModelAttribute @Valid ProductRequest productRequest,
                                                  @PathVariable("productId") String productId,
                                                  @RequestParam("files") List<MultipartFile> files) {
        return productService.updateProduct(files, productRequest, productId);
    }
    @Override
    @ApiShow
    public ListResponse<ProductResponse> getAll() {
        return productService.getAllProduct();
    }
    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @GetMapping("/get-by-pages")
    public ListResponse<ProductResponse> getProductByPage(@Param("page") int page, @Param("size") int size) {
        return productService.getPagedProducts(page, size);
    }
    @ApiShow
    @GetMapping("/get-by-id/{id}")
    public DataResponse<ProductResponse> getProductByID(@PathVariable("id") String id) {
        return productService.getProductById(id);
    }
    @Override
    @ApiShow
    public ListResponse<ProductResponse> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return super.searchByKeyword(searchKeywordDto);
    }
}
