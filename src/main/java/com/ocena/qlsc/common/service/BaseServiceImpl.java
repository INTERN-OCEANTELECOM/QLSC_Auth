package com.ocena.qlsc.common.service;


import com.ocena.qlsc.common.dto.ChangeStatusDto;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseServiceImpl<E extends BaseModel, D> implements BaseService<E, D> {
    protected abstract BaseRepository<E> getBaseRepository();

    protected abstract BaseMapper<E, D> getBaseMapper();

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<E> create(D dto) {
        E entity = getBaseMapper().dtoToEntity(dto);
        getBaseRepository().save(entity);
        return ResponseMapper.toDataResponseSuccess(entity);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<E> update(String id, D dto) {
        Optional<E> optional = getBaseRepository().findById(id);
        if (optional.isPresent()) {
            E entity = optional.get();
            getBaseMapper().dtoToEntity(dto, entity);
            entity.setId(id);
            getBaseRepository().save(entity);
            return ResponseMapper.toDataResponseSuccess(entity);
        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<E> delete(E entity) {
        entity.setRemoved(true);
        getBaseRepository().save(entity);
        return ResponseMapper.toDataResponseSuccess(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataResponse<E> getById(String id) {
        Optional<E> optional = getBaseRepository().findById(id);
        return optional.map(ResponseMapper::toDataResponseSuccess).orElseGet(() -> ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<E> getByIds(String ids) {
        String[] arr = ids.trim().split(",");
        if (arr.length > 0) {
            List<String> listIds = Arrays.asList(arr);
            return ResponseMapper.toListResponseSuccess(getBaseRepository().findAllById(listIds));
        }
        return ResponseMapper.toListResponseSuccess(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<E> getAll() {
        return ResponseMapper.toListResponseSuccess(getBaseRepository().findAll());
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataResponse<E> changeStatus(ChangeStatusDto changeStatusDto) {
        Optional<E> optional = getBaseRepository().findById(changeStatusDto.getId());
        if (optional.isPresent()) {
            E entity = optional.get();
//            entity.setStatus(changeStatusDto.getStatus());
            getBaseRepository().save(entity);
            return ResponseMapper.toDataResponseSuccess(entity);
        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }


    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<E> getAllByKeyword(String keyword) {
        return ResponseMapper.toListResponseSuccess(getListSearchResults(keyword));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<E> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        Pageable pageable = PageRequest.of(searchKeywordDto.getPageIndex(), searchKeywordDto.getPageSize());
        return ResponseMapper.toPagingResponseSuccess(getPageResults(searchKeywordDto, pageable));
    }

    protected abstract Page<E> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable);

    protected abstract List<E> getListSearchResults(String keyword);
}
