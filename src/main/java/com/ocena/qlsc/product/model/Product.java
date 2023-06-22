package com.ocena.qlsc.product.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.*;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends BaseModel  implements Serializable {

    @Column(name = "product_id", unique = true)
    private Long productId;

    @Column(name = "product_name")
    private String productName;
}
