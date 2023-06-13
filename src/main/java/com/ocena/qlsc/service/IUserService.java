package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService {

    boolean registerUser(RegisterRequest registerRequest);


    ResponseEntity<UserResponse> validateRegister(RegisterRequest registerRequest, BindingResult result);

    ResponseEntity<UserResponse> validateUser(@Valid LoginRequest loginRequest, BindingResult result);
}
