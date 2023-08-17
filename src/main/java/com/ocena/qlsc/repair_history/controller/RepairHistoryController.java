package com.ocena.qlsc.repair_history.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.common.validate.ValidList;
import com.ocena.qlsc.repair_history.dto.RepairHistoryRequest;
import com.ocena.qlsc.repair_history.dto.RepairHistoryResponse;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import com.ocena.qlsc.repair_history.service.RepairHistoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/repair-history")
public class RepairHistoryController extends BaseApiImpl<RepairHistory, RepairHistoryRequest, RepairHistoryResponse> {

    @Autowired
    RepairHistoryService repairHistoryService;

    @Override
    protected BaseService<RepairHistory, RepairHistoryRequest, RepairHistoryResponse> getBaseService() {
        return repairHistoryService;
    }

    @ApiShow
    @Override
    public DataResponse<RepairHistoryResponse> addAll(@Valid ValidList<RepairHistoryRequest> objectDTO) {
        repairHistoryService.validateRepairHistoryRequest(objectDTO.getList());
        return super.addAll(objectDTO);
    }

    /**
     * get Data RepairHistory By SerialNumber and PoNumber
     *
     * @param poDetailId PoDetailId = PoNumber-ProductId-SerialNumber
     * @return
     */
    @ApiShow
    @GetMapping("/get-repair-history-by-serial-and-po-number")
    public ListResponse<RepairHistoryResponse> getRepairHistoryBySerialAndPoNumber(@RequestParam("poDetailId") String poDetailId){
        return repairHistoryService.getRepairHistoryBySerialAndPoNumber(poDetailId);
    }


}
