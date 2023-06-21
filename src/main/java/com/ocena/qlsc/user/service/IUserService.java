package com.ocena.qlsc.user.service;

import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.*;
import com.ocena.qlsc.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUserService extends BaseService<User, UserDTO> {

//    boolean createUser(RegisterRequest registerRequest);

    DataResponse<User> create(UserDTO dto);

    DataResponse<User> validateRegister(UserDTO dto);

    ListResponse<UserDTO> getAllUser();

    DataResponse<User> validateLogin(LoginRequest loginRequest, HttpServletRequest request);

    DataResponse<User> sentOTP(String email, HttpServletRequest request);

    DataResponse<User> validateOTP(String email, Integer OTP, String newPassword, String rePassword);

    DataResponse<User> deleteUser(String emailUser, String emailModifier);

    DataResponse<User> getUserByEmail(String email);

    DataResponse<User> updateUser(String emailUser, UserDTO userDTO);
}
