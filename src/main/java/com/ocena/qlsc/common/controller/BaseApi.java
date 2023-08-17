package com.ocena.qlsc.common.controller;

import com.ocena.qlsc.common.dto.ChangeStatusDto;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.util.ApiResources;
import com.ocena.qlsc.common.validate.ValidList;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface BaseApi<E, Q, R> {


    @PostMapping(ApiResources.ADD)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    DataResponse<R> add(@RequestBody Q objectDTO);

    @PostMapping(ApiResources.ADD_ALL)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    DataResponse<R> addAll(@RequestBody ValidList<Q> objectDTO);

    @PutMapping(ApiResources.UPDATE)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    DataResponse<R> update(@RequestBody Q objectDTO,
                           @PathVariable("key") String key);

    @GetMapping(ApiResources.GET_BY_ID)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    public DataResponse<R> getById(@RequestParam String id);

    @PutMapping(ApiResources.DELETE)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    public DataResponse<R> delete(@PathVariable("id") String id);

    @GetMapping(ApiResources.GET_ALL)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    public ListResponse<R> getAll();

    @Hidden
    @GetMapping(ApiResources.GET_BY_IDS)
    public ListResponse<R> getByIds(@RequestParam String ids);

    @Hidden
    @GetMapping(ApiResources.GET_ALL_BY_KEYWORD)
    public ListResponse<E> getAllByKeyword(@RequestParam String keyword);

    @PostMapping(ApiResources.SEARCH_BY_KEYWORD)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    public ListResponse<R> searchByKeyword(@Valid @RequestBody SearchKeywordDto searchKeywordDto);

    @GetMapping(ApiResources.GET_ALL_BY_PAGE)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    public ListResponse<R> getAllByPage(@RequestParam("page") int page, @RequestParam("size") int size);
}
