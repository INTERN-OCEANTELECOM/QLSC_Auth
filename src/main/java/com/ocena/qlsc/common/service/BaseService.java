package com.ocena.qlsc.common.service;


import com.ocena.qlsc.common.dto.ChangeStatusDto;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface BaseService<E, D> {
    DataResponse<E> create(D dto);

    @Transactional
    @SuppressWarnings("unchecked")
    DataResponse<E> update(String key, D dto, Function<String, Optional<E>> findByFunction);

    DataResponse<D> delete(String id);

    DataResponse<E> getById(String id);

    ListResponse<D> getByIds(String ids);

    ListResponse<D> getAll();

    DataResponse<E> changeStatus(ChangeStatusDto changeStatusDto);

    ListResponse<E> getAllByKeyword(String keyword);

    ListResponse<E> searchByKeyword(SearchKeywordDto searchKeywordDto);

    List<String> validationRequest (Object object);

    ListResponse<D> getAllByPage(int page, int size);
}
