package com.ocena.qlsc.repository;

import com.ocena.qlsc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = "select u.email, u.password, u.status from User u  join u.roles where u.email=:email")
    List<Object[]> existsByEmail(String email);

    @Query(value = "select u.fullName, u.email, u.phoneNumber, u.userName, u.password, u.status, r FROM User u JOIN u.roles r")
    List<Object[]> getAllUser();
    
    @Query(value = "select r.roleId, r.roleName from Role r")
    List<Object[]> getAllRoles();
}
