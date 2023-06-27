package com.ocena.qlsc.product.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "product")
public class Product extends BaseModel  implements Serializable {

    @Column(name = "product_id", unique = true)
    @Positive(message = "ID sản phẩm phải là một số nguyên dương")
    private Long productId;

    @Column(name = "product_name")
    @Size(min = 1, message = "Tên sản phẩm là rỗng")
    private String productName;

    public Product(Long productId) {
        this.productId = productId;
    }
}
