package com.ocena.qlsc.common.service;


import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.constants.message.StatusCode;
import com.ocena.qlsc.common.constants.message.StatusMessage;
import com.ocena.qlsc.common.error.advice.DirectErrorHandler;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.model.BaseModel;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.util.ReflectionUtil;
import com.ocena.qlsc.user_history.enums.Action;
import com.ocena.qlsc.user_history.enums.ObjectName;
import com.ocena.qlsc.user_history.model.HistoryDescription;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<E extends BaseModel, D> implements BaseService<E, D> {
    protected abstract BaseRepository<E> getBaseRepository();

    protected abstract BaseMapper<E, D> getBaseMapper();

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
    public DataResponse<D> create(D dto) {
        E entity = getBaseMapper().dtoToEntity(dto);
        getBaseRepository().save(entity);
        try {
            getLogger().info("Create New Object");
            saveHistory(Action.CREATE, "", entity, getEntityClass().getDeclaredConstructor().newInstance());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |InstantiationException e) {
            getLogger().error(e);
            throw new RuntimeException(e);
        }
        return ResponseMapper.toDataResponseSuccess("");
    }

    private void saveHistory(Action action, String key, E newEntity, E oldEntity) {
        HistoryDescription historyDescription = new HistoryDescription();
        if(action == Action.DELETE) {
            historyDescription.setKey(key);
        } else {
            // Compare the values of the attributes in the old entity and the new entity
            String descriptionDetails = oldEntity.compare(newEntity, action, historyDescription);
            // Set Description
            if(!descriptionDetails.equals("")) {
                if(action == Action.CREATE) {
                    historyDescription.setDetails(descriptionDetails);
                } else {
                    // Action is Edit
                    historyDescription.setKey(key);
                    historyDescription.setDetails(descriptionDetails);
                }
            }
        }
        // Get Object New
        String objectName = (String) ReflectionUtil.getFieldValueByReflection(newEntity.getClass().getSimpleName().toString(),
                new ObjectName());
        historyService.save(action.getValue(), objectName, historyDescription.getDescription(), "", null);
    }


    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<D> update(String key, D dto) {
        Optional<E> optional = getFindByFunction().apply(key);
        if (optional.isPresent()) {
            E entity = optional.get();
            String id = entity.getId();
            E oldEntity = null;

            try {
                oldEntity = (E) entity.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            getBaseMapper().dtoToEntity(dto, entity);
            entity.setId(id);
            getBaseRepository().save(entity);
            saveHistory(Action.EDIT, key, getBaseMapper().dtoToEntity(dto), oldEntity);
            getLogger().info("Update Key " + key);

            return ResponseMapper.toDataResponseSuccess("");
        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }
    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public DataResponse<D> delete(String id) {
        Optional<E> optional = getFindByFunction().apply(id);
        if (optional.isPresent()) {
            E entity = optional.get();
            entity.setRemoved(true);
            if (getBaseRepository().save(entity) != null) {
                getLogger().info("Delete User " + id);
                saveHistory(Action.DELETE, id, entity, null);
                return ResponseMapper.toDataResponseSuccess("");
            }
        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataResponse<D> getById(String id) {
        Optional<E> optional = getFindByFunction().apply(id);
        return optional.map(value -> ResponseMapper.toDataResponseSuccess(getBaseMapper().entityToDto(value)))
                .orElseGet(() -> ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND));
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
    public ListResponse<E> getAllByKeyword(String keyword) {
        return ResponseMapper.toListResponseSuccess(getListSearchResults(keyword));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListResponse<D> searchByKeyword(SearchKeywordDto searchKeywordDto) {
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
    public ListResponse<D> getAllByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<E> listResult = getBaseRepository().findAll(pageable);

        Page<D> listDTO = listResult.map(item
                -> getBaseMapper().entityToDto(item));

        return ResponseMapper.toPagingResponse(listDTO, StatusCode.REQUEST_SUCCESS, StatusMessage.REQUEST_SUCCESS);
    }

    protected abstract Page<D> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable);

    protected abstract List<E> getListSearchResults(String keyword);
}
