package com.ocena.qlsc.user.service;

import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.ObjectResponse;
import com.ocena.qlsc.user.dto.RegisterRequest;
import com.ocena.qlsc.user.dto.RoleDTO;
import com.ocena.qlsc.user.dto.UserDTO;
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

//    /**
//     * @see UserService#getAllUser()
//     */
//    ResponseEntity<ObjectResponse> getAllUser();
//
//    ResponseEntity<ObjectResponse> validateLogin(String email, String password,
//                                                 HttpServletRequest request, BindingResult result);
//
//    /**
//     * @see UserService#sentOTP(String, HttpServletRequest)
//     */
//    ResponseEntity<ObjectResponse> sentOTP(String email, HttpServletRequest request);
//
//    /**
//     * @see UserService#validateOTP(String, Integer, String, String)
//     */
//    ResponseEntity<ObjectResponse> validateOTP(String email, Integer OTP, String newPassword, String rePassword);
//
//    ResponseEntity<List<RoleDTO>> getAllRoles();
//
//    ResponseEntity<ObjectResponse> getUserByEmail(String email);
}
