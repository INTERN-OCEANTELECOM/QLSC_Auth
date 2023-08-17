package com.ocena.qlsc.repair_history.service;

import com.ocena.qlsc.common.constants.TimeConstants;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.error.exception.InvalidTimeException;
import com.ocena.qlsc.common.error.exception.ResourceNotFoundException;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.common.util.StringUtil;
import com.ocena.qlsc.common.util.SystemUtil;
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
            System.out.println(history.getPoDetail().getSerialNumber());
            System.out.println(history.getRepairResults());
            serialNumberStatus.putIfAbsent(history.getPoDetail().getSerialNumber(), new HashSet<>());
            serialNumberStatus.get(history.getPoDetail().getSerialNumber()).add(history.getRepairResults());
        }

        for (String serial : serialNumberStatus.keySet()) {
            System.out.println(RepairResults.OK);
            System.out.println(serial);
            serialNumberStatus.get(serial).forEach(System.out::println);
            if (serialNumberStatus.get(serial).size() > 0
                    && serialNumberStatus.get(serial).stream()
                    .allMatch(repairResults -> repairResults == RepairResults.OK))
                count++;
        }

        return count;
    }

    protected Page<PoDetailResponse> getPageResult(SearchKeywordDto searchKeywordDto, Pageable pageable, boolean allNullAndEmpty) {
        String productName = searchKeywordDto.getKeyword().get(0);
        List<String> listSerialNumber = StringUtil.splitStringToList2(searchKeywordDto.getKeyword().get(1));
        List<String> listPoNumber = StringUtil.splitStringToList2(searchKeywordDto.getKeyword().get(2));
        String creator = searchKeywordDto.getKeyword().get(3);
        String repairResult = searchKeywordDto.getKeyword().get(4);
        System.out.println(listSerialNumber);
        System.out.println(listPoNumber);
        System.out.println(productName);
        System.out.println(creator);
        System.out.println(repairResult);

        Page<PoDetail> pageSearchRepairHistory = repairHistoryRepository
                .searchRepairHistory(listSerialNumber, listPoNumber, productName, pageable);


        if (allNullAndEmpty) {
            return pageSearchRepairHistory.map(poDetail -> poDetailMapper.entityToDto(poDetail));
        }

        System.out.println("Xuong day");

        for (PoDetail poDetail: pageSearchRepairHistory.getContent()) {
            Iterator<RepairHistory> iterator = poDetail.getRepairHistories().iterator();
            while (iterator.hasNext()) {
                RepairHistory repairHistory = iterator.next();
                // Thực hiện kiểm tra và thay đổi nếu cần
                if (!(repairHistory.getCreator().equals(creator) || creator == null)
                        || !(repairHistory.getRepairResults().toString().equals(repairResult) || repairResult == null)) {
                    iterator.remove(); // Loại bỏ phần tử hiện tại
                }
            }
        }

        return pageSearchRepairHistory.map(poDetail -> {
            PoDetailResponse poDetailResponse = poDetailMapper.entityToDto(poDetail);


            for (RepairHistoryResponse repairHistoryResponse : poDetailResponse.getRepairHistories()) {

                int amountInPO = poDetailRepository.countByProductIdAndPoNumber(poDetail.getProduct().getProductId(),
                        poDetail.getPo().getPoNumber());
                List<RepairHistory> repairHistories = repairHistoryRepository.findByProductIdAndSerialNumber(
                        poDetail.getProduct().getProductId(), poDetail.getSerialNumber());
                int count = countSerialWithAllIsOK(repairHistories);

                repairHistoryResponse.setAmountInPo(amountInPO);
                repairHistoryResponse.setRemainingQuantity(amountInPO - count);
            }
            return poDetailResponse;
        });
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

    @Override
    protected List<String> getListKey(List<RepairHistoryRequest> objDTO) {
        return objDTO.stream().map(RepairHistoryRequest::getId).collect(Collectors.toList());
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

    public void validateRepairHistoryRequest(List<RepairHistoryRequest> repairHistoryRequest) {
        List<RepairHistory> repairHistoryList = repairHistoryRequest.stream().map(value -> repairHistoryRepository.findById(value.getId())
                        .orElse(new RepairHistory(poDetailRepository.findById(value.getPoDetail().getId()).get())))
                .toList();

        for (RepairHistory repairHistory : repairHistoryList) {
            if (repairHistory.getPoDetail().getPo().getEndAt() != null && repairHistory.getPoDetail().getPo().getEndAt() < SystemUtil.getCurrentTime()) {
                throw new InvalidTimeException(repairHistory.getPoDetail().getPo().getPoNumber() + " Invalid Time");
            }

            if (repairHistory.getId() != null) {
                if (!repairHistory.getRepairResults().name().equals(RepairResults.DANG_SC.name())
                        && repairHistory.getCreated() + TimeConstants.REPAIR_HISTORY_LIMIT_TIME < SystemUtil.getCurrentTime()) {
                    throw new InvalidTimeException(repairHistory.getPoDetail().getSerialNumber() + " Invalid Time");
                }
            }
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
