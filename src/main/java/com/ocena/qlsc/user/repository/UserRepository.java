package com.ocena.qlsc.user.repository;

import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseRepository<User> {
    @Query(value = "select u.email, u.password, u.status from User u join u.roles where u.email=:email")
    List<Object[]> existsByEmail(String email);
//
//    @Query(value = "select u.fullName, u.email, u.phoneNumber, u.password, u.status, r FROM User u JOIN u.roles r")
//    List<Object[]> getAllUser();
//
    @Query(value = "select r.id, r.roleName from Role r")
    List<Object[]> getAllRoles();
//
//    @Modifying(clearAutomatically = true)
//    @Transactional
//    @Query(value = "update User u set u.password = :password, u.status =:status, u.modifier = :modifier, " +
//            "u.updated = :updated where u.email = :emailUser")
//    int forgotPassword(@Param("password") String password, @Param("status") int status ,
//                       @Param("modifier") String modifier, @Param("updated") Long updated,
//                       @Param("emailUser") String emailUser);
//
//    @Query(value = "select u.email, u.password, u.fullName, u.phoneNumber from User u where u.email = :email")
//    List<Object[]> getUserByEmail(String email);
}
