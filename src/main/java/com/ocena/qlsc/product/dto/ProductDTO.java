package com.ocena.qlsc.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    @Positive(message = "ID sản phẩm phải là một số nguyên dương")
    @NotNull(message = "ID sản phẩm không được NULL")
    private Long productId;

    private String productName;

    private Long productQuantity;

    private Long repairStatusSuccessful;

    public ProductDTO(Long productId) {
        this.productId = productId;
    }

    public ProductDTO(Long productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                '}';
    }
}

