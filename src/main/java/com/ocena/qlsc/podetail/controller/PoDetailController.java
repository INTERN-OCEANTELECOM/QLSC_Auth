package com.ocena.qlsc.podetail.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.service.PoDetailService;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

    @Override
    public DataResponse<PoDetailResponse> update(PoDetailResponse objectDTO, String key) {
        return poDetailService.updatePoDetail(objectDTO, key);
    }

    @PostMapping("/update")
    public ListResponse<ErrorResponseImport> updateRepairStatus(@RequestParam("file") MultipartFile file,
                                                                @RequestParam("attribute") String attribute) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return poDetailService.processFileUpdatePoDetail(file, attribute);
    }

    @PostMapping("/import")
    public ListResponse<ErrorResponseImport> importPODetail(@RequestParam("file") MultipartFile file) {
        return poDetailService.importPODetail(file);
    }

}
