package com.ocena.qlsc.podetail.dto;

import com.ocena.qlsc.po.dto.PoDto;
import com.ocena.qlsc.product.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoDetailResponse {
    private ProductDto product;
    private String serialNumber;
    private PoDto po;
}
