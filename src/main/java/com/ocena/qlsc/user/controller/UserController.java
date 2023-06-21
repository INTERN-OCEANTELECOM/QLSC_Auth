package com.ocena.qlsc.user.controller;


import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.*;
import com.ocena.qlsc.user.model.User;
import com.ocena.qlsc.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin(origins = "*")
public class UserController extends BaseApiImpl<User, UserDTO> {
    @Autowired
    UserService userService;

    @Override
    protected BaseService<User, UserDTO> getBaseService() {
        return userService;
    }


    @PostMapping("/login")
    public DataResponse<User> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userService.validateLogin(loginRequest, request);
    }

    @PostMapping("/delete")
    public DataResponse<User> deleteUser(@RequestParam String emailUser, HttpServletRequest request) {
        String email = request.getHeader("email");
        return userService.deleteUser(emailUser, email);
    }

    @Override
    public ListResponse<UserDTO> getAll() {
        return userService.getAllUser();
    }

    @Override
    public DataResponse<User> add(UserDTO objectDTO) {
        return userService.validateRegister(objectDTO);
    }

    @GetMapping("/get-user/{email}")
    public DataResponse<User> getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/get-roles")
    public ListResponse<List<RoleDTO>> getRoles() {
        return userService.getAllRoles();
    }

    @PostMapping("/forgot-password/sent-otp")
    public DataResponse<User> SentOTP(@RequestParam String email, HttpServletRequest request) {
        return userService.sentOTP(email, request);
    }

    @PostMapping("/forgot-password/verify")
    public DataResponse<User> forgotPassword(@RequestParam String email, @RequestParam Integer OTP, @RequestParam String newPassword, String rePassword) {
        return userService.validateOTP(email, OTP, newPassword, rePassword);
    }

    @PutMapping ("/update")
    public DataResponse<User> updateUser(@RequestParam String emailUser, @RequestBody UserDTO userDTO) {
        return userService.updateUser(emailUser, userDTO);
    }
}
