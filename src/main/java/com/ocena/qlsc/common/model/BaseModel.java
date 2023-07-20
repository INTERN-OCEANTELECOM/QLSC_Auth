package com.ocena.qlsc.common.model;

import com.ocena.qlsc.common.fields.ProductFields;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.user_history.enums.Action;
import com.ocena.qlsc.user_history.model.SpecificationDesc;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
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

    public String getFieldNameVN(String fieldName) {
        try {
            Class<?> clazz = Class.forName("com.ocena.qlsc.common.fields." + this.getClass().getSimpleName() + "Fields");
            Object getClass = clazz.getDeclaredConstructor().newInstance();
            Field field = clazz.getDeclaredField(fieldName);
            return (String) field.get(getClass);
        }  catch (NoSuchFieldException | InvocationTargetException | InstantiationException | IllegalAccessException |
                NoSuchMethodException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }
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
                if (value1 == null && value2 == null) {
                    continue;
                }
                if(!value1.equals(value2)) {
                    diffProperties.add(getFieldNameVN(field.getName()));
                    oldDatas.add(value1.toString());
                    newDatas.add(value2.toString());
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
            } else if(action == Action.UPDATE) {
                specificationDesc.setDescription(diffProperties, oldDatas, newDatas, "");
            }
            specification = specificationDesc.getSpecification();
        }
        return specification;
    }
}
