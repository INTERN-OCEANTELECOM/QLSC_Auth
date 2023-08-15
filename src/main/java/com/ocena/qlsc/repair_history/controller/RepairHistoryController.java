package com.ocena.qlsc.repair_history.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.repair_history.dto.RepairHistoryDto;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import com.ocena.qlsc.repair_history.service.RepairHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repair-history")
public class RepairHistoryController extends BaseApiImpl<RepairHistory, RepairHistoryDto> {

    @Autowired
    RepairHistoryService repairHistoryService;

    @Override
    protected BaseService<RepairHistory, RepairHistoryDto> getBaseService() {
        return repairHistoryService;
    }

    @ApiShow
    @Override
    public DataResponse<RepairHistoryDto> add(RepairHistoryDto objectDTO) {
        return super.add(objectDTO);
    }

    @ApiShow
    @Override
    public DataResponse<RepairHistoryDto> update(RepairHistoryDto objectDTO, String key) {
        return repairHistoryService.updateRepairHistory(objectDTO, key);
    }
}
