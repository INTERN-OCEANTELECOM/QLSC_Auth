package com.ocena.qlsc.repair_history.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.repair_history.dto.RepairHistoryRequest;
import com.ocena.qlsc.repair_history.dto.RepairHistoryResponse;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import com.ocena.qlsc.repair_history.service.RepairHistoryServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/repair-history")
public class RepairHistoryController extends BaseApiImpl<RepairHistory, RepairHistoryRequest, RepairHistoryResponse> {

    @Autowired
    RepairHistoryServiceService repairHistoryService;

    @Override
    protected BaseService<RepairHistory, RepairHistoryRequest, RepairHistoryResponse> getBaseService() {
        return repairHistoryService;
    }

    @ApiShow
    @Override
    public DataResponse<RepairHistoryResponse> addAll(@Valid List<RepairHistoryRequest> objectDTO) {
        repairHistoryService.validateRepairHistoryRequest(objectDTO);
        return super.addAll(objectDTO);
    }

    @ApiShow
    @Override
    public DataResponse<RepairHistoryResponse> update(@Valid RepairHistoryRequest objectDTO, String key) {
        List<RepairHistoryRequest> repairHistoryRequests= new ArrayList<>() {{
            add(objectDTO);
        }};
        repairHistoryService.validateRepairHistoryRequest(repairHistoryRequests);
        return repairHistoryService.updateRepairHistory(objectDTO, key);
    }

    @Override
    public DataResponse<RepairHistoryResponse> add(@Valid RepairHistoryRequest objectDTO) {
        List<RepairHistoryRequest> repairHistoryRequests= new ArrayList<>() {{
            add(objectDTO);
        }};
        repairHistoryService.validateRepairHistoryRequest(repairHistoryRequests);
        return super.add(objectDTO);
    }
}
