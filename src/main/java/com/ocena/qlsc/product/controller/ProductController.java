package com.ocena.qlsc.product.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @CacheEvict(value = "getProducts", allEntries = true)
    public DataResponse<ProductDTO> add(ProductDTO objectDTO) {
        return super.add(objectDTO);
    }

    @Override
    public ListResponse<ProductDTO> getAll() {
        return super.getAll();
    }

    @GetMapping
    @Cacheable(value = "getProducts")
    public ListResponse<ProductDTO> getProducts(@RequestParam("page") int page,
                                                      @RequestParam("size") int size) {
        System.out.println("Get All Product");
        return super.getAllByPage(page, size);
    }

    @PostMapping("/import")
    @CacheEvict(value = "getProducts", allEntries = true)
    public ListResponse<ErrorResponseImport> importProducts(@RequestParam("file") MultipartFile file) {
        System.out.println("Update Entry");
        return productService.importProducts(file);
    }

    @Override
    public ListResponse<Product> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return super.searchByKeyword(searchKeywordDto);
    }

    @Override
    @CacheEvict(value = "getProducts", allEntries = true)
    public DataResponse<ProductDTO> update(ProductDTO objectDTO, String key) {
        System.out.println("Update Entry");
        return super.update(objectDTO, key);
    }


    @GetMapping("/po")
    public ListResponse<ProductDTO> getProductsByPo(@RequestParam("Po") String Po) {
        return productService.getProductsByPO(Po);
    }
}
