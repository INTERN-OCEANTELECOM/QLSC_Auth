package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.configs.Mapper.Mapper;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.model.User;
import com.ocena.qlsc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class UserService implements IUserService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Registers a user and returns a boolean value,
    // True: create user succesfully, false: user created failed.
    @Override
    public boolean createUser(RegisterRequest registerRequest) {
        // Map registerRequest to User model
        User user = mapper.convertTo(registerRequest, User.class);

        if(userRepository.existsByUsername(registerRequest.getUserName()).size() > 0) {
            // User already exists in the database
            return false;
        } else {
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Set userId with uuid
            UUID uuid = UUID.randomUUID();
            user.setUserId(uuid.toString());

            // Get current Date
            Long currentTimeMillis = new Date().getTime();
            user.setCreated(currentTimeMillis);

            // Trang thai moi
            user.setStatus((short) 0);

            // set logic delete is true
            user.delete();

            return userRepository.save(user) != null;
        }
    }

    /**
     * Validates user registration data and returns a ResponseEntity object
     * /containing a UserResponse object.
     * @param registerRequest : registerRequest Object (DTO) receive from request
     * @param result : The BindingResult object that holds the result of the data validation process.
     * @return ResponseEntity UserResponse
     */
    @Override
    public ResponseEntity<UserResponse> validateRegister(RegisterRequest registerRequest, BindingResult result) {
        // implementation
        if((result.hasErrors())) {
            // User is invalid
            // Get Errors List
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new UserResponse("Create", "User is invalid", errorMessages)
            );
        } else {
            // User is valid
            // Check if user has been created successfully
            if(createUser(registerRequest)) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new UserResponse("Create", "Create User successfully", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new UserResponse("Create", "User already exists in the database", "")
                );
            }
        }
    }

    /* Validate Request Login */
    @Override
    public ResponseEntity<UserResponse> validateUser(LoginRequest loginRequest, BindingResult result) {
        /* Using BindingResult of springframework-validator to check condition LoginRequest*/
        if((result.hasErrors())) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new UserResponse("Fail", errorMessages.get(0).toString(), "")
            );
        }

        /* Check username and password in DB*/
        return validateLogin(loginRequest.getUserName(),loginRequest.getPassword());
    }

    /* Authenticate Login*/
    private ResponseEntity<UserResponse> validateLogin(String username, String password) {
        /* Get User by Username*/
        List<Object[]> listUser = userRepository.existsByUsername(username);

        /* Check UserName and Password*/
        if (!listUser.isEmpty()) {
            Object[] userLogin = listUser.get(0);

            if (passwordEncoder.matches(password, String.valueOf(userLogin[1].toString()))){
                return ResponseEntity.status(HttpStatus.OK).body(
                        new UserResponse("Success", "Login Success", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new UserResponse("Fail", "Incorrect password", "")
                );
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new UserResponse("Fail", "User Not Found", "")
            );
        }
    }

    /**
     * Return a ResponseEntity containing a list of Roles in system
     * Roles include roleId and roleName
     * @return ResponseEntity List RoleResponse
     */
    @Override
    public ResponseEntity<List<RoleResponse>> getAllRoles() {

        List<RoleResponse> listRoles = userRepository.getAllRoles()
                                        .stream()
                                        .map(objs -> {
                                            return new RoleResponse(
                                                    Integer.valueOf(objs[0].toString()),
                                                    objs[1].toString());
                                        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(listRoles);
    }
}

