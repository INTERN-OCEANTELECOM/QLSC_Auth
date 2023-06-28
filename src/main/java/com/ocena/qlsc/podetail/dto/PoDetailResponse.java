package com.ocena.qlsc.podetail.dto;

import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoDetailResponse {
    private String serialNumber;
    private String bbbgNumber;
    private Long importDate;
    private Short repairCategory;
    private Short repairStatus;
    private Short exportPartner;
    private Short kcsVT;
    private Long warrantyPeriod;
    private ProductDTO product;
    private Po po;
}
