package com.ocena.qlsc.repository;

import com.ocena.qlsc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "select u.userName, u.password from User u join u.roles where u.userName=:userName")
    List<Object[]> existsByUsername(String userName);
}
