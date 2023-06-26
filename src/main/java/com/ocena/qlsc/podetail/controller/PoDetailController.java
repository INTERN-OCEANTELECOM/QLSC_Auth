package com.ocena.qlsc.podetail.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.service.PoDetailService;
import com.ocena.qlsc.product.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/po-detail")
public class PoDetailController extends BaseApiImpl<PoDetail, PoDetailResponse> {
    @Autowired
    PoDetailService poDetailService;

    @Autowired
    PoDetailRepository poDetailRepository;

    @Override
    protected BaseService getBaseService() {
        return poDetailService;
    }
    
    @Override
    public ListResponse<PoDetailResponse> getAllByPage(int page, int size) {
        return super.getAllByPage(page, size);
    }

    @PostMapping("/import/status")
    public ListResponse<ErrorResponse> importPOStatus(@RequestParam("file") MultipartFile file){
        return poDetailService.importPOStatus(file);
    }
}
