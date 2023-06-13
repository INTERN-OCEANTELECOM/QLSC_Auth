package com.ocena.qlsc.controller;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.model.User;
import com.ocena.qlsc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest,  BindingResult result){
        return userService.validateUser(loginRequest, result);
    }
}
