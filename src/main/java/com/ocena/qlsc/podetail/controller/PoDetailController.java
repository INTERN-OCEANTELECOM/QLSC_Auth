package com.ocena.qlsc.podetail.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.constants.RegexConstants;
import com.ocena.qlsc.podetail.dto.PoDetailDto;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.service.PoDetailService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;

@RestController
//@CrossOrigin(value = "*")
@RequestMapping("/po-detail")
public class PoDetailController extends BaseApiImpl<PoDetail, PoDetailDto> {
    @Autowired
    PoDetailService poDetailService;

    @Autowired
    PoDetailRepository poDetailRepository;

    @Override
    protected BaseService getBaseService() {
        return poDetailService;
    }
    
    @Override
    @ApiShow
    public ListResponse<PoDetailDto> getAllByPage(int page, int size) {
        return super.getAllByPage(page, size);
    }

    @Override
    @ApiShow
    public DataResponse<PoDetailDto> update(@Valid PoDetailDto poDetailResponse, String key) {
        return poDetailService.updatePoDetail(poDetailResponse, key);
    }

    @PostMapping("/deleteByID")
    @ApiShow
    public DataResponse<String> deleteByID(@RequestParam("id") String id) {
        return poDetailService.deletePoDetail(id);
    }

    @GetMapping("/getByPo/{id}")
    @ApiShow
    public ListResponse<PoDetailDto> getByPO(@PathVariable("id") String poNumber) {
        return poDetailService.getByPO(poNumber);
    }

    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @PostMapping("/update")
    public ListResponse<?> updateFromExcel(@RequestParam("file") MultipartFile file) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return poDetailService.updatePoDetailFromExcel(file);
    }

    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @PostMapping("/import")
    public ListResponse<?> importFromExcel(@RequestParam("file") MultipartFile file) {
        return poDetailService.importPODetailFromExcel(file);
    }

    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @PostMapping("/update/import-date")
    public DataResponse updateImportDate(@RequestParam("list") String listPoDetailId) {
        return poDetailService.updateImportDateOrExportPartner(listPoDetailId, RegexConstants.FIELDS_REGEX_MAP.get(RegexConstants.REGEX_IMPORT_DATE));
    }

    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @PostMapping("/update/export-partner")
    public DataResponse updateExportPartner(@RequestParam("list") String listPoDetailId) {
        return poDetailService.updateImportDateOrExportPartner(listPoDetailId, RegexConstants.FIELDS_REGEX_MAP.get(RegexConstants.REGEX_EXPORT_PARTNER));
    }

    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @PostMapping("/search/serialNumber")
    public ListResponse<PoDetailDto> searchBySerialNumbers(@RequestParam("file") MultipartFile file) {
        return poDetailService.searchBySerialNumbers(file);
    }

    @ApiShow
    @Override
    public ListResponse<PoDetailDto> getAll() {
        return super.getAll();
    }

    @ApiShow
    @Override
    public ListResponse<PoDetailDto> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return searchKeywordDto.getProperty().equals("ALL") ?
                poDetailService.getAllByListKeyword(searchKeywordDto) :
                super.searchByKeyword(searchKeywordDto);
    }

    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @GetMapping("/serialNumber")
    public ListResponse<PoDetailDto> getBySerialNumber(String serialNumber) {
        return poDetailService.getBySerialNumber(serialNumber);
    }
}
