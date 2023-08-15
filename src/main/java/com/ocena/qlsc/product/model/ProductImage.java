package com.ocena.qlsc.product.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage implements Serializable {
    @Id
    private String id;
    private String filePath;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    public ProductImage(String filePath, Product product) {
        this.filePath = filePath;
        this.product = product;
    }

    @PrePersist
    private void ensureId() {
        this.setId(UUID.randomUUID().toString());
    }
}
