package com.ocena.qlsc.repair_history.model;

import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(
        name = "repair_history"
)
public class RepairHistory extends BaseModel {
    @Column(name = "module", length = 200)
    private String module;
    @Column(name = "repair_results")
    private RepairResults repairResults;
    @Column(name = "accessory", length = 150)
    private String accessory;
    @Column(name = "repair_error")
    private String repairError;
    @Column(name = "repair_date")
    private Long repairDate;
    @ManyToOne
    @JoinColumn(name = "po_detail_id", referencedColumnName = "id")
    private PoDetail poDetail;

    public RepairHistory(PoDetail poDetail) {
        this.poDetail = poDetail;
    }
    @Override
    public void ensureId() {
        this.setRepairDate(System.currentTimeMillis());
        super.ensureId();
    }

    @Transient
    private int amountInPo;

    @Transient
    private int remainingQuantity;

    private void calculateAmountInPO() {

    }

    public int getAmountInPO() {
        return this.amountInPo;
    }
}
