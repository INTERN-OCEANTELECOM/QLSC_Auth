package com.ocena.qlsc.product.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ProductImage extends BaseModel {
    private byte[] fileBytes;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;
}
