package com.ocena.qlsc.repair_history.dto;

import com.ocena.qlsc.podetail.dto.PoDetailRequest;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RepairHistoryRequest {
    @NotBlank(message = "ID is required")
    private String id;
    private String module;
    private String accessory;
    private String repairError;
    private Long repairDate;
    private RepairResults repairResults;
    private String creator;
    private PoDetailRequest poDetail;
}
