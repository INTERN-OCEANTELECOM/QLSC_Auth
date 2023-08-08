package com.ocena.qlsc.po.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PoDto implements Serializable {

    @Size(min = 1, message = "Số hợp đồng không được để trống")
    private String contractNumber;
    @Size(min = 1, message = "Số PO không được để trống")
    private String poNumber;

//    @NotNull(message = "quantity must not be null")
//    @Min(value = 1, message = "quantity must be greater than 0")
    private Integer quantity;

//    @NotNull(message = "beginAt must not be null")
//    @Min(value = 1, message = "beginAt must be greater than 0")
    private Long beginAt;

//    @NotNull(message = "endAt must not be null")
//    @Min(value = 1, message = "endAt must be greater than 0")
    private Long endAt;

    private Long warrantyExpirationDate;

    private Long contractWarrantyExpirationDate;

    @Size(max = 400, message = "Ghi chú phải nhỏ hơn 400 ký tự")
    private String note;

    public PoDto(String poNumber) {
        this.poNumber = poNumber;
    }
}
