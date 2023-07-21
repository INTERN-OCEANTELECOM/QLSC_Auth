package com.ocena.qlsc.common.model;

import com.ocena.qlsc.common.util.DateUtil;
import com.ocena.qlsc.common.util.ReflectionUtil;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.user_history.enums.Action;
import com.ocena.qlsc.user_history.model.SpecificationDesc;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public class BaseModel {
    @Id
    private String id;

    @Column
    private Long created;

    @Column
    private String creator;

    private Long updated;

    @Column
    private String modifier;

    @Column(name = "removed", columnDefinition = "boolean default true")
    private Boolean removed;

    @PrePersist
    private void ensureId() {
        this.setId(UUID.randomUUID().toString());
        this.setCreated(System.currentTimeMillis());
        this.setCreator(SystemUtil.getCurrentEmail());
        this.setRemoved(false);
    }

    @PreUpdate
    private void setUpdated() {
        this.setModifier(SystemUtil.getCurrentEmail());
        this.setUpdated(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", creator='" + creator + '\'' +
                ", updated=" + updated +
                ", modifier='" + modifier + '\'' +
                ", removed=" + removed +
                '}';
    }

    public String getVietNameseFieldName(String fieldName) {
        return ReflectionUtil.getFieldValueByReflection(fieldName,
                "com.ocena.qlsc.common.fields." + this.getClass().getSimpleName() + "Fields");
    }

    public <T extends BaseModel> String compare(T other, Action action, SpecificationDesc specificationDesc)  {
        String specification = "";
        List<String> diffProperties = new ArrayList<>();
        List<String> oldDatas = new ArrayList<>();
        List<String> newDatas = new ArrayList<>();
        Class<? extends BaseModel> clazz = this.getClass();
        try {
            for (Field field: clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value1 = field.get(this);
                Object value2 = field.get(other);
                if (value2 == null) {
                    continue;
                }
                if(value1 == null || !value1.equals(value2)) {
                    diffProperties.add(getVietNameseFieldName(field.getName()));
                    oldDatas.add(DateUtil.convertObjectToDateFormat(value1));
                    newDatas.add(DateUtil.convertObjectToDateFormat(value2));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if(diffProperties.size() > 0) {
            if(action == Action.EDIT) {
                specificationDesc.setDescription(diffProperties, oldDatas, newDatas);
            } else if(action == Action.CREATE) {
                specificationDesc.setDescription(diffProperties, newDatas);
            }
            specification = specificationDesc.getSpecification();
        }
        return specification;
    }
}
