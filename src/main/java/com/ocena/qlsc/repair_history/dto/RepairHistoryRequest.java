package com.ocena.qlsc.repair_history.dto;

import com.ocena.qlsc.podetail.dto.PoDetailRequest;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class RepairHistoryRequest {
    @NotNull(message = "ID is required")
    private String id;
    private String module;
    private String accessory;
    private String repairError;
    private Long repairDate;
    private RepairResults repairResults;
    private String creator;
    private PoDetailRequest poDetail;
}
