package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.product.dto.ErrorResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IPoDetail extends BaseService<PoDetail, PoDetailResponse> {
    ListResponse<ErrorResponse> importPOStatus(MultipartFile file);

    ListResponse<ErrorResponse> importPODetail(MultipartFile file);
}
