package com.ocena.qlsc.common.controller;

import com.ocena.qlsc.common.dto.ChangeStatusDto;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.util.ApiResources;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

public interface BaseApi<E, D> {
    @PostMapping(ApiResources.ADD)
    DataResponse<E> add(@RequestBody D objectDTO);

    @PutMapping(ApiResources.UPDATE)
    public DataResponse<E> update(@Valid @RequestBody D objectDTO, @PathVariable("id") String id);

    @GetMapping(ApiResources.GET_BY_ID)
    public DataResponse<E> getById(@RequestParam String id);

    @PutMapping(ApiResources.DELETE)
    public DataResponse<D> delete(@PathVariable("id") String id);

    @GetMapping(ApiResources.GET_ALL)
    public ListResponse<D> getAll();

    @GetMapping(ApiResources.GET_BY_IDS)
    public ListResponse<D> getByIds(@RequestParam String ids);

    @GetMapping(ApiResources.GET_ALL_BY_KEYWORD)
    public ListResponse<E> getAllByKeyword(@RequestParam String keyword);

    @PostMapping(ApiResources.SEARCH_BY_KEYWORD)
    public ListResponse<E> searchByKeyword(@Valid @RequestBody SearchKeywordDto searchKeywordDto);

    @PutMapping(ApiResources.CHANGE_STATUS)
    public DataResponse<E> update(@Valid @RequestBody ChangeStatusDto changeStatusDto);
}
