package com.ocena.qlsc.po.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.error.exception.InvalidTimeException;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.po.service.PoService;
import io.swagger.v3.oas.annotations.Hidden;
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
public class PoController extends BaseApiImpl<Po, PoDTO> {

    @Autowired
    PoService poService;

    @Autowired
    PoRepository poRepository;

    @Override
    protected BaseService getBaseService() {
        return poService;
    }

    @Override
    @Cacheable(value = "getAllPO")
    public ListResponse<PoDTO> getAll() {
        return super.getAll();
    }

    @Override
    @CacheEvict(value = {"getAllPO", "getPoByPage"}, allEntries = true)
    public DataResponse<PoDTO> add(@Valid PoDTO poDTO) {
        if(poDTO.getBeginAt() != null && poDTO.getEndAt() != null && poDTO.getBeginAt() > poDTO.getEndAt()) {
            throw new InvalidTimeException("Invalid Time");
        }
        return super.add(poDTO);
    }

    @Override
    @CacheEvict(value = {"getAllPO", "getPoByPage"}, allEntries = true)
    public DataResponse<PoDTO> update(@Valid PoDTO poDTO, String key) {
        poService.validateUpdatePo(poDTO, key);
        return super.update(poDTO, key);
    }

    @GetMapping(value = "/{poNumber}")
    public DataResponse<HashMap<String, HashMap<String, Integer>>> getStatisticsByPoNumber(@PathVariable("poNumber") String poNumber) {
        return poService.getStatisticsByPoNumber(poNumber);
    }

    @Override
    public ListResponse<PoDTO> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return super.searchByKeyword(searchKeywordDto);
    }

    @Override
    @Cacheable(value = "getPoByPage")
    public ListResponse<PoDTO> getAllByPage(int page, int size) {
        return super.getAllByPage(page, size);
    }

    /*Use For Swagger*/
    @Hidden
    @Override
    public DataResponse<PoDTO> getById(String id) {
        return null;
    }
    @Hidden
    @Override
    public DataResponse<PoDTO> delete(String id) {
        return null;
    }
    @Hidden
    @Override
    public ListResponse<PoDTO> getByIds(String ids) {
        return null;
    }
    @Hidden
    @Override
    public ListResponse<Po> getAllByKeyword(String keyword) {
        return null;
    }
}
