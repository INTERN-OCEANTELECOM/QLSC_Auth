package com.ocena.qlsc.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @Positive(message = "ID sản phẩm phải là một số nguyên dương")
    @JsonProperty("productId")
    private Long productId;

    private String productName;

    public ProductDTO(Long productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                '}';
    }
}

