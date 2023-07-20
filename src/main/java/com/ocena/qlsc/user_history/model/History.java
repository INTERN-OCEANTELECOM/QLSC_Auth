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
@Table(name = "user_history")
public class History {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "email")
    private User user;

    private Long created;

    private String object;

    private String action;

    @Lob
    @Column(columnDefinition = "TEXT", length = 100000)
    private String specification;

    @PrePersist
    private void createID(){
        this.setId(UUID.randomUUID().toString());
        this.setCreated(System.currentTimeMillis());
    }
}
