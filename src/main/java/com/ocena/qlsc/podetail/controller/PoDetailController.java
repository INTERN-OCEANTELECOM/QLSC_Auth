package com.ocena.qlsc.podetail.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/podetail")
public class PoDetailController extends BaseApiImpl<PoDetail, PoDetailResponse> {
    @Override
    protected BaseService getBaseService() {
        return null;
    }
}
