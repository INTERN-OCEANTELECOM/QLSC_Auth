package com.ocena.qlsc.user_history.model;

import com.ocena.qlsc.common.util.SystemUtil;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_history")
public class History {

    @Id
    private String id;

    private Long created;

    @Column(length = 69)
    private String object;

    @Column(length = 69)
    private String action;

    private String email;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String specification;

    @PrePersist
    private void createID(){
        this.setId(UUID.randomUUID().toString());
        this.setCreated(System.currentTimeMillis());
        this.setEmail(SystemUtil.getCurrentEmail());
    }
}
