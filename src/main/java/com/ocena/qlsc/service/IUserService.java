package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService {

 
    ResponseEntity<ObjectResponse> validateRegister(RegisterRequest registerRequest, BindingResult result);

    boolean createUser(RegisterRequest registerRequest);

    /**
     * @see UserService#validateUser(LoginRequest, BindingResult)
     */
    ResponseEntity<ObjectResponse> validateUser(@Valid LoginRequest loginRequest, BindingResult result);

    /**
     * @see UserService#getAllUser()
     */
    ResponseEntity<ObjectResponse> getAllUser();

    ResponseEntity<List<RoleResponse>> getAllRoles();

    ResponseEntity<ObjectResponse> sentOTP(String email);

    ResponseEntity<ObjectResponse> validateOTP(String email, Integer OTP);
}
