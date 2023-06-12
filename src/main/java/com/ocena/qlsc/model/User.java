package com.ocena.qlsc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name", length = 30, nullable = false)
    private String userName;

    @Column(length = 30, nullable = false)
    private String password;

    @Column(length = 140, nullable = false)
    private String email;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id")},
            inverseJoinColumns = { @JoinColumn(name = "role_id")})
    private List<Role> roles;

}
