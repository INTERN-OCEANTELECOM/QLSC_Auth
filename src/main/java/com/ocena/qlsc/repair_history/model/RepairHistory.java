package com.ocena.qlsc.repair_history.model;

import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepairHistory extends BaseModel {
    private String module;
    @Column(name = "repair_results")
    private RepairResults repairResults;
    @Column(name = "accessory")
    private String accessory;
    @Column(name = "repair_error")
    private String repairError;
    @Column(name = "repair_date")
    private String repairDate;
    @Column(name = "repair_person")
    private String repairPerson;
    @ManyToOne
    @JoinColumn(name = "po_detail_id", referencedColumnName = "id")
    private PoDetail poDetail;
}
