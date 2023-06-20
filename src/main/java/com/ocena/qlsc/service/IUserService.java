package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.*;
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

    ResponseEntity<ObjectResponse> deleteUser(String emailUser, String emailModifier);

    ResponseEntity<ObjectResponse> updateUser(String emailUser, String emailModifier, String fullName, String phoneNumber, String email);

}
