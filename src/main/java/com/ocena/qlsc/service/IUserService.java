package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService {

 
    ResponseEntity<ObjectResponse> validateRegister(RegisterRequest registerRequest, BindingResult result);

    boolean createUser(RegisterRequest registerRequest);

    /**
     * @see UserService#validateUser(LoginRequest, BindingResult, HttpServletRequest)
     */
    ResponseEntity<ObjectResponse> validateUser(LoginRequest loginRequest, BindingResult result, HttpServletRequest request);

    /**
     * @see UserService#getAllUser()
     */
    ResponseEntity<ObjectResponse> getAllUser();

    ResponseEntity<List<RoleResponse>> getAllRoles();

    ResponseEntity<ObjectResponse> sentOTP(String email, HttpServletRequest request);

    ResponseEntity<ObjectResponse> validateOTP(String email, Integer OTP);
}
