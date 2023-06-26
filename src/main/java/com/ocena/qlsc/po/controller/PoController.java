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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ListResponse<PoDTO> getAll() {
        return super.getAll();
    }

    @Override
    public DataResponse<Po> add(PoDTO objectDTO) {
        return (poService.validationPoRequest(objectDTO, false, null) == null) ? super.add(objectDTO) : poService.validationPoRequest(objectDTO, false, null);
    }

    @Override
    public DataResponse<Po> update(PoDTO objectDTO, String key) {
        return (poService.validationPoRequest(objectDTO, true, key) == null) ?
                super.update(objectDTO, key) :
                poService.validationPoRequest(objectDTO, true, key);
    }
}
