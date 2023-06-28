package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPoDetail extends BaseService<PoDetail, PoDetailResponse> {
    ListResponse<ErrorResponseImport> importPOStatus(MultipartFile file);

    ListResponse<ErrorResponseImport> importPODetail(MultipartFile file) throws IOException;
}
