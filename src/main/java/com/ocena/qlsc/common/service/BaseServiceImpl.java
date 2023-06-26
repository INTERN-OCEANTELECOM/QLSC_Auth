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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<E extends BaseModel, D> implements BaseService<E, D> {
    protected abstract BaseRepository<E> getBaseRepository();

    protected abstract BaseMapper<E, D> getBaseMapper();

    @Autowired
    private LocalValidatorFactoryBean validator;

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
    public DataResponse<E> update(String key, D dto, Function<String, Optional<E>> findByFunction) {
        Optional<E> optional = findByFunction.apply(key);
        if (optional.isPresent()) {
            E entity = optional.get();
            getBaseMapper().dtoToEntity(dto, entity);;
            getBaseRepository().save(entity);
            return ResponseMapper.toDataResponseSuccess(entity);
        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<D> delete(String id) {
        Optional<E> optional = getBaseRepository().findById(id);
        if (optional.isPresent()) {
            E entity = optional.get();
            entity.setRemoved(true);
            getBaseRepository().save(entity);
            return ResponseMapper.toDataResponseSuccess(getBaseMapper().entityToDto(entity));
        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataResponse<E> getById(String id) {
        Optional<E> optional = getBaseRepository().findById(id);
        return optional.map(ResponseMapper::toDataResponseSuccess).orElseGet(() -> ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<D> getByIds(String ids) {
        String[] arr = ids.trim().split(",");
        if (arr.length > 0) {
            List<String> listIds = Arrays.asList(arr);
            return ResponseMapper.toListResponseSuccess(getBaseRepository().findAllById(listIds)
                    .stream().map(value -> getBaseMapper().entityToDto(value)).collect(Collectors.toList()));
        }
        return ResponseMapper.toListResponseSuccess(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<D> getAll() {
        return ResponseMapper.toListResponseSuccess(
                getBaseRepository().findAll()
                        .stream().map(value -> getBaseMapper().entityToDto(value)).collect(Collectors.toList()));
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

    @Override
    public List<String> validationRequest(Object object) {
        // The BindingResult object that holds the result of the data validation process.
        Errors result = new BeanPropertyBindingResult(object, "validationRequest");
        validator.validate(object, result);

        if((result.hasErrors())) {
            // User is invalid
            // Get Errors List
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return errorMessages;
        }
        return null;
    }

    protected abstract Page<E> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable);

    protected abstract List<E> getListSearchResults(String keyword);
}
