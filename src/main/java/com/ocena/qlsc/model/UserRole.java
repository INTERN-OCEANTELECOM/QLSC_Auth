package com.ocena.qlsc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long created;

    @Column(length = 30, nullable = false)
    private String creator;

    private Long updated;

    @Column(length = 30)
    private String modifier;

    /*  0. Trang thai moi
        1. Trang thai cap nhat
        2. Trang thai xoa  */

    @Column(nullable = false)
    private Short status;

    @Column(name = "removed", nullable = false)
    private boolean removed = false;

    public void delete() {
        this.removed = true;
    }

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
