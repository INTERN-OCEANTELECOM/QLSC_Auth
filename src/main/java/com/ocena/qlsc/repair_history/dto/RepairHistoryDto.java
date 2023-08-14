package com.ocena.qlsc.repair_history.dto;

import com.ocena.qlsc.podetail.dto.PoDetailRequest;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepairHistoryDto {
    private String id;
    private String module;
    private RepairResults repairResults;
    private String accessory;
    private String repairError;
    private String repairDate;
    private PoDetailRequest poDetail;
}
