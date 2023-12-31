package com.ocena.qlsc.user_history.model;

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

    @Column(length = 69)
    private String object;

    @Column(length = 69)
    private String action;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "file_path", length = 400)
    private String filePath;

    @PrePersist
    private void createID(){
        this.setId(UUID.randomUUID().toString());
        this.setCreated(System.currentTimeMillis());
    }
}
