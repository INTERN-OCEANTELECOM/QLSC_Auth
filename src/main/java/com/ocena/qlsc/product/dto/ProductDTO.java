package com.ocena.qlsc.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ocena.qlsc.product.model.ProductImage;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 5364187189081705233L;

    @Size(min = 2, message = "Kích thước mã hàng hóa phải lớn hơn 2 chứ số")
    @Pattern(regexp = "^[\\d]*$", message = "Mã hàng hóa chỉ chứa số và không chứa khoảng trắng")
    @NotNull(message = "Dữ liệu không đúng định dạng")
    private String productId;

    private String productName;

    private Integer amount;

    private List<ProductImageDTO> images;

    public ProductDTO(String productId) {
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

