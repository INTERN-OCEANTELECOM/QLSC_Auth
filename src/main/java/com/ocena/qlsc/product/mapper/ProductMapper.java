package com.ocena.qlsc.product.mapper;

import com.ocena.qlsc.common.model.BaseMapperImpl;
import com.ocena.qlsc.product.dto.ProductDto;
import com.ocena.qlsc.product.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper extends BaseMapperImpl<Product, ProductDto> {

    public ProductMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }


    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }

    @Override
    protected Class<ProductDto> getDtoClass() {
        return ProductDto.class;
    }
}
