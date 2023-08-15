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
    @NotBlank(message = "thiếu thuộc tính id")
    private String id;
    @NotBlank(message = "thiếu thuộc tính module")
    private String module;
    @NotBlank(message = "thiếu thuộc tính accessory")
    private String accessory;
    @NotBlank(message = "thiếu thuộc tính repairError")
    private String repairError;

    private RepairResults repairResults;
    private PoDetailRequest poDetail;
}
