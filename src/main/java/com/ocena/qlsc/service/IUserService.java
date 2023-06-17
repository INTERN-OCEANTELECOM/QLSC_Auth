package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService {
    ResponseEntity<ObjectResponse> validateRegister(RegisterRequest registerRequest, BindingResult result);

    boolean createUser(RegisterRequest registerRequest);

    /**
     * @see UserService#getAllUser()
     */
    ResponseEntity<ObjectResponse> getAllUser();

    ResponseEntity<ObjectResponse> validateLogin(String email, String password,
                                                 HttpServletRequest request, BindingResult result);

    /**
     * @see UserService#sentOTP(String, HttpServletRequest) 
     */
    ResponseEntity<ObjectResponse> sentOTP(String email, HttpServletRequest request);

    /**
     * @see UserService#validateOTP(String, Integer, String, String)
     */
    ResponseEntity<ObjectResponse> validateOTP(String email, Integer OTP, String newPassword, String rePassword);

    ResponseEntity<List<RoleResponse>> getAllRoles();

}
