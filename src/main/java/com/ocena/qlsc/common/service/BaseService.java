package com.ocena.qlsc.common.service;


import com.ocena.qlsc.common.dto.ChangeStatusDto;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import jakarta.transaction.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface BaseService<E, Q, R> {
    DataResponse<R> create(Q dto);

    DataResponse<R> createMore(List<Q> dto);

    @Transactional
    @SuppressWarnings("unchecked")
    DataResponse<R> update(String key, Q dto);
    @Transactional
    @SuppressWarnings("unchecked")
    DataResponse<R> delete(String id);

    @SuppressWarnings("unchecked")
    DataResponse<R> getById(String id);

    @SuppressWarnings("unchecked")
    ListResponse<R> getByIds(String ids);

    ListResponse<R> getAll();

    ListResponse<E> getAllByKeyword(String keyword);

    ListResponse<R> searchByKeyword(SearchKeywordDto searchKeywordDto);

    List<String> validationRequest (Object object);

    ListResponse<R> getAllByPage(int page, int size);
}
