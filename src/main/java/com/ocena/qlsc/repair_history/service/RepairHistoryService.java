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
import com.ocena.qlsc.podetail.dto.PoDetailDto;
import com.ocena.qlsc.podetail.mapper.PoDetailMapper;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.service.PoDetailService;
import com.ocena.qlsc.repair_history.dto.RepairHistoryDto;
import com.ocena.qlsc.repair_history.mapper.RepairHistoryMapper;
import com.ocena.qlsc.repair_history.model.RepairHistory;
import com.ocena.qlsc.repair_history.repository.RepairHistoryRepository;
import com.ocena.qlsc.user_history.service.HistoryService;
import org.apache.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
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
public class RepairHistoryService extends BaseServiceImpl<RepairHistory, RepairHistoryDto> implements IRepairHistory{

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
    protected BaseMapper<RepairHistory, RepairHistoryDto> getBaseMapper() {
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
    protected Page<RepairHistoryDto> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        //Get Data From PoDetail
        List<String> keywordSearchPoDetail = new ArrayList<>();
        keywordSearchPoDetail.add(null);
        keywordSearchPoDetail.add(searchKeywordDto.getKeyword().get(1));
        keywordSearchPoDetail.add(searchKeywordDto.getKeyword().get(2));
        keywordSearchPoDetail.add(null);
        keywordSearchPoDetail.add(null);
        keywordSearchPoDetail.add(null);
        keywordSearchPoDetail.add(null);
        keywordSearchPoDetail.add(null);
        keywordSearchPoDetail.add(null);
        keywordSearchPoDetail.add(null);

        SearchKeywordDto searchKeywordDtoPoDetail = SearchKeywordDto.builder()
                .keyword(keywordSearchPoDetail)
                .build();

        List<PoDetailDto> poDetailDtoList = poDetailService.getAllByListKeyword(searchKeywordDtoPoDetail).getData();

        List<RepairHistory> poDetailDtoToRepairHistory = poDetailDtoList
                .stream()
                .map(poDetailDto -> RepairHistory
                        .builder()
                        .poDetail(poDetailMapper.dtoToEntity(poDetailDto))
                        .build())
                .toList();
        /////
        List<String> listSerialNumbers = StringUtil.splitStringToList(searchKeywordDto.getKeyword().get(1));
        List<String> listPoNumbers = StringUtil.splitStringToList(searchKeywordDto.getKeyword().get(2));

        Pageable page = pageable;

        if(!listPoNumbers.isEmpty() || !listSerialNumbers.isEmpty()){
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        }

        Page<RepairHistory> repairHistoryDtos = repairHistoryRepository
                .searchRepairHistory(searchKeywordDto.getKeyword().get(0),
                        searchKeywordDto.getKeyword().get(3),
                        searchKeywordDto.getKeyword().get(4),pageable);

        if (listSerialNumbers.isEmpty() && listPoNumbers.isEmpty()) {
            return repairHistoryDtos.map(repairHistory -> repairHistoryMapper.entityToDto(repairHistory));
        }

        List<RepairHistory> repairHistoryListSearch = repairHistoryDtos.getContent()
                .stream()
                .filter(repairHistory -> listSerialNumbers.contains(repairHistory.getPoDetail().getSerialNumber())
                        || listSerialNumbers.isEmpty())
                .toList()
                .stream()
                .filter(repairHistory -> listPoNumbers.contains(repairHistory.getPoDetail().getPo().getPoNumber())
                        || listPoNumbers.isEmpty())
                .toList();

        poDetailDtoToRepairHistory.forEach(System.out::println);
        repairHistoryListSearch.forEach(System.out::println);

        List<RepairHistory> mergeList = new ArrayList<>(Stream.concat(poDetailDtoToRepairHistory.stream(), repairHistoryListSearch.stream())
                .collect(Collectors.toMap(RepairHistory::getPoDetail, repairHistory -> repairHistory, (s1, s2) -> s1))
                .values());

        //Create Page with Start End
        List<RepairHistory> pageRepairHistory = mergeList
                .subList(page.getPageNumber() * page.getPageSize(),
                        Math.min(page.getPageNumber() * page.getPageSize() + page.getPageSize(), mergeList.size()));
        return new PageImpl<>(pageRepairHistory, page, mergeList.size()).map(repairHistory -> repairHistoryMapper.entityToDto(repairHistory));
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
    public DataResponse<RepairHistoryDto> updateRepairHistory(RepairHistoryDto repairHistoryDto, String key) {
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

    public ListResponse<RepairHistoryDto> getAllByListKeyword(SearchKeywordDto searchKeywordDto){
        Page<RepairHistoryDto> historyDtosPage = getPageResults(searchKeywordDto, PageRequest.of(0, Integer.MAX_VALUE));
        return ResponseMapper.toListResponseSuccess(historyDtosPage.getContent());
    }


}
