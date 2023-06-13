package com.ocena.qlsc.controller;


import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.model.User;
import com.ocena.qlsc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest,  BindingResult result) {
        return userService.validateUser(loginRequest, result);
    }

    // Function is used to create a new user
    // Using @Valid is used to check the validation of registerRequest
    @PostMapping("/create-user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest registerRequest,
                                                   BindingResult result) {
        return userService.validateRegister(registerRequest, result);
    }
}
