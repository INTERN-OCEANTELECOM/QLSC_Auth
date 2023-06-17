package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.dto.RoleResponse;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.configs.Mapper.Mapper;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.model.Role;
import com.ocena.qlsc.model.User;
import com.ocena.qlsc.repository.UserRepository;
import com.ocena.qlsc.sendmail.OTPService;
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

    private final Map<String, Integer> loginAttempts = new HashMap<>();
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
        if(userRepository.existsByEmail(registerRequest.getUserName()).size() > 0) {
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
                        new ObjectResponse("Create", "User already exists in the database", "")
                );
            }
        }
    }

    /**
     * {@inheritDoc}
     * Validate Request Login
     * @param loginRequest
     * @param result
     * @return ResponseEntity with type UserResponse
     */
    @Override
    public ResponseEntity<ObjectResponse> validateUser(LoginRequest loginRequest, BindingResult result, HttpServletRequest request) {
        /* Using BindingResult of springframework-validator to check condition LoginRequest*/
        if((result.hasErrors())) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ObjectResponse("Fail", errorMessages.get(0).toString(), "")
            );
        }

        /* Check username and password in DB*/
        return validateLogin(loginRequest.getEmail(), loginRequest.getPassword(), request);
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
                        .userName((String) user[3])
                        .password((String) user[4])
                        .status((Short) user[5])
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
     * @return ResponseEntity with type UserResponse
     */
    private ResponseEntity<ObjectResponse> validateLogin(String email, String password) {
        /* Check Gmail*/
        if ( !email.endsWith("@daiduongtelecom.com")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ObjectResponse("Fail", "Email is not associated with the organization", "")
            );
        }

    private boolean isAuthenticate(String email, String password) {
        List<Object[]> listUser = userRepository.existsByEmail(email);

        boolean isValid = false;
        // Exist User in DB
        if (!listUser.isEmpty()) {
            Object[] userLogin = listUser.get(0);

            // Status another state 2 and matches with password in db  => true
            if ((Short) userLogin[2] != 2 && passwordEncoder.matches(
                    password, String.valueOf(userLogin[1].toString()))) {
                return true;
            }

        }

        return isValid;
    }
    private ResponseEntity<ObjectResponse> validateLogin(String email, String password, HttpServletRequest request) {
        // Check Gmail
        HttpSession session = request.getSession();

        // Check if locked accounts
        Long lockedTime = (Long) session.getAttribute("lockedTime");
        if(lockedTime != null)  {
            if(System.currentTimeMillis() / 60 < lockedTime) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
                );
            }
            session.setAttribute("lockedTime", 0L);
        }

        // Check Email and Password
        if(isAuthenticate(email, password)) {
            loginAttempts.remove(email);
            return ResponseEntity.status(HttpStatus.OK).body(new ObjectResponse("OK", "Login Successfully", ""));
        }
        else {
            // attempts attempts by 1 on wrong login
            int attempts = loginAttempts.getOrDefault(email, 0);
            attempts++;
            loginAttempts.put(email, attempts);

            System.out.println("attempts: " + attempts);
            if(attempts >= 3) {

                // Set time out is 15 = 30 minutes
                lockedTime = System.currentTimeMillis() / 1000 + 60;
                session.setAttribute("lockedTime", lockedTime);

                loginAttempts.remove(email);

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
                );
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ObjectResponse("Failed", "Invalid username or password.", "")
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
    public ResponseEntity<ObjectResponse> sentOTP(String email) {
        String message = otpService.generateOtp(email);
        if (message.equals("OTP Has Been Sent!!!")){
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

