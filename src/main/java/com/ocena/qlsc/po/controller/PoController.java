package com.ocena.qlsc.po.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.po.service.PoService;
import com.ocena.qlsc.podetail.model.PoDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping(value = "po")
@CrossOrigin(value = "*")
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
        System.out.println("GetAll");
        return super.getAll();
    }

    @Override
    @CacheEvict(value = "getAllPO", allEntries = true)
    public DataResponse<PoDTO> add(PoDTO objectDTO) {
        System.out.println("Update Entry");
        return (poService.validationPoRequest(objectDTO, false, null) == null) ? super.add(objectDTO) : poService.validationPoRequest(objectDTO, false, null);
    }


    @Override
    @CacheEvict(value = "getAllPO", allEntries = true)
    public DataResponse<PoDTO> update(PoDTO objectDTO, String key) {
        System.out.println("Update Entry");
        return (poService.validationPoRequest(objectDTO, true, key) == null) ?
                super.update(objectDTO, key) :
                poService.validationPoRequest(objectDTO, true, key);
    }

    @GetMapping(value = "/{poNumber}")
    public DataResponse<HashMap<String, HashMap<String, Integer>>> getStatisticsByPoNumber(@PathVariable("poNumber") String poNumber) {
        return poService.getStatisticsByPoNumber(poNumber);
    }
}
