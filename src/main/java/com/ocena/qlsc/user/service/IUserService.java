package com.ocena.qlsc.user.service;

import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.*;
import com.ocena.qlsc.user.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService extends BaseService<User, UserDto> {

//    boolean createUser(RegisterRequest registerRequest);

    DataResponse<UserDto> create(UserDto dto);


//    ListResponse<UserDTO> getAllUser();

    DataResponse<User> login(LoginRequest loginRequest, HttpServletRequest request);

    DataResponse<User> sentOTP(String email, HttpServletRequest request);

    DataResponse<User> validateOTP(String email, Integer OTP, String newPassword);


    boolean hasDeleteUserPermission(String emailUser, String emailModifier);

    DataResponse<User> resetPassword(String email, String oldPassword, String newPassword);
}
