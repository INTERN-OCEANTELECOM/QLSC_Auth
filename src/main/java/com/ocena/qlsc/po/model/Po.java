package com.ocena.qlsc.po.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "Po")
public class Po extends BaseModel {
    @Column(name = "order_quantity")
    private Integer orderQuantity;

    @Column(name = "begin_at")
    private Long beginAt;

    @Column(name = "end_at")
    private Long endAt;
}
