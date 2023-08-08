package com.ocena.qlsc.user.controller;


import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.error.exception.NotPermissionException;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.user.dto.*;
import com.ocena.qlsc.user.model.User;
import com.ocena.qlsc.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/user")
//@CrossOrigin(value = "*")
@RequiredArgsConstructor
public class UserController extends BaseApiImpl<User, UserDto> {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected BaseService<User, UserDto> getBaseService() {
        return userService;
    }

    @PostMapping ("/login")
    @ApiShow
    public DataResponse<User> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userService.login(loginRequest, request);
    }

    @PutMapping ("/update")
    @ApiShow
    @CacheEvict(value = {"getAllUser", "getUserRole", "validateUser"}, allEntries = true)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    public DataResponse<User> updateUser(@RequestParam String key,
                                         @Valid @RequestBody UserDto userDTO) {
        return userService.updateUser(key, userDTO);
    }

    @Override
    @ApiShow
    @Cacheable(value = "getAllUser")
    public ListResponse<UserDto> getAll() {
        return super.getAll();
    }

    @Override
    @ApiShow
    @CacheEvict(value = {"getAllUser", "getUserRole", "validateUser"}, allEntries = true)
    public DataResponse<UserDto> add(@Valid UserDto objectDTO) {
        objectDTO.setPassword(passwordEncoder.encode(objectDTO.getPassword()));
        return super.add(objectDTO);
    }

    @Override
    @ApiShow
    public DataResponse<UserDto> getById(String id) {
        return super.getById(id);
    }

    @PostMapping("/forgot-password/sent-otp")
    @ApiShow
    public DataResponse<User> SentOTP(@RequestParam String email, HttpServletRequest request) {
        return userService.sentOTP(email, request);
    }

    @PostMapping("/forgot-password/verify")
    @ApiShow
    public DataResponse<User> forgetPasswordOTP(@RequestParam String email, @RequestParam Integer OTP, @RequestParam String newPassword) {
        return userService.validateOTP(email, OTP, newPassword);
    }

    @PostMapping ("/reset-password")
    @ApiShow
    @CacheEvict(value = {"getAllUser"}, allEntries = true)
    @Parameter(in = ParameterIn.HEADER, name = "email", description = "Email Header")
    public DataResponse<User> resetPassword(@RequestParam String oldPassword,
                                            @RequestParam String newPassword,
                                            HttpServletRequest request) {
        String email = request.getHeader("email");
        return userService.resetPassword(email, oldPassword, newPassword);
    }

    @Override
    @ApiShow
    @CacheEvict(value = {"getAllUser", "getUserRole", "validateUser"}, allEntries = true)
    public DataResponse<UserDto> delete(String email) {
        String emailModify = SystemUtil.getCurrentEmail();
        if(!userService.hasDeleteUserPermission(email, emailModify)) {
            throw new NotPermissionException();
        }
        return super.delete(email);
    }

    @Override
    @ApiShow
    public ListResponse<User> getAllByKeyword(String keyword) {
        return super.getAllByKeyword(keyword);
    }
}
