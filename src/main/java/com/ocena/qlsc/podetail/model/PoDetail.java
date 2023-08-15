package com.ocena.qlsc.podetail.model;

import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_order_detal")
public class PoDetail extends BaseModel implements Serializable {
    @Column(name = "po_detail_id", unique = true)
    private String poDetailId;
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "bbbg_number_import")
    private String bbbgNumberImport;
    @Column(name = "import_date")
    private Long importDate;
    @Column(name = "repair_category")
    private Short repairCategory;
    @Column(name = "repair_status")
    private Short repairStatus;
    @Column(name = "export_partner")
    private Long exportPartner;
    @Column(name = "kcs_vt")
    private Short kcsVT;
    @Column(name = "warranty_period")
    private Long warrantyPeriod;
    @Column(name = "priority")
    private Short priority;
    @Column(name = "bbbg_number_export")
    private String bbbgNumberExport;
    @Column(length = 401)
    private String note;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "po_number", referencedColumnName = "po_number")
    private Po po;

    @OneToMany(mappedBy = "poDetail", cascade = CascadeType.ALL)
    private List<RepairHistory> historyList;
}
