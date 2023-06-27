package com.ocena.qlsc.user.controller;


import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.dto.ChangeStatusDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.*;
import com.ocena.qlsc.user.model.User;
import com.ocena.qlsc.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class UserController extends BaseApiImpl<User, UserDTO> {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected BaseService<User, UserDTO> getBaseService() {
        return userService;
    }

    @PostMapping ("/login")
    public DataResponse<User> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userService.validateLogin(loginRequest, request);
    }

//    @PostMapping("/delete")
//    public DataResponse<User> deleteUser(@RequestParam String emailUser, HttpServletRequest request) {
//        String email = request.getHeader("email");
//        return userService.deleteUser(emailUser, email);
//    }

    @Override
    public ListResponse<UserDTO> getAll() {
        return userService.getAllUser();
    }


    @Override
    public DataResponse<User> add(UserDTO objectDTO) {
        if(userService.validateCreate(objectDTO)) {
            objectDTO.setPassword(passwordEncoder.encode(objectDTO.getPassword()));
            return super.add(objectDTO);
        } else {
            return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }
    }

    @Override
    public DataResponse<UserDTO> getById(String id) {
        return super.getById(id);
    }

    @PostMapping("/forgot-password/sent-otp")
    public DataResponse<User> SentOTP(@RequestParam String email, HttpServletRequest request) {
        return userService.sentOTP(email, request);
    }

    @PostMapping("/forgot-password/verify")
    public DataResponse<User> forgetPasswordOTP(@RequestParam String email, @RequestParam Integer OTP, @RequestParam String newPassword) {
        return userService.validateOTP(email, OTP, newPassword);
    }

//    @PutMapping ("/update")
//    public DataResponse<User> updateUser(@RequestParam String emailUser,
//                                         @RequestBody UserDTO userDTO) {
//        return userService.updateUser(emailUser, userDTO);
//    }

    @Override
    public DataResponse<User> update(UserDTO objectDTO, String key) {
        if(userService.validateUpdateUser(key, objectDTO) == null) {
            return ResponseMapper.toDataResponseSuccess(null);
        } else if (userService.validateUpdateUser(key, objectDTO)) {
            // isAdmin send request
            return super.update(objectDTO, key);
        } else {
            objectDTO.setRoles(null);
            objectDTO.setEmail(key);
            return super.update(objectDTO, key);
        }

    }

    @PostMapping ("/reset-password")
    public DataResponse<User> resetPassword(@RequestParam String oldPassword,
                                            @RequestParam String newPassword,
                                            HttpServletRequest request) {
        String email = request.getHeader("email");
        return userService.resetPassword(email, oldPassword, newPassword);
    }

    @Override
    public DataResponse<UserDTO> delete(String email) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String emailHeader = request.getHeader("email");
        System.out.println(emailHeader);
        return userService.validateDeleteUser(email, emailHeader) ? super.delete(email) :
                ResponseMapper.toDataResponse("", StatusCode.NOT_IMPLEMENTED, StatusMessage.NOT_IMPLEMENTED);
    }
}
