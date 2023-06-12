package com.ocena.qlsc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_role")
@IdClass(UserRole.UserRoleId.class)
public class UserRole {

    // Primary key of UserRole is roleId, userId
    @Id
    @Column(name = "role_id")
    private Integer roleId;

    @Id
    @Column(name = "user_id")
    private String userId;


    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * A composite primary key class for the UserRole entity.
     * Represents the composite primary key of the UserRole entity, which consists of
     two columns: roleId and userId.
     */
    public class UserRoleId implements Serializable {
        private Long roleId;
        private Long userId;
    }
}
