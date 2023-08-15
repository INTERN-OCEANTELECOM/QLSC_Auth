package com.ocena.qlsc.podetail.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.podetail.dto.PoDetailRequest;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.service.PoDetailService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;

@RestController
//@CrossOrigin(value = "*")
@RequestMapping("/po-detail")
public class PoDetailController extends BaseApiImpl<PoDetail, PoDetailRequest, PoDetailResponse> {
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
    public ListResponse<PoDetailResponse> getAllByPage(int page, int size) {
        return super.getAllByPage(page, size);
    }
    @Override
    @ApiShow
    public DataResponse<PoDetailResponse> update(PoDetailRequest poDetailRequest, String key) {
        return poDetailService.updatePoDetail(poDetailRequest, key);
    }
    @PostMapping("/deleteByID")
    @ApiShow
    public DataResponse<String> deleteByID(@RequestParam("id") String id) {
        return poDetailService.deletePoDetail(id);
    }
    @GetMapping("/getByPo/{id}")
    @ApiShow
    public ListResponse<PoDetailResponse> getByPO(@PathVariable("id") String poNumber) {
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
    public DataResponse<String> updateImportDates(@RequestParam("poDetailIds") String poDetailIds) {
        return poDetailService.updateImageDates(poDetailIds);
    }
    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @PostMapping("/update/export-partner")
    public DataResponse<String> updateExportPartners(@RequestParam("poDetailIds") String poDetailIds) {
        return poDetailService.updateExportPartners(poDetailIds);
    }
    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @PostMapping("/search/serialNumber")
    public ListResponse<?> searchBySerialNumbers(@RequestParam("file") MultipartFile file) {
        return poDetailService.searchBySerialNumbers(file);
    }
    @ApiShow
    @Override
    public ListResponse<PoDetailResponse> getAll() {
        return super.getAll();
    }
    @ApiShow
    @Override
    public ListResponse<PoDetailResponse> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        return searchKeywordDto.getProperty().equals("ALL") ?
                poDetailService.getAllByListKeyword(searchKeywordDto) :
                super.searchByKeyword(searchKeywordDto);
    }
    @ApiShow
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    @GetMapping("/serialNumber")
    public ListResponse<PoDetailResponse> getBySerialNumber(String serialNumber) {
        return poDetailService.getBySerialNumber(serialNumber);
    }
}
