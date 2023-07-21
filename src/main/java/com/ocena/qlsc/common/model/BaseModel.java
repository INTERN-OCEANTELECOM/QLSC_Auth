package com.ocena.qlsc.common.model;

import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.common.util.DateUtil;
import com.ocena.qlsc.common.util.ReflectionUtil;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.user_history.enums.Action;
import com.ocena.qlsc.user_history.model.SpecificationDesc;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    private void setLogsEditRole(Object value1, Object value2, List<String> diffProperties, List<String> oldDatas, List<String> newDatas) {
        List<Role> listValue1 = (ArrayList<Role>) value1;
        List<Role> listValue2 = (ArrayList<Role>) value2;
        if (!(listValue1.stream().map(Role::getId)
                .collect(Collectors.toList())
                .equals(listValue2.stream().map(Role::getId)
                        .collect(Collectors.toList())))) {
            diffProperties.add("Vai Trò");
            oldDatas.add(listValue1.get(0).getRoleName());
            newDatas.add(listValue2.get(0).getRoleName());
        }
    }

    public <T extends BaseModel> String compare(T other, Action action, SpecificationDesc specificationDesc) {
        String specification = "";
        List<String> diffProperties = new ArrayList<>();
        List<String> oldDatas = new ArrayList<>();
        List<String> newDatas = new ArrayList<>();
        Class<? extends BaseModel> clazz = this.getClass();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value1 = field.get(this);
                Object value2 = field.get(other);
                if (value2 == null) {
                    continue;
                }

                if (!value2.equals(value1) && !(value2 instanceof Product) && !(value2 instanceof Po)) {
                    if (field.getName().equals("roles")) {
                        setLogsEditRole(value1, value2, diffProperties, oldDatas, newDatas);
                        continue;
                    }
                    diffProperties.add(getVietNameseFieldName(field.getName()));
                    oldDatas.add(DateUtil.convertObjectToDateFormat(value1, field.getName()));
                    newDatas.add(DateUtil.convertObjectToDateFormat(value2, field.getName()));
                }

            }
        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }

        if (diffProperties.size() > 0) {
            if (action == Action.EDIT) {
                specificationDesc.setDesc(diffProperties, oldDatas, newDatas);
            } else if (action == Action.CREATE) {
                specificationDesc.setDesc(diffProperties, newDatas);
            }
        }
        return specificationDesc.getSpecification();
    }
}