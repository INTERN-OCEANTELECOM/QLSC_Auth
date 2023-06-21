package com.ocena.qlsc.product.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "product")
@CrossOrigin(value = "*")
public class ProductController extends BaseApiImpl<Product, ProductDTO> {

    @Autowired
    ProductService productService;

    @Override
    protected BaseService<Product, ProductDTO> getBaseService() {
        return productService;
    }

    @Override
    public DataResponse<Product> add(ProductDTO objectDTO) {
        return super.add(objectDTO);
    }
}
