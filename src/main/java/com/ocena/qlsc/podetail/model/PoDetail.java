package com.ocena.qlsc.podetail.model;

import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.podetail.status.ExportPartner;
import com.ocena.qlsc.podetail.status.KSCVT;
import com.ocena.qlsc.podetail.status.RepairCategory;
import com.ocena.qlsc.podetail.status.RepairStatus;
import com.ocena.qlsc.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_order_detal")
public class PoDetail extends BaseModel {
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "bbbg_number")
    private String bbbgNumber;
    @Column(name = "import_date")
    private Long importDate;
    @Column(name = "repair_category")
    private RepairCategory repairCategory;
    @Column(name = "repair_status")
    private RepairStatus repairStatus;
    @Column(name = "export_partner")
    private ExportPartner exportPartner;
    @Column(name = "kcs_vt")
    private KSCVT kcsVT;
    @Column(name = "warranty_period")
    private Long warrantyPeriod;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "po_number", referencedColumnName = "po_number")
    private Po po;
}
