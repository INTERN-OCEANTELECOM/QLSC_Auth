package com.ocena.qlsc.repair_history.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocena.qlsc.common.constants.TimeConstants;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.error.exception.DataNotFoundException;
import com.ocena.qlsc.common.error.exception.InvalidTimeException;
import com.ocena.qlsc.common.error.exception.ResourceNotFoundException;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.common.util.EnumUtils;
import com.ocena.qlsc.common.util.StringUtils;
import com.ocena.qlsc.common.util.SystemUtils;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.mapper.PoDetailMapper;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.service.PoDetailService;
import com.ocena.qlsc.repair_history.dto.RepairHistoryRequest;
import com.ocena.qlsc.repair_history.dto.RepairHistoryResponse;
import com.ocena.qlsc.repair_history.enumrate.RepairResults;
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

@Service
public class RepairHistoryService extends BaseServiceImpl<RepairHistory, RepairHistoryRequest, RepairHistoryResponse> implements IRepairHistoryService {

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

    @Autowired
    PoDetailRepository poDetailRepository;

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

    private int countSerialWithAllIsOK(List<RepairHistory> repairHistories) {
        Map<String, Set<RepairResults>> serialNumberStatus = new HashMap<>();
        int count = 0;

        for (RepairHistory history : repairHistories) {
            serialNumberStatus.putIfAbsent(history.getPoDetail().getSerialNumber(), new HashSet<>());
            serialNumberStatus.get(history.getPoDetail().getSerialNumber()).add(history.getRepairResults());
        }

        for (String serial : serialNumberStatus.keySet()) {
            if (serialNumberStatus.get(serial).size() > 0
                    && serialNumberStatus.get(serial).stream()
                    .allMatch(repairResults -> repairResults == RepairResults.OK))
                count++;
        }

        return count;
    }

    private void filterByCreatorAndRepairResult(String creator, String repairResult, List<PoDetail> poDetails) {
        Iterator<PoDetail> iteratorPoDetail = poDetails.iterator();

        while (iteratorPoDetail.hasNext()) {
            PoDetail poDetail = iteratorPoDetail.next();
            Iterator<RepairHistory> iterator = poDetail.getRepairHistories().iterator();
            while (iterator.hasNext()) {
                RepairHistory repairHistory = iterator.next();
                if((creator == null || creator.equals(repairHistory.getCreator()))
                        && (repairResult == null || repairResult.equals(repairHistory.getRepairResults().toString()))) {
                    continue;
                }
                iterator.remove();
            }
            if(creator != null || repairResult != null) {
                if(poDetail.getRepairHistories().isEmpty()) {
                    iteratorPoDetail.remove();
                }
            }
        }
    }

    protected Page<PoDetailResponse> getPageResult(SearchKeywordDto searchKeywordDto, Pageable pageable, boolean allNullAndEmpty) {
        String productName = searchKeywordDto.getKeyword().get(0);
        String serialNumber = searchKeywordDto.getKeyword().get(1);
        String poNumber = searchKeywordDto.getKeyword().get(2);
        String creator = searchKeywordDto.getKeyword().get(3);
        String repairResult = searchKeywordDto.getKeyword().get(4);


        Page<PoDetail> pageSearchRepairHistory = repairHistoryRepository
                .searchRepairHistory(serialNumber, poNumber, productName, pageable);

        List<PoDetail> poDetails = new ArrayList<>(pageSearchRepairHistory.getContent());

        filterByCreatorAndRepairResult(creator, repairResult, poDetails);

        List<PoDetailResponse> listPoDetailResponse = poDetails.stream().map(poDetail -> {
            PoDetailResponse poDetailResponse = poDetailMapper.entityToDto(poDetail);
            int amountInPO = poDetailRepository.countByProductIdAndPoNumber(poDetail.getProduct().getProductId(),
                    poDetail.getPo().getPoNumber());
            int countRepairIsOK = 0;
            List<RepairHistory> repairHistories = repairHistoryRepository.findByPoDetailId(
                    poDetail.getPoDetailId());
            countRepairIsOK = countSerialWithAllIsOK(repairHistories);
            poDetailResponse.setAmountInPo(amountInPO);
            poDetailResponse.setRemainingQuantity(amountInPO - countRepairIsOK);
            return poDetailResponse;
        }).collect(Collectors.toList());

        return new PageImpl<>(listPoDetailResponse, pageable, listPoDetailResponse.size());
    }

