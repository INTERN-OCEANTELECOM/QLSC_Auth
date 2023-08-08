package com.ocena.qlsc.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ProductImageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private byte[] fileBytes;
}
