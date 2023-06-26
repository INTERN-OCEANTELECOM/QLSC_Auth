package com.ocena.qlsc.podetail.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.service.PoDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.Function;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/podetail")
public class PoDetailController extends BaseApiImpl<PoDetail, PoDetailResponse> {
    @Autowired
    PoDetailService poDetailService;
    @Override
    protected BaseService getBaseService() {
        return poDetailService;
    }

    @Override
    protected Function<String, Optional<PoDetail>> getFindByFunction() {
        return null;
    }
}
