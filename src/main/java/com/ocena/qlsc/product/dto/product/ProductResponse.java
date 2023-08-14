package com.ocena.qlsc.product.dto.product;

import com.ocena.qlsc.product.dto.image.ProductImageDto;
import lombok.*;

import java.io.Serial;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    @Serial
    private static final long serialVersionUID = 333318718908L;
    private String id;
    private String productId;
    private String productName;
    private Integer amount;
    private List<ProductImageDto> images;
}
