package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService {
    User registerUser(User user);

    User update(String id, User user);

    User getUserById(String userId);

    List<User> getAll();

    boolean delete(User user);

    ResponseEntity<UserResponse> validateUser(@Valid LoginRequest loginRequest, BindingResult result);
}
