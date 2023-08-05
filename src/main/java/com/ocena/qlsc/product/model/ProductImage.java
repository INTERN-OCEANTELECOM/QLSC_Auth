package com.ocena.qlsc.product.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.*;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Setter
public class ProductImage implements Serializable {
    @Id
    private String id;
    @Column(name = "file_bytes")
    private byte[] fileBytes;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;
    @PrePersist
    private void ensureId() {
        this.setId(UUID.randomUUID().toString());
    }
}
