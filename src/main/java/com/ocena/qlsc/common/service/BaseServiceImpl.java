package com.ocena.qlsc.common.service;


import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.constants.message.StatusCode;
import com.ocena.qlsc.common.constants.message.StatusMessage;
import com.ocena.qlsc.common.error.advice.DirectErrorHandler;
import com.ocena.qlsc.common.error.exception.DataNotFoundException;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.user_history.service.HistoryService;
import jakarta.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseServiceImpl<E extends BaseModel, Q, R> implements BaseService<E, Q, R> {
    protected abstract BaseRepository<E> getBaseRepository();

    protected abstract BaseMapper<E, Q, R> getBaseMapper();

    protected abstract Function<String, Optional<E>> getFindByFunction();

    protected abstract Class<E> getEntityClass();

    public Logger getLogger() {
        return Logger.getLogger(getEntityClass());
    }

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private HistoryService historyService;

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<R> create(Q dto) {
        E entity = getBaseMapper().dtoToEntity(dto);
        getBaseRepository().save(entity);
        try {
            getLogger().info("Create New Object");
            historyService.persistHistory(getEntityClass(), getEntityClass().getDeclaredConstructor().newInstance(), entity);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |InstantiationException e) {
            throw new DataNotFoundException(e.getMessage());
        }
        return ResponseMapper.toDataResponseSuccess("");
    }

    @Override
    public DataResponse<R> createMore(List<Q> dto) {
        List<String> listKey = getListKey(dto);

        List<E> entityList = IntStream.range(0, listKey.size())
                .mapToObj(index -> {
                            Optional<E> optional = getFindByFunction().apply(listKey.get(index));
                            if (optional.isPresent()) {
                                E entity = optional.get();
                                String id = entity.getId();
                                E oldEntity = null;

                                try {
                                    oldEntity = (E) entity.clone();
                                } catch (CloneNotSupportedException e) {
                                    throw new DataNotFoundException(e.getMessage());
                                }

                                getBaseMapper().dtoToEntity(dto.get(index), entity);
                                entity.setId(id);
                                getBaseRepository().save(entity);
                                getLogger().info("Update Key " + listKey.get(index));
                                historyService.updateHistory(getEntityClass(), listKey.get(index), oldEntity, getBaseMapper().dtoToEntity(dto.get(index)));
                                return null;
                            } else {
                                return getBaseMapper().dtoToEntity(dto.get(index));
                            }
                        }
                ).toList();

        if (!entityList.isEmpty()){
            getBaseRepository().saveAll(entityList.stream().filter(Objects::nonNull).toList());
            entityList.stream().filter(Objects::nonNull).forEach(entity -> {
                try {
                    historyService.persistHistory(getEntityClass(), getEntityClass().getDeclaredConstructor().newInstance(), entity);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                         InstantiationException e) {
                    throw new DataNotFoundException(e.getMessage());
                }
            });
        }
        return ResponseMapper.toDataResponseSuccess("");
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<R> update(String key, Q dto) {
        Optional<E> optional = getFindByFunction().apply(key);
        if (optional.isPresent()) {
            E entity = optional.get();
            String id = entity.getId();
            E oldEntity;

            try {
                oldEntity = (E) entity.clone();
            } catch (CloneNotSupportedException e) {
                throw new DataNotFoundException(e.getMessage());
            }
            System.out.println(dto);
            getBaseMapper().dtoToEntity(dto, entity);
            System.out.println(entity);
            entity.setId(id);
            getBaseRepository().save(entity);
            getLogger().info("Update Key " + key);
            historyService.updateHistory(getEntityClass(), key, oldEntity, getBaseMapper().dtoToEntity(dto));
            return ResponseMapper.toDataResponseSuccess("");
        }
        throw new DataNotFoundException(key + " doesn't exist");
    }
    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<R> delete(String id) {
        Optional<E> optional = getFindByFunction().apply(id);
        if (optional.isPresent()) {
            E entity = optional.get();
            entity.setRemoved(true);
            if (getBaseRepository().save(entity) != null) {
                getLogger().info("Delete User " + id);
                historyService.deleteHistory(getEntityClass(), id);
                return ResponseMapper.toDataResponseSuccess("");
            }
        }
        throw new DataNotFoundException(id + " doesn't exist");
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataResponse<R> getById(String id) {
        Optional<E> optional = getFindByFunction().apply(id);
        return optional.map(value -> ResponseMapper.toDataResponseSuccess(getBaseMapper().entityToDto(value)))
                .orElseGet(() -> ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<R> getByIds(String ids) {
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
    public ListResponse<R> getAll() {
        return ResponseMapper.toListResponseSuccess(
                getBaseRepository().findAll()
                        .stream().map(value -> getBaseMapper().entityToDto(value)).collect(Collectors.toList()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<E> getAllByKeyword(String keyword) {
        return ResponseMapper.toListResponseSuccess(getListSearchResults(keyword));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<R> searchByKeyword(SearchKeywordDto searchKeywordDto) {
        Pageable pageable = PageRequest.of(searchKeywordDto.getPageIndex(), searchKeywordDto.getPageSize());
        return ResponseMapper.toPagingResponseSuccess(getPageResults(searchKeywordDto, pageable));
    }

    /**
     * Validates a request object using a validator and returns a list of error messages if the object is invalid.
     * @param object the request object to be validated
     * @return a list of error messages if the object is invalid, or null otherwise
     */
    @Override
    public List<String> validationRequest(Object object) {
        // Create a BindingResult object to hold the result of the data validation process
        Errors result = new BeanPropertyBindingResult(object, "validationRequest");
        validator.validate(object, result);

        // If the object is invalid, return a list of error messages
        if((result.hasErrors())) {

            return result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
        }

        // If the object is valid, return null
        return null;
    }

    @Override
    public ListResponse<R> getAllByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<E> listResult = getBaseRepository().findAll(pageable);

        Page<R> listDTO = listResult.map(item
                -> getBaseMapper().entityToDto(item));

        return ResponseMapper.toPagingResponse(listDTO, StatusCode.REQUEST_SUCCESS, StatusMessage.REQUEST_SUCCESS);
    }

    protected abstract Page<R> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable);

    protected abstract List<E> getListSearchResults(String keyword);

    protected abstract List<String> getListKey(List<Q> objDTO);
}
