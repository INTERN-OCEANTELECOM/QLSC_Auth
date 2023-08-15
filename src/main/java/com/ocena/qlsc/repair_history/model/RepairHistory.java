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
public class RepairHistory extends BaseModel {
    private String module;
    @Column(name = "repair_results")
    private RepairResults repairResults;
    @Column(name = "accessory")
    private String accessory;
    @Column(name = "repair_error")
    private String repairError;
    @Column(name = "repair_date")
    private Long repairDate;
    @Column(name = "repair_person")
    private String repairPerson;

    @ManyToOne
    @JoinColumn(name = "po_detail_id", referencedColumnName = "po_detail_id")
    private PoDetail poDetail;

    @Override
    public void ensureId() {
        this.setRepairPerson(SystemUtil.getCurrentEmail());
        this.setRepairDate(System.currentTimeMillis());
        super.ensureId();
    }
}
