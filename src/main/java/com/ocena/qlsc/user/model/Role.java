package com.ocena.qlsc.user.model;

import com.ocena.qlsc.common.model.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role extends BaseModel {
    @Column(name = "role_name", length = 30, nullable = false)
    private String roleName;


    /** mapped to the "user_role" table in the database.
     * The "joinColumns" attribute specifies the foreign key column in the "user_role" table
     that references the "role_id" column in the "role" table.
     * The "inverseJoinColumns" attribute specifies the foreign key column in the "user_role" table
     that references the "user_id" column in the "user" table.
     */
    /*  0. new user
        1. updated user
        2. delete user  */
    @Column(nullable = false)
    private Short status;

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> users;
}
