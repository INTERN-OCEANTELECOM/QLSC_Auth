package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService {

    /**
     * test
     * @param registerRequest : ádas
     * @param result : ádad
     * @return :a ád
     */
    ResponseEntity<UserResponse> validateRegister(RegisterRequest registerRequest, BindingResult result);

    boolean createUser(RegisterRequest registerRequest);

    ResponseEntity<UserResponse> validateUser(@Valid LoginRequest loginRequest, BindingResult result);

    ResponseEntity<List<RoleResponse>> getAllRoles();
}
