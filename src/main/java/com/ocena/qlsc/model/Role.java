package com.ocena.qlsc.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

    @Column(name = "role_name", length = 30, nullable = false)
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

    // Set removed attribute is true
    public void delete() {
        this.removed = true;
    }

    /** mapped to the "user_role" table in the database.
     * The "joinColumns" attribute specifies the foreign key column in the "user_role" table
     that references the "role_id" column in the "role" table.
     * The "inverseJoinColumns" attribute specifies the foreign key column in the "user_role" table
     that references the "user_id" column in the "user" table.
     */
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    private List<User> users;
}
