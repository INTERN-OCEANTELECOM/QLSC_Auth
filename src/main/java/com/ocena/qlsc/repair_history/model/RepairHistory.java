package com.ocena.qlsc.repair_history.model;

import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.common.util.DateUtils;
import com.ocena.qlsc.common.util.ObjectUtils;
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
    public boolean equalsAll(Object obj) {
        RepairHistory value = (RepairHistory) obj;
        return ObjectUtils.equal(this.module, value.getModule())
                && ObjectUtils.equal(this.repairResults.toString(), value.repairResults.toString())
                && ObjectUtils.equal(this.repairError, value.getRepairError())
                && ObjectUtils.equal(this.accessory, value.getAccessory())
                && ObjectUtils.equal(this.repairDate, value.getRepairDate());
    }

    @Override
    public String getKey(boolean isUpdate) {
        if(isUpdate) {
            return String.format("S/N: <%s>; PoNumber: <%s>; TG Tiep Nhan: <%s>",
                    this.poDetail.getSerialNumber(), this.getPoDetail().getPo().getPoNumber(),
                    DateUtils.getCurrentDateByDDMMYYYYhhmmss(this.repairDate));
        } else {
            return String.format("S/N: <%s>; PoNumber: <%s>", this.poDetail.getSerialNumber(), this.poDetail.getPo().getPoNumber());
        }

    }
}
