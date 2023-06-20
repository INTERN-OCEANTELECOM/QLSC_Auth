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


    /**
     * Creates a new user based on the provided registration request.
     * @param registerRequest The registration request containing the user information.
     * @return True if the user is created successfully; false if the role
     * does not exist in the database or the email already exists in the database.
     */
    @Override
    public boolean createUser(RegisterRequest registerRequest) {
        // Map registerRequest to User model
        User user = mapper.convertTo(registerRequest, User.class);

        // Get all roles from the database
        List<Object[]> listRoles = userRepository.getAllRoles();
        for(Role role : registerRequest.getRoles()) {
            // Check if each roleId exists in the database
            if(!listRoles.stream().anyMatch(objs -> objs[0].equals(role.getRoleId()))) {
                return false;
            }
        }

        // Check if the email already exists in the database
        if(userRepository.existsByEmail(registerRequest.getEmail()).size() > 0) {
            // User already exists in the database
            return false;
        } else {
            // Encode the user's password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            UUID uuid = UUID.randomUUID();
            user.setUserId(uuid.toString());

            // Set the current date as the created timestamp
            Long currentTimeMillis = new Date().getTime();
            user.setCreated(currentTimeMillis);

            user.setStatus((short) 0);
            user.delete();

            // Save the user in the database and return true if successful, false otherwise
            return userRepository.save(user) != null ? true : false;
        }
    }

    /**
     * Validates user registration data and returns a ResponseEntity object
     * containing a UserResponse object.
     * @param registerRequest : registerRequest Object (DTO) receive from request
     * @param result : The BindingResult object that holds the result of the data validation process.
     * @return The ResponseEntity object contains the register result information
     */
    @Override
    public ResponseEntity<ObjectResponse> validateRegister(RegisterRequest registerRequest, BindingResult result) {
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
     * Retrieves all users from the database and returns a response with the user data.
     * @return A ResponseEntity containing the result of retrieving all users and the corresponding user data.
     */
    @Override
    public ResponseEntity<ObjectResponse> getAllUser() {
        // Get all user data from the database
        List<Object[]> listUserDB = userRepository.getAllUser();

        // Convert object data to UserResponse objects
        List<User> listUser = new ArrayList<>();
        List<Role> role = new ArrayList<>();
        List<UserResponse> listUserDTO = new ArrayList<>();

        for (Object[] user : listUserDB) {
            if (user[5] instanceof Role) {
                role = Collections.singletonList((Role) user[5]);
                System.out.println("List 1");
            } else if (user[5] instanceof List<?>) {
                role = (List<Role>) user[5];
                System.out.println("List n");
            }

            listUser.add(User.builder()
                    .fullName((String) user[0])
                    .email((String) user[1])
                    .phoneNumber((String) user[2])
                    .password((String) user[3])
                    .status((Short) user[4])
                    .roles(role)
                    .build());
        }

        // Convert User objects to UserResponse objects using a mapper
        listUserDTO = listUser.stream()
                .map(user -> mapper.convertTo(user, UserResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ObjectResponse("Success", "Get all user sucessfully", listUserDTO)
        );
    }

    /**
     * Authenticates the user by checking if the provided email and password match an existing user in the database.
     * @param email
     * @param password
     * @return A UserResponse object containing the authenticated user's information,
     * or an empty UserResponse object if authentication fails.
     */
    private UserResponse isAuthenticate(String email, String password) {
        // Check if the user exists in the database based on the email
        List<Object[]> listUser = userRepository.existsByEmail(email);

        UserResponse userResponse = new UserResponse();
        // If the user exists in the database
        if (!listUser.isEmpty()) {
            Object[] userLogin = listUser.get(0);

            // // Check the user's status and password match
            if ((Short) userLogin[2] != 2 && passwordEncoder.matches(
                    password, String.valueOf(userLogin[1].toString()))) {

                // Set the user's information in the UserResponse object
                userResponse.setEmail(userLogin[0].toString());
                userResponse.setPassword(userLogin[1].toString());
                userResponse.setStatus((Short) userLogin[2]);
            }
        }

        return userResponse;
    }

    /**
     * A function that handles limiting the number of failed login attempts
     * and handling login result
     * @param email email of user
     * @param session HttpSession to store session information
     * @param result BidingResult object to check for errors when validating input data
     * @return The ResponseEntity object contains the login result information
     */
    public ResponseEntity<ObjectResponse> handleLoginAttempts(String email, HttpSession session, BindingResult result) {
        Long lockedTime;

        // Increase the number of false logins
        loginAttempts++;

        System.out.println("Attempts: " + loginAttempts);
        if(loginAttempts >= 3) {

            // Set time out is 60s
            lockedTime = System.currentTimeMillis() / 1000 + 60;
            session.setAttribute("lockedTime", lockedTime);

            // Reset false login attempts to 0
            loginAttempts = 0;

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
            );
        }

        if((result.hasErrors())) {

            // Get list of error messages from BindingResult
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

    /**
     * Validates the login credentials provided by the user.
     * @param email
     * @param password
     * @param request
     * @param result The BindingResult object to check for input validation errors.
     * @return A ResponseEntity containing the validation result and response object.
     */
    @Override
    public ResponseEntity<ObjectResponse> validateLogin(String email, String password,
                                                        HttpServletRequest request, BindingResult result) {
        HttpSession session = request.getSession();

        // Check if the account is temporarily locked
        Long lockedTime = (Long) session.getAttribute("lockedTime");
        if(lockedTime != null)  {
            if(System.currentTimeMillis() / 1000 < lockedTime) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
                );
            }
            session.setAttribute("lockedTime", 0L);
        }

        // Authenticate the email and password
        if(isAuthenticate(email, password).getEmail() != null) {
            loginAttempts = 0;
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ObjectResponse("OK", "Login Successfully", isAuthenticate(email ,password))
            );
        }
        else {
            // Handle login attempts for wrong login
            return handleLoginAttempts(email, session, result);
        }
    }

    /**
     * Retrieves a list of all roles in the system.
     * Each role includes the roleId and roleName.
     * @return A ResponseEntity containing a list of RoleResponse objects.
     */
    @Override
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        // Retrieve all roles from the UserRepository and convert them to RoleResponse objects
        List<RoleResponse> listRoles = userRepository.getAllRoles()
                                        .stream()
                                        .map(objs -> {
                                            return new RoleResponse(
                                                    Integer.valueOf(objs[0].toString()),
                                                    objs[1].toString());
                                        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(listRoles);
    }

    /**
     * Sent Email OTP
     *
     * @param email: User's email
     * @param request: Request to be blocked when sending OTP.
     * @return ResponseEntity UserResponse
     */
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

    /**
     * Validate OTP When User ResetPassword
     *
     * @param email: User's email
     * @param OTP: The OTP received by the user in the email
     * @return ResponseEntity UserResponse
     */
    @Override
    public ResponseEntity<ObjectResponse> validateOTP(String email, Integer OTP, String newPassword, String rePassword) {
        String message = "Something Went Wrong!!";

        if (newPassword.equals(rePassword)){
            if (newPassword.length() < 8 || newPassword.contains(" ")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ObjectResponse("Fail", "The password must have a minimum length of 8 characters and should not contain any spaces", "")
                );
            }

            message = otpService.validateOTP(email, OTP);

            if (message.equals("GET OTP Success!!!")){
                //Get time forgot password
                Long currentTimeMillis = new Date().getTime();

                //Hash Password
                String passwordHash = passwordEncoder.encode(newPassword);

                int update = userRepository.forgotPassword(passwordHash,1, email, currentTimeMillis, email);

                if (update != 0) {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ObjectResponse("Success", message, "")
                    );
                } else {
                    message = "Unable to update password";
                }
            }
        } else {
            message = "Passwords do not match";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ObjectResponse("Fail", message, "")
        );
    }

    @Override
    public ResponseEntity<ObjectResponse> deleteUser(String emailUser, String emailModifier) {
        String message = "Something Went Wrong!!";

        List<Role> listRoles = userRepository.getRoleByEmail(emailModifier);

        boolean hasRoleOne = false;
        for (Role role : listRoles) {
            System.out.println("Role là: " + role.getRoleName());
            if(role.getRoleId().toString().equals("1")){
                hasRoleOne = true;
                break;
            }
        }

        if (hasRoleOne && !emailModifier.equals(emailUser)){
            //Get time
            Long currentTimeMillis = new Date().getTime();

            int update = userRepository.update(emailModifier, (short) 2, currentTimeMillis, true, emailUser);

            if (update != 0) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ObjectResponse("Success", "Delete User Success", "")
                );
            } else {
                message = "User to be deleted not found";
            }
        } else {
            message = "User does not have delete permission";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ObjectResponse("Fail", message, "")
        );
    }

    @Override
    public ResponseEntity<ObjectResponse> updateUser(String emailUser, String emailModifier, String fullName, String phoneNumber, String email) {
        List<Role> listRoles = userRepository.getRoleByEmail(emailModifier);

        User user = User.builder().status((short)1)
                .updated((Long)new Date().getTime())
                .email(email)
                .phoneNumber(phoneNumber)
                .userId(userRepository.getIdByEmail(emailUser))
                .build();

        for (Role role : listRoles) {
            System.out.println("Role là: " + role.getRoleName());
            if(role.getRoleId().toString().equals("1")){

            } else if (role.getRoleId().toString().equals("2")){
            }
        }
        return null;
    }
}

