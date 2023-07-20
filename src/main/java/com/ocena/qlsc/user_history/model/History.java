package com.ocena.qlsc.user_history.model;

import com.ocena.qlsc.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_history")
public class History {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "email")
    private User user;

    private Long created;

    private String object;

    private String action;

    @Lob
    @Column(columnDefinition = "TEXT", length = 100000)
    private String specification;
}
