package com.ocena.qlsc.model;

import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role {
    @Id
    // Auto Increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(length = 30, nullable = false)
    private String roleName;

    @Column(nullable = false)
    private Long created;

    @Column(length = 30, nullable = false)
    private String creator;

    private Long updated;

    @Column(length = 30)
    private String modifier;
    /*
        0. Trang thai moi
        1. Trang thai cap nhat
        2. Trang thai xoa
     */

    @Column(nullable = false)
    private Short status;

    @Column(name = "removed", nullable = false)
    private boolean removed = false;

    public void delete() {
        this.removed = true;
    }

    @ManyToMany(mappedBy = "roles")
    private List<User> users;
}
