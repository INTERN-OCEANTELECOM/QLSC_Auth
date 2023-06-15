package com.ocena.qlsc.repository;

import com.ocena.qlsc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = "select u.userName, u.password, u.status from User u  join u.roles where u.userName=:userName")
    List<Object[]> existsByUsername(String userName);

    @Query(value = "select u.fullName, u.email, u.phoneNumber, u.userName, u.password, u.status, r FROM User u JOIN u.roles r")
    List<Object[]> getAllUser();
    
    @Query(value = "select r.roleId, r.roleName from Role r")
    List<Object[]> getAllRoles();
}
