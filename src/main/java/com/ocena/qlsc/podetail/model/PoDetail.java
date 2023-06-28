package com.ocena.qlsc.podetail.model;

import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.podetail.status.ExportPartner;
import com.ocena.qlsc.podetail.status.KSCVT;
import com.ocena.qlsc.podetail.status.RepairCategory;
import com.ocena.qlsc.podetail.status.RepairStatus;
import com.ocena.qlsc.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_order_detal")
public class PoDetail extends BaseModel {
    @Column(name = "po_detail_id", unique = true)
    private String poDetailId;
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "bbbg_number")
    private String bbbgNumber;
    @Column(name = "import_date")
    private Long importDate;
    @Column(name = "repair_category")
    private Short repairCategory;
    @Column(name = "repair_status")
    private Short repairStatus;
    @Column(name = "export_partner")
    private Short exportPartner;
    @Column(name = "kcs_vt")
    private Short kcsVT;
    @Column(name = "warranty_period")
    private Long warrantyPeriod;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "po_number", referencedColumnName = "po_number")
    private Po po;

    @Override
    public String toString() {
        return "PoDetail{" +
                "poDetailId='" + poDetailId + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", bbbgNumber='" + bbbgNumber + '\'' +
                ", importDate=" + importDate +
                ", repairCategory=" + repairCategory +
                ", repairStatus=" + repairStatus +
                ", exportPartner=" + exportPartner +
                ", kcsVT=" + kcsVT +
                ", warrantyPeriod=" + warrantyPeriod +
                ", product=" + product +
                ", po=" + po +
                '}';
    }
}
