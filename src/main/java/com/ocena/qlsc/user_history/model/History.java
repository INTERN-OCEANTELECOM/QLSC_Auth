package com.ocena.qlsc.user_history.model;

import com.ocena.qlsc.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "user_history")
public class History {

    @Id
    private String id;

    private String email;

    private Long created;

    private String object;

    private String action;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String specification;

    @PrePersist
    private void createID(){
        this.setId(UUID.randomUUID().toString());
        this.setCreated(System.currentTimeMillis());
    }
}
