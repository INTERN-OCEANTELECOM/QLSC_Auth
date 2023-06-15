package com.ocena.qlsc.controller;


import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<ObjectResponse> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        return userService.validateUser(loginRequest, result);
    }

    @GetMapping("/get-all-user")
    public ResponseEntity<ObjectResponse> getAllUser() {
        return userService.getAllUser();
    }

    // Function is used to create a new user
    // Using @Valid is used to check the validation of registerRequest
    @PostMapping("/create-user")
    public ResponseEntity<ObjectResponse> createUser(@Valid @RequestBody RegisterRequest registerRequest,
                                                     BindingResult result) {
        if((result.hasErrors())) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ObjectResponse("Create", "User is invalid", errorMessages)
            );
        } else {
            // User is valid
            // Check if user has been created successfully
            if(userService.registerUser(registerRequest)) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ObjectResponse("Create", "Create User successfully", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new ObjectResponse("Create", "User already exists in the database", "")
                );
            }
        }
    }
}
