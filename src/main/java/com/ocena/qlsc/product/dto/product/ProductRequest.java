package com.ocena.qlsc.product.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest implements Serializable {
    private static final long serialVersionUID = 5364187189081705233L;
    @Size(min = 2, message = "Kích thước mã hàng hóa phải lớn hơn 2 chứ số")
    @Pattern(regexp = "^[\\d]*$", message = "Mã hàng hóa chỉ chứa số và không chứa khoảng trắng")
    @NotNull(message = "Dữ liệu không đúng định dạng")
    private String productId;
    @NotNull(message = "ProductName is required")
    private String productName;
    public ProductRequest(String productId) {
        this.productId = productId;
    }
}
