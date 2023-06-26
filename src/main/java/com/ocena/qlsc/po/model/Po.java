package com.ocena.qlsc.po.model;

import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.common.util.SystemUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_order")
public class Po extends BaseModel {
    @Column(name = "po_number", unique = true)
    private String poNumber;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "begin_at")
    private Long beginAt;

    @Column(name = "end_at")
    private Long endAt;

    public Po(String poNumber) {
        this.poNumber = poNumber;
    }
}
