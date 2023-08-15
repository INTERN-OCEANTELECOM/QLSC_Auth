package com.ocena.qlsc.common.model;

import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.user_history.service.HistoryService;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseModel implements Cloneable {
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @PrePersist
    public void ensureId() {
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
}