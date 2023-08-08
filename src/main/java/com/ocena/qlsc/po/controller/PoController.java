package com.ocena.qlsc.po.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.error.exception.InvalidTimeException;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.po.dto.PoDto;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.po.service.PoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(value = "po")
@RequiredArgsConstructor
public class PoController extends BaseApiImpl<Po, PoDto> {

    @Autowired
    PoService poService;

    @Autowired
    PoRepository poRepository;

    @Override
    protected BaseService getBaseService() {
        return poService;
    }

    @Override
    @ApiShow
    @Cacheable(value = "getAllPO")
    public ListResponse<PoDto> getAll() {
        return super.getAll();
    }

    @Override
    @ApiShow
    @CacheEvict(value = {"getAllPO", "getPoByPage"}, allEntries = true)
    public DataResponse<PoDto> add(@Valid PoDto poDTO) {
        if(poDTO.getBeginAt() != null && poDTO.getEndAt() != null && poDTO.getBeginAt() > poDTO.getEndAt()) {
            throw new InvalidTimeException("Invalid Time");
        }
        return super.add(poDTO);
    }

    @Override
    @ApiShow
    @CacheEvict(value = {"getAllPO", "getPoByPage"}, allEntries = true)
    public DataResponse<PoDto> update(@Valid PoDto poDTO, String key) {
        poService.validateUpdatePo(poDTO, key);
        return super.update(poDTO, key);
    }

    @ApiShow
    @GetMapping(value = "/{poNumber}")
    public DataResponse<HashMap<String, HashMap<String, Integer>>> getStatisticsByPoNumber(@PathVariable("poNumber") String poNumber) {
        return poService.getStatisticsByPoNumber(poNumber);
    }

    @ApiShow
    @Override
    public ListResponse<PoDto> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return super.searchByKeyword(searchKeywordDto);
    }

    @Override
    @ApiShow
    @Cacheable(value = "getPoByPage")
    public ListResponse<PoDto> getAllByPage(int page, int size) {
        return super.getAllByPage(page, size);
    }
}
