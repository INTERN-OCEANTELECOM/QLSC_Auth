package com.ocena.qlsc.user_history.service;

import com.ocena.qlsc.common.constants.FieldsNameConstants;
import com.ocena.qlsc.common.error.exception.DataNotFoundException;
import com.ocena.qlsc.common.util.ReflectionUtil;
import com.ocena.qlsc.common.util.StringUtil;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user_history.dto.HistoryDto;
import com.ocena.qlsc.user_history.enumrate.ObjectName;
import com.ocena.qlsc.user_history.mapper.HistoryMapper;
import com.ocena.qlsc.user_history.enumrate.Action;
import com.ocena.qlsc.user_history.model.ComparisonResults;
import com.ocena.qlsc.user_history.model.History;
import com.ocena.qlsc.user_history.model.HistoryDescription;
import com.ocena.qlsc.user_history.repository.HistoryRepository;
import com.ocena.qlsc.user_history.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {
    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    HistoryMapper historyMapper;

    public ListResponse<HistoryDto> getAll(){
        List<HistoryDto> historyDTOList = historyRepository.findAll().stream()
                .map(history -> historyMapper.convertTo(history, HistoryDto.class)).collect(Collectors.toList());

        return ResponseMapper.toListResponseSuccess(historyDTOList);
    }

    public void save(String action, String object, String description, String email, String filePath) {
        if(action.equals(Action.DELETE.getValue()) ||
                action.equals(Action.RESET_PASSWORD.getValue()) ||
                !description.equals("") ) {
            History history = new History();
            history.setAction(action);
            history.setObject(object);
            history.setDescription(description);
            if(email.equals(""))
                history.setEmail(SystemUtil.getCurrentEmail());
            else {
                history.setEmail(email);
            }
            if(filePath != null) {
                history.setFilePath(filePath);
            }
            historyRepository.save(history);
        }
    }

    public ListResponse<HistoryDto> getByCreatedBetween(Long start, Long end){
        List<History> historyList = historyRepository.getAllByCreatedBetween(start, end);

        List<HistoryDto> historyDTOList = historyList.stream()
                .map(history -> historyMapper.convertTo(history, HistoryDto.class)).collect(Collectors.toList());

        return ResponseMapper.toListResponseSuccess(historyDTOList);
    }

    public ResponseEntity<byte[]> downloadFile(String filePath) {
        byte[] bytes = FileUtil.getBytesDataFromFilePath(filePath);
        if(bytes == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND".getBytes());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filePath);
        return ResponseEntity.ok().header(String.valueOf(headers)).body(bytes);
    }



    private void compareRoles(Object oldFieldValue,
                              Object newFieldValue,
                              List<String> diffProperties,
                              List<String> previousObjectAttributeValues,
                              List<String> newObjectAttributeValues) {
        List<Role> listOldRoles = (List<Role>) oldFieldValue;
        List<Role> listNewRoles = (List<Role>) newFieldValue;

        // Compare on Roles Fields
        if(listOldRoles == null && listNewRoles != null) {
            diffProperties.add("Quyền");
            previousObjectAttributeValues.add("null");
            newObjectAttributeValues.add(listNewRoles.get(0).getRoleName());
        }
        else if (!listOldRoles.get(0).getId().equals(listNewRoles.get(0).getId())) {
            diffProperties.add("Quyền");
            previousObjectAttributeValues.add(listOldRoles.get(0).getRoleName());
            newObjectAttributeValues.add(listNewRoles.get(0).getRoleName());
        }
    }

    public ComparisonResults compareObjects(Object oldObject, Object newObject) {
        Class<?> clazz = oldObject.getClass();
        List<String> fieldNames = new ArrayList<>();
        List<String> oldValues = new ArrayList<>();
        List<String> newValues = new ArrayList<>();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object oldFieldValue = field.get(oldObject);
                Object newFieldValue = field.get(newObject);
                if (oldFieldValue == null && newFieldValue == null)
                    continue;

                if (newFieldValue == null
                        && !field.getType().equals(Short.class)
                        && !field.getType().equals(Long.class)) {
                    continue;
                }

                if (ReflectionUtil.isComplexType(field.getType()) || FieldsNameConstants.FIELD_TO_EXCLUDE.contains(field.getName()))
                    continue;

                if ((newFieldValue == null && oldFieldValue != null) || !newFieldValue.equals(oldFieldValue)) {
                    if (field.getType().equals(Role.class)) {
                        compareRoles(oldFieldValue, newFieldValue, fieldNames, oldValues, newValues);
                    } else {
                        fieldNames.add(ReflectionUtil.getVietNameseFieldName(field.getName(), clazz.getSimpleName().toUpperCase()));
                        oldValues.add(StringUtil.convertValueToFormattedString(oldFieldValue, field.getName()));
                        newValues.add(StringUtil.convertValueToFormattedString(newFieldValue, field.getName()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new DataNotFoundException(e.getMessage());
        }
        return new ComparisonResults(fieldNames, oldValues, newValues);
    }

    public void persistHistory(Class<?> clazz, Object oldObject, Object newObject) {
        ComparisonResults comparisonResults = compareObjects(oldObject, newObject);
        HistoryDescription historyDescription = new HistoryDescription();
        String details = historyDescription.getDetailsDescription(comparisonResults.getFieldNames(), null, comparisonResults.getNewValues());
        historyDescription.setDetails(details);
        String objectName = (String) ReflectionUtil.getFieldValueByReflection(clazz.getSimpleName().toString(),
                new ObjectName());
        save(Action.CREATE.getValue(), objectName, historyDescription.getDescription(), "", null);
    }

    public void updateHistory(Class<?> clazz, String key, Object oldObject, Object newObject) {
        ComparisonResults comparisonResults = compareObjects(oldObject, newObject);
        HistoryDescription historyDescription = new HistoryDescription();
        String details = historyDescription.getDetailsDescription(comparisonResults.getFieldNames(), comparisonResults.getOldValues(), comparisonResults.getNewValues());
        historyDescription.setKey(key);
        historyDescription.setDetails(details);
        String objectName = (String) ReflectionUtil.getFieldValueByReflection(clazz.getSimpleName().toString(),
                new ObjectName());
        save(Action.EDIT.getValue(), objectName, historyDescription.getDescription(), "", null);
    }

    public void deleteHistory(Class<?> clazz, String key) {
        HistoryDescription historyDescription = new HistoryDescription();
        historyDescription.setKey(key);
        String objectName = (String) ReflectionUtil.getFieldValueByReflection(clazz.getSimpleName().toString(),
                new ObjectName());
        save(Action.DELETE.getValue(), objectName, historyDescription.getDescription(), "", null);
    }

    public void importExcelHistory(String action, List<PoDetail> poDetailList, MultipartFile file) {
        List<String> distinctPoNumber = poDetailList.stream()
                .map(poDetail -> poDetail.getPo().getPoNumber())
                .distinct()
                .collect(Collectors.toList());

        HistoryDescription description = new HistoryDescription();
        description.setKey(String.join(", ", distinctPoNumber));
        description.setImportAmount(((Integer) poDetailList.size()).toString());

        String descriptionHistory = poDetailList.stream()
                .map(poDetail -> "<" + poDetail.getSerialNumber().toString() + "> ")
                .collect(Collectors.joining());
        description.setDetails(description.getDetailsDescription(descriptionHistory));

        String filePath = FileUtil.saveUploadedFile(file, action);
        save(action, ObjectName.PoDetail, description.getDescription(), "", filePath);
    }

    public void loginHistory(String key) {
        save(Action.LOGIN.getValue(), null, "Đăng Nhập Thành Công", key, null);
    }

    public void lockHistory(String key) {
        save(Action.LOGIN.getValue(), null, "Tài Khoản bị khóa tạm thời", key, null);
    }

    public void resetPassword(String key) {
        save(Action.RESET_PASSWORD.getValue(), ObjectName.User, "", key, null);
    }
}
