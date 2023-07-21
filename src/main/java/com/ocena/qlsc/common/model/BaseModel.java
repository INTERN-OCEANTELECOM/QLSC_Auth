package com.ocena.qlsc.common.model;

import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.user.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public <T extends BaseModel> List<String> compare(T other)  {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        List<String> diffProperties = new ArrayList<>();
        Class<? extends BaseModel> clazz = this.getClass();
        try {
            for (Field field: clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value1 = field.get(this);
                Object value2 = field.get(other);
                if (value1 == null && value2 == null) {
                    continue;
                }

                if(value2 != null && !value2.equals(value1)
                        && !(value2 instanceof Product)
                        && !(value2 instanceof Po)){
//                    if (field.getName().equals("password") && passwordEncoder.matches(value2.toString(),value1.toString())){
//                        System.out.println("zô");
//                    }
                    if (field.getName().equals("roles")){
                        List<Role> ListValue1  = (List<Role>) value1;
                        List<Role> ListValue2  = (List<Role>) value2;
                        if(!(ListValue1.stream().map(Role::getId)
                                .collect(Collectors.toList())
                                .equals(ListValue2.stream().map(Role::getId)
                                        .collect(Collectors.toList())))) {
                            diffProperties.add("Vai Trò");
                        }
                    } else {
                        diffProperties.add(getFieldNameVN(field.getName()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return diffProperties;
    }
}