    public ListResponse<PoDetailResponse> getAllByListKeyword(SearchKeywordDto searchKeywordDto) {
        boolean allNullAndEmpty = searchKeywordDto.getKeyword()
                .stream()
                .allMatch(str -> str == null || str.trim().isEmpty());
        Pageable pageable = null;
        if (!allNullAndEmpty || searchKeywordDto.getProperty().equals("ALL")) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        } else {
            pageable = PageRequest.of(searchKeywordDto.getPageIndex(), searchKeywordDto.getPageSize());
        }
        return ResponseMapper.toPagingResponseSuccess(getPageResult(searchKeywordDto, pageable, allNullAndEmpty));
    }

    public void validateRepairHistoryRequest(List<RepairHistoryRequest> repairHistoryRequest){
        List<RepairHistory> repairHistoryList = repairHistoryRequest
                .stream()
                .map(repairHistory -> {
                    if (repairHistory.getId() == null) {
                        return new RepairHistory(poDetailRepository.findById(repairHistory.getPoDetail().getId()).get());
                    } else {
                        return repairHistoryRepository.findById(repairHistory.getId()).orElse(null);
                    }
                }).toList();

        for (RepairHistory repairHistory : repairHistoryList) {
            if (repairHistory.getPoDetail().getPo().getEndAt() != null && repairHistory.getPoDetail().getPo().getEndAt() < SystemUtils.getCurrentTime()) {
                throw new InvalidTimeException(repairHistory.getPoDetail().getPo().getPoNumber() + " Invalid Time");
            }

            if (repairHistory.getId() != null) {
                if (!repairHistory.getRepairResults().name().equals(RepairResults.DANG_SC.name())
                        && repairHistory.getCreated() + TimeConstants.REPAIR_HISTORY_LIMIT_TIME < SystemUtils.getCurrentTime()) {
                    throw new InvalidTimeException(repairHistory.getPoDetail().getSerialNumber() + " Invalid Time");
                }
            }
        }
    }

    public ListResponse<RepairHistoryResponse> getRepairHistoryBySerialAndPoNumber(String poDetailId, String repairHistoryId) {
        try {
            List<String> splitList = StringUtils.splitDashToList(poDetailId);
            String poNumber = splitList.get(0);
            String productId = splitList.get(1);
            String serial = splitList.get(2);

            List<RepairHistory> repairHistoryList = repairHistoryRepository.getBySerialAndPoNumber(serial, poNumber);

            List<RepairHistoryResponse> repairHistoryResponseList = Objects.requireNonNull(repairHistoryList
                            .stream()
                            .filter(repairHistory -> repairHistory.getId().equals(repairHistoryId))
                            .findFirst()
                            .map(foundRepairHistory -> {
                                repairHistoryList.remove(foundRepairHistory);
                                repairHistoryList.add(0, foundRepairHistory);
                                return repairHistoryList;
                            })
                            .orElse(new ArrayList<>()))
                            .stream()
                            .map(repairHistory -> repairHistoryMapper.entityToDto(repairHistory)).toList();

            int amountInPO = poDetailRepository.countByProductIdAndPoNumber(productId, poNumber);

            if (repairHistoryResponseList.isEmpty()) {
                repairHistoryResponseList = new ArrayList<>() {{
                    add(RepairHistoryResponse.builder()
                            .poDetail(poDetailMapper.entityToDto(poDetailRepository.findByPoDetailId(poDetailId).get()))
                            .build());
                }};
            } else {
                int countSerialWithAllIsOK = countSerialWithAllIsOK(repairHistoryList);
                repairHistoryResponseList.forEach(repairHistory -> {
                });
            }
            return ResponseMapper.toListResponseSuccess(repairHistoryResponseList);
        } catch (NoSuchElementException ignore) {
            throw new DataNotFoundException("Not Found");
        }
    }

    @Override
    protected Page<RepairHistoryResponse> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<RepairHistory> getListSearchResults(String keyword) {
        return null;
    }
}
