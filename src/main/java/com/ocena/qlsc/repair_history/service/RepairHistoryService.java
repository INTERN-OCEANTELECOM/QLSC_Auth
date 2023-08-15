package com.ocena.qlsc.repair_history.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.error.exception.ResourceNotFoundException;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.common.util.StringUtil;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.mapper.PoDetailMapper;
import com.ocena.qlsc.podetail.service.PoDetailService;
import com.ocena.qlsc.repair_history.dto.RepairHistoryRequest;
import com.ocena.qlsc.repair_history.dto.RepairHistoryResponse;
import com.ocena.qlsc.repair_history.mapper.RepairHistoryMapper;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import com.ocena.qlsc.repair_history.repository.RepairHistoryRepository;
import com.ocena.qlsc.user_history.service.HistoryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RepairHistoryService extends BaseServiceImpl<RepairHistory, RepairHistoryRequest, RepairHistoryResponse> implements IRepairHistory{

    @Autowired
    RepairHistoryRepository repairHistoryRepository;

    @Autowired
    RepairHistoryMapper repairHistoryMapper;

    @Autowired
    PoDetailMapper poDetailMapper;

    @Autowired
    HistoryService historyService;

    @Autowired
    PoDetailService poDetailService;

    @Override
    protected BaseRepository<RepairHistory> getBaseRepository() {
        return repairHistoryRepository;
    }

    @Override
    protected BaseMapper<RepairHistory, RepairHistoryRequest, RepairHistoryResponse> getBaseMapper() {
        return repairHistoryMapper;
    }

    @Override
    protected Function<String, Optional<RepairHistory>> getFindByFunction() {
        return getBaseRepository()::findById;
    }

    @Override
    protected Class<RepairHistory> getEntityClass() {
        return RepairHistory.class;
    }

    @Override
    protected Page<RepairHistoryResponse> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<RepairHistory> getListSearchResults(String keyword) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    @Transactional
    public DataResponse<RepairHistoryResponse> updateRepairHistory(RepairHistoryRequest repairHistoryDto, String key) {
        // Update the PO detail record with the new data
        try {
            Optional<RepairHistory> optionalRepairHistory = repairHistoryRepository.findById(key);
            RepairHistory repairHistory = optionalRepairHistory.get();

            repairHistory.setModule(repairHistoryDto.getModule());
            repairHistory.setRepairError(repairHistoryDto.getRepairError());
            repairHistory.setRepairResults(repairHistoryDto.getRepairResults());
            repairHistory.setAccessory(repairHistoryDto.getAccessory());

            repairHistoryRepository.save(repairHistory);

            historyService.updateHistory(RepairHistory.class, key, repairHistory, getBaseMapper().dtoToEntity(repairHistoryDto));
            return ResponseMapper.toDataResponseSuccess("Success");
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException(key + " doesn't exist");
        }
    }
}
