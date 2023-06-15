package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService {

    boolean registerUser(RegisterRequest registerRequest);

    User update(String id, User user);

    User getUserById(String userId);

    List<User> getAll();

    boolean delete(User user);

    /**
     * @see UserService#validateUser(LoginRequest, BindingResult)
     */
    ResponseEntity<ObjectResponse> validateUser(@Valid LoginRequest loginRequest, BindingResult result);

    /**
     * @see UserService#getAllUser()
     */
    ResponseEntity<ObjectResponse> getAllUser();
}
