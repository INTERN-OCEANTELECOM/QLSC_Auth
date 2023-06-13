package com.ocena.qlsc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    // Random from UUID
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name", length = 30, nullable = false)
    private String userName;

    @Column(length = 250, nullable = false)
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

    @Column(name = "removed", columnDefinition = "boolean default true")
    private boolean removed = true;

    // Set removed attribute is true
    public void delete() {
        this.removed = false;
    }

    /**
     * A many-to-many relationship between the User and Role entities.
     relationship, which is mapped to the "user_role" table in the database.
     * The "joinColumns" attribute references the "user_id" column in the "user" table.
     * The "inverseJoinColumns" attribute references the "role_id" column in the "role" table.
     */
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private List<Role> roles;
}
