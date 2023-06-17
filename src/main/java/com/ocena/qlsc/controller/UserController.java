package com.ocena.qlsc.controller;


import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import com.ocena.qlsc.dto.RoleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<ObjectResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                                BindingResult result,
                                                HttpServletRequest request) {
        return userService.validateUser(loginRequest, result, request);
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
        return userService.validateRegister(registerRequest, result);
    }

    @GetMapping("/get-roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return userService.getAllRoles();
    }
}
