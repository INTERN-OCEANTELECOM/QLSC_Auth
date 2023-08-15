package com.ocena.qlsc.repair_history.dto;

import com.ocena.qlsc.podetail.dto.PoDetailDto;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RepairHistoryDto {
    private String id;
    private String module;
    private RepairResults repairResults;
    private String accessory;
    private String repairError;
    private Long repairDate;
    private String repairPerson;
    private String poDetailId;
}
