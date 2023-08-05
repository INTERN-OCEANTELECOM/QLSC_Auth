package com.ocena.qlsc.product.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ProductImageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private byte[] fileBytes;
}
