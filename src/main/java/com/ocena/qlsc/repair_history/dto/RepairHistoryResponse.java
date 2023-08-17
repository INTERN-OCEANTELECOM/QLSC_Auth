package com.ocena.qlsc.repair_history.dto;

import com.ocena.qlsc.podetail.dto.PoDetailRequest;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepairHistoryResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String module;
    private RepairResults repairResults;
    private String accessory;
    private String repairError;
    private Long repairDate;
    private String creator;
    private int amountInPo;
    private int remainingQuantity;
    private PoDetailRequest poDetail;

}
