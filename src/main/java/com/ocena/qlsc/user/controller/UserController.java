package com.ocena.qlsc.user.controller;


import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.*;
import com.ocena.qlsc.user.model.User;
import com.ocena.qlsc.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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


//    @PostMapping("/login")
//    public ResponseEntity<ObjectResponse> login(@Valid @RequestBody LoginRequest loginRequest,
//                                                BindingResult result,
//                                                HttpServletRequest request) {
//        return userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword(), request, result);
//    }

//    @GetMapping("/get-all-user")
//    public ResponseEntity<ObjectResponse> getAllUser() {
//        return userService.getAllUser();
//    }


    @Override
    public DataResponse<User> add(UserDTO objectDTO) {
        return userService.validateRegister(objectDTO);
    }

//    @PostMapping("/create-user")
//    public DataResponse<User> createUser(@Valid @RequestBody UserDTO dto,
//                                         BindingResult result) {
//
//    }

//    @GetMapping("/get-user-by-email/{email}")
//    public ResponseEntity<ObjectResponse> getUserByID(@PathVariable("email") String email) {
//        return userService.getUserByEmail(email);
//    }
//
//    @GetMapping("/get-roles")
//    public ResponseEntity<List<RoleDTO>> getRoles() {
//        return userService.getAllRoles();
//    }
//
//    @PostMapping("/forgot-password/sent-otp")
//    public ResponseEntity<ObjectResponse> SentOTP(@RequestParam String email, HttpServletRequest request) {
//        return userService.sentOTP(email, request);
//    }
//
//    @PostMapping("/forgot-password/verify")
//    public ResponseEntity<ObjectResponse> forgotPassword(@RequestParam String email, @RequestParam Integer OTP, @RequestParam String newPassword, String rePassword) {
//        return userService.validateOTP(email, OTP, newPassword, rePassword);
//    }
}
