package com.ocena.qlsc.common.controller;

import com.ocena.qlsc.common.dto.ChangeStatusDto;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.util.ApiResources;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Function;

public interface BaseApi<E, D> {


    @PostMapping(ApiResources.ADD)
    DataResponse<D> add(@RequestBody D objectDTO);

    @PutMapping(ApiResources.UPDATE)
    DataResponse<D> update(@RequestBody D objectDTO,
                           @PathVariable("key") String key);

    @GetMapping(ApiResources.GET_BY_ID)
    public DataResponse<D> getById(@RequestParam String id);

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

    @GetMapping(ApiResources.GET_ALL_BY_PAGE)
    public ListResponse<D> getAllByPage(@RequestParam("page") int page, @RequestParam("size") int size);
}
