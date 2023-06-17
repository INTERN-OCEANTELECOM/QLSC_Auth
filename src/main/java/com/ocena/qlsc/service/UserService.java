package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.*;
import com.ocena.qlsc.configs.Mapper.Mapper;
import com.ocena.qlsc.model.Role;
import com.ocena.qlsc.model.User;
import com.ocena.qlsc.repository.UserRepository;
import com.ocena.qlsc.utils.sendmail.OTPService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OTPService otpService;

    private static Integer loginAttempts = 0;

    // Registers a user and returns a boolean value,
    // True: create user succesfully, false: user created failed.
    @Override
    public boolean createUser(RegisterRequest registerRequest) {
        // Map registerRequest to User model
        User user = mapper.convertTo(registerRequest, User.class);

        // Get All Roles
        List<Object[]> listRoles = userRepository.getAllRoles();
        for(Role role : registerRequest.getRoles()) {
            // Check roleId exists in db
            if(!listRoles.stream().anyMatch(objs -> objs[0].equals(role.getRoleId()))) {
                return false;
            }
        }
        if(userRepository.existsByEmail(registerRequest.getEmail()).size() > 0) {
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

            return userRepository.save(user) != null ? true : false;
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
    public ResponseEntity<ObjectResponse> validateRegister(RegisterRequest registerRequest, BindingResult result) {
        // implementation
        if((result.hasErrors())) {
            // User is invalid
            // Get Errors List
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ObjectResponse("Create", "User is invalid", errorMessages)
            );
        } else {
            // User is valid
            // Check if user has been created successfully
            if(createUser(registerRequest)) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ObjectResponse("Create", "Create User successfully", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new ObjectResponse("Failed", "User already exists in the database", "")
                );
            }
        }
    }

    /**
     * Get all user
     * @return List UserResponse
     */
    @Override
    public ResponseEntity<ObjectResponse> getAllUser() {
        /* Get All User From DB*/
        List<Object[]> listUserDB = userRepository.getAllUser();

        /* Convert Object to UserResponse*/
        List<User> listUser = new ArrayList<>();
        List<Role> role = new ArrayList<>();
        List<UserResponse> listUserDTO = new ArrayList<>();

        if (!listUserDB.isEmpty()) {
            for (Object[] user : listUserDB) {

                if (user[6] instanceof Role) {
                    role = Collections.singletonList((Role) user[6]);
                    System.out.println("Role " + role.get(0).getRoleName());
                } else if (user[5] instanceof List<?>) {
                    role = (List<Role>) user[6];
                }

                User user1 = User.builder()
                        .fullName((String) user[0])
                        .email((String) user[1])
                        .phoneNumber((String) user[2])
                        .password((String) user[3])
                        .status((Short) user[4])
                        .roles(role)
                        .build();

                listUser.add(user1);
            }

            listUserDTO = listUser
                    .stream()
                    .map(user -> mapper.convertTo(user, UserResponse.class))
                    .collect(Collectors.toList());
        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ObjectResponse("Fail", "Get All User Fail", ""));
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ObjectResponse("Success", "Get All User Success", listUserDTO)
        );
    }

    /**
     * Authenticate Login
     * @param email
     * @param password
     * @return if result is true then user valid and in exist in DB else is false
     */
    private UserResponse isAuthenticate(String email, String password) {
        List<Object[]> listUser = userRepository.existsByEmail(email);

        UserResponse userResponse = new UserResponse();
        // Exist User in DB
        if (!listUser.isEmpty()) {
            Object[] userLogin = listUser.get(0);

            // Status another state 2 and matches with password in db  => true
            if ((Short) userLogin[2] != 2 && passwordEncoder.matches(
                    password, String.valueOf(userLogin[1].toString()))) {
                userResponse.setEmail(userLogin[0].toString());
                userResponse.setPassword(userLogin[1].toString());
                userResponse.setStatus((Short) userLogin[2]);
            }

        }

        return userResponse;
    }

    public ResponseEntity<ObjectResponse> handleLoginAttempts(String email, HttpSession session, BindingResult result) {
        Long lockedTime;
//        int attempts = loginAttempts.getOrDefault(email, 0);
//        attempts++;
//        loginAttempts.put(email, attempts);
        loginAttempts++;

        System.out.println("Attempts: " + loginAttempts);
        if(loginAttempts >= 3) {

            // Set time out is 60s
            lockedTime = System.currentTimeMillis() / 1000 + 60;
            session.setAttribute("lockedTime", lockedTime);

            loginAttempts = 0;

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
            );
        }

        if((result.hasErrors())) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ObjectResponse("Failed", errorMessages.get(0).toString(), "")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ObjectResponse("Failed", "Invalid email or password.", "")
        );
    }


    @Override
    public ResponseEntity<ObjectResponse> validateLogin(String email, String password,
                                                        HttpServletRequest request, BindingResult result) {
        // Check Gmail
        HttpSession session = request.getSession();

        // Check if locked accounts
        Long lockedTime = (Long) session.getAttribute("lockedTime");
        if(lockedTime != null)  {
            if(System.currentTimeMillis() / 1000 < lockedTime) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
                );
            }
            session.setAttribute("lockedTime", 0L);
        }

        // Check Email and Password
        if(isAuthenticate(email, password).getEmail() != null) {
            loginAttempts = 0;
            return ResponseEntity.status(HttpStatus.OK).body(new ObjectResponse("OK", "Login Successfully", isAuthenticate(email ,password)));
        }
        else {
            // If attempts by 1 on wrong login
            return handleLoginAttempts(email, session, result);
        }
    }

    /**
     * Return a ResponseEntity containing a list of Roles in system
     * Roles include roleId and roleName
     * @return ResponseEntity List RoleResponse
     */
    @Override
    public ResponseEntity<List<RoleResponse>> getAllRoles() {

        // Get All Users then convert to RoleResponse
        List<RoleResponse> listRoles = userRepository.getAllRoles()
                                        .stream()
                                        .map(objs -> {
                                            return new RoleResponse(
                                                    Integer.valueOf(objs[0].toString()),
                                                    objs[1].toString());
                                        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(listRoles);
    }

    @Override
    public ResponseEntity<ObjectResponse> sentOTP(String email, HttpServletRequest request) {
        HttpSession session = request.getSession();

        // Check if locked sent OTP
        Long lockedTimeOTP = (Long) session.getAttribute("lockedTimeOTP");

        if(lockedTimeOTP != null)  {
            if(System.currentTimeMillis() / 1000 < lockedTimeOTP) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ObjectResponse("Failed", "Please wait for "+ (lockedTimeOTP - (System.currentTimeMillis() / 1000))+" seconds and try again", lockedTimeOTP)
                );
            }
            session.setAttribute("lockedTimeOTP", 0L);
        }

        // GenerateOTP and sent mail OTP
        String message = otpService.generateOtp(email);

        if (message.equals("OTP Has Been Sent!!!")){
            // Set time out is 60s
            lockedTimeOTP = System.currentTimeMillis() / 1000 + 60;
            session.setAttribute("lockedTimeOTP", lockedTimeOTP);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ObjectResponse("Success", message, "")
            );
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ObjectResponse("Fail", message, "")
        );
    }

    @Override
    public ResponseEntity<ObjectResponse> validateOTP(String email, Integer OTP) {
        String message = otpService.validateOTP(email, OTP);
        if (message.equals("OTP Has Been Sent!!!")){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ObjectResponse("Success", message, "")
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ObjectResponse("Fail", message, "")
        );
    }
}

