package com.ocena.qlsc.controller;


import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ObjectResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                                BindingResult result) {
        return userService.validateUser(loginRequest, result);
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
