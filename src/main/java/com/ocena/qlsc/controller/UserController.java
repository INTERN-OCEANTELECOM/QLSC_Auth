package com.ocena.qlsc.controller;


import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.service.IUserService;
import com.ocena.qlsc.service.UserService;
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
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ObjectResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                                BindingResult result,
                                                HttpServletRequest request) {
        return userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword(), request, result);
    }

    @GetMapping("/get-all-user")
    public ResponseEntity<ObjectResponse> getAllUser() {
        return userService.getAllUser();
    }

    @PostMapping("/create-user")
    public ResponseEntity<ObjectResponse> createUser(@Valid @RequestBody RegisterRequest registerRequest,
                                                     BindingResult result) {
        return userService.validateRegister(registerRequest, result);
    }

    @GetMapping("/get-roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return userService.getAllRoles();
    }

    @PostMapping("/forgot-password/sent-otp")
    public ResponseEntity<ObjectResponse> SentOTP(@RequestParam String email, HttpServletRequest request) {
        return userService.sentOTP(email, request);
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<ObjectResponse> forgotPassword(@RequestParam String email, @RequestParam Integer OTP, @RequestParam String newPassword, String rePassword) {
        return userService.validateOTP(email, OTP);
    }
}
