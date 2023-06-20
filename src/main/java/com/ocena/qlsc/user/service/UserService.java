package com.ocena.qlsc.user.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.user.Mapper.UserMapper;
import com.ocena.qlsc.user.dto.RoleDTO;
import com.ocena.qlsc.user.dto.UserDTO;
import com.ocena.qlsc.user.model.User;
import com.ocena.qlsc.user.repository.UserRepository;
import com.ocena.qlsc.user.configs.sendmail.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseServiceImpl<User, UserDTO> implements IUserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OTPService otpService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private LocalValidatorFactoryBean validator;
//    private static Integer loginAttempts = 0;

    @Override
    protected BaseRepository<User> getBaseRepository() {
        return userRepository;
    }

    @Override
    protected BaseMapper<User, UserDTO> getBaseMapper() {
        return userMapper;
    }

    @Override
    protected Page<User> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<User> getListSearchResults(String keyword) {
        return null;
    }


    /**
     * Creates a new user based on the provided registration request.
     * @param dto The registration request containing the user information.
     * @return True if the user is created successfully; false if the role
     * does not exist in the database or the email already exists in the database.
     */
    @Override
    public DataResponse<User> create(UserDTO dto) {
        // Get all roles from the database
        List<Object[]> listRoles = userRepository.getAllRoles();
        for(RoleDTO role : dto.getRoles()) {
            // Check if each roleId exists in the database
            if(!listRoles.stream().anyMatch(objs -> objs[0].equals(role.getId()))) {
                return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
            }
        }

        if(userRepository.existsByEmail(dto.getEmail()).size() > 0) {
            return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
        }
        // Encode the user's password
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        return super.create(dto);
    }

//    @Override
//    public boolean createUser(RegisterRequest registerRequest) {
//        // Map registerRequest to User model
////        User user = mapper.convertTo(registerRequest, User.class);
//        User user = getBaseMapper().dtoToEntity(registerRequest);
////        User user = mapper
//
//        // Get all roles from the database
//        List<Object[]> listRoles = userRepository.getAllRoles();
//        for(Role role : registerRequest.getRoles()) {
//            // Check if each roleId exists in the database
//            if(!listRoles.stream().anyMatch(objs -> objs[0].equals(role.getRoleId()))) {
//                return false;
//            }
//        }
//
//        // Check if the email already exists in the database
//        if(userRepository.existsByEmail(registerRequest.getEmail()).size() > 0) {
//            // User already exists in the database
//            return false;
//        } else {
//            // Encode the user's password
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//
////            UUID uuid = UUID.randomUUID();
////            user.setUserId(uuid.toString());
//
//            // Set the current date as the created timestamp
//            Long currentTimeMillis = new Date().getTime();
//            user.setCreated(currentTimeMillis);
//
//            user.setStatus((short) 0);
////            user.delete();
//
//            // Save the user in the database and return true if successful, false otherwise
//            return userRepository.save(user) != null ? true : false;
//        }
//    }

    /**
     * Validates user registration data and returns a ResponseEntity object
     * containing a UserResponse object.
     * @param dto : registerRequest Object (DTO) receive from request
     * The BindingResult object that holds the result of the data validation process.
     * @return The ResponseEntity object contains the register result information
     */
    @Override
    public DataResponse<User> validateRegister(UserDTO dto) {
        Errors result = new BeanPropertyBindingResult(dto, "userDTO");
        validator.validate(dto, result);

        if((result.hasErrors())) {
            // User is invalid
            // Get Errors List
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseMapper.toDataResponse(errorMessages, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }
        else {
            return create(dto);
        }
    }


    /**
     * Retrieves all users from the database and returns a response with the user data.
     * @return A ResponseEntity containing the result of retrieving all users and the corresponding user data.
     */
//    @Override
//    public ResponseEntity<ObjectResponse> getAllUser() {
//        // Get all user data from the database
//        List<Object[]> listUserDB = userRepository.getAllUser();
//
//        // Convert object data to UserResponse objects
//        List<User> listUser = new ArrayList<>();
//        List<Role> role = new ArrayList<>();
//        List<UserDTO> listUserDTO = new ArrayList<>();
//
//        for (Object[] user : listUserDB) {
//            if (user[5] instanceof Role) {
//                role = Collections.singletonList((Role) user[5]);
//                System.out.println("List 1");
//            } else if (user[5] instanceof List<?>) {
//                role = (List<Role>) user[5];
//                System.out.println("List n");
//            }
//
//            listUser.add(User.builder()
//                    .fullName((String) user[0])
//                    .email((String) user[1])
//                    .phoneNumber((String) user[2])
//                    .password((String) user[3])
//                    .status((Short) user[4])
//                    .roles(role)
//                    .build());
//        }
//
//        // Convert User objects to UserResponse objects using a mapper
//        listUserDTO = listUser.stream()
//                .map(user -> mapper.convertTo(user, UserDTO.class))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ObjectResponse("Success", "Get all user sucessfully", listUserDTO)
//        );
//    }

//    /**
//     * Authenticates the user by checking if the provided email and password match an existing user in the database.
//     * @param email
//     * @param password
//     * @return A UserResponse object containing the authenticated user's information,
//     * or an empty UserResponse object if authentication fails.
//     */
//    private UserDTO isAuthenticate(String email, String password) {
//        // Check if the user exists in the database based on the email
//        List<Object[]> listUser = userRepository.existsByEmail(email);
//
//        UserDTO userResponse = new UserDTO();
//        // If the user exists in the database
//        if (!listUser.isEmpty()) {
//            Object[] userLogin = listUser.get(0);
//
//            // // Check the user's status and password match
//            if ((Short) userLogin[2] != 2 && passwordEncoder.matches(
//                    password, String.valueOf(userLogin[1].toString()))) {
//
//                // Set the user's information in the UserResponse object
//                userResponse.setEmail(userLogin[0].toString());
//                userResponse.setPassword(userLogin[1].toString());
//                userResponse.setStatus((Short) userLogin[2]);
//            }
//        }
//
//        return userResponse;
//    }
//
//    /**
//     * A function that handles limiting the number of failed login attempts
//     * and handling login result
//     * @param email email of user
//     * @param session HttpSession to store session information
//     * @param result BidingResult object to check for errors when validating input data
//     * @return The ResponseEntity object contains the login result information
//     */
//    public ResponseEntity<ObjectResponse> handleLoginAttempts(String email, HttpSession session, BindingResult result) {
//        Long lockedTime;
//
//        // Increase the number of false logins
//        loginAttempts++;
//
//        System.out.println("Attempts: " + loginAttempts);
//        if(loginAttempts >= 3) {
//
//            // Set time out is 60s
//            lockedTime = System.currentTimeMillis() / 1000 + 60;
//            session.setAttribute("lockedTime", lockedTime);
//
//            // Reset false login attempts to 0
//            loginAttempts = 0;
//
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                    new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
//            );
//        }
//
//        if((result.hasErrors())) {
//            // Get list of error messages from BindingResult
//            List<String> errorMessages = result.getFieldErrors()
//                    .stream()
//                    .map(FieldError::getDefaultMessage)
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new ObjectResponse("Failed", errorMessages.get(0).toString(), "")
//            );
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                new ObjectResponse("Failed", "Invalid email or password.", "")
//        );
//    }
//
//    /**
//     * Validates the login credentials provided by the user.
//     * @param email
//     * @param password
//     * @param request
//     * @param result The BindingResult object to check for input validation errors.
//     * @return A ResponseEntity containing the validation result and response object.
//     */
//    @Override
//    public ResponseEntity<ObjectResponse> validateLogin(String email, String password,
//                                                        HttpServletRequest request, BindingResult result) {
//        HttpSession session = request.getSession();
//
//        // Check if the account is temporarily locked
//        Long lockedTime = (Long) session.getAttribute("lockedTime");
//        if(lockedTime != null)  {
//            if(System.currentTimeMillis() / 1000 < lockedTime) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                        new ObjectResponse("Failed", "Your account is temporarily locked", lockedTime)
//                );
//            }
//            session.setAttribute("lockedTime", 0L);
//        }
//
//        // Authenticate the email and password
//        if(isAuthenticate(email, password).getEmail() != null) {
//            loginAttempts = 0;
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ObjectResponse("OK", "Login Successfully", isAuthenticate(email ,password))
//            );
//        }
//        else {
//            // Handle login attempts for wrong login
//            return handleLoginAttempts(email, session, result);
//        }
//    }
//
//    /**
//     * Retrieves a list of all roles in the system.
//     * Each role includes the roleId and roleName.
//     * @return A ResponseEntity containing a list of RoleResponse objects.
//     */
//    @Override
//    public ResponseEntity<List<RoleDTO>> getAllRoles() {
//        // Retrieve all roles from the UserRepository and convert them to RoleResponse objects
//        List<RoleDTO> listRoles = userRepository.getAllRoles()
//                                        .stream()
//                                        .map(objs -> {
//                                            return new RoleDTO(
//                                                    Integer.valueOf(objs[0].toString()),
//                                                    objs[1].toString());
//                                        }).collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.OK).body(listRoles);
//    }
//
//    /**
//     * Sent Email OTP
//     *
//     * @param email: User's email
//     * @param request: Request to be blocked when sending OTP.
//     * @return ResponseEntity UserResponse
//     */
//    @Override
//    public ResponseEntity<ObjectResponse> sentOTP(String email, HttpServletRequest request) {
//        HttpSession session = request.getSession();
//
//        // Check if locked sent OTP
//        Long lockedTimeOTP = (Long) session.getAttribute("lockedTimeOTP");
//
//        if(lockedTimeOTP != null)  {
//            if(System.currentTimeMillis() / 1000 < lockedTimeOTP) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                        new ObjectResponse("Failed", "Please wait for "+ (lockedTimeOTP - (System.currentTimeMillis() / 1000))+" seconds and try again", lockedTimeOTP)
//                );
//            }
//            session.setAttribute("lockedTimeOTP", 0L);
//        }
//
//        // GenerateOTP and sent mail OTP
//        String message = otpService.generateOtp(email);
//
//        if (message.equals("OTP Has Been Sent!!!")){
//            // Set time out is 60s
//            lockedTimeOTP = System.currentTimeMillis() / 1000 + 60;
//            session.setAttribute("lockedTimeOTP", lockedTimeOTP);
//
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ObjectResponse("Success", message, "")
//            );
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                new ObjectResponse("Fail", message, "")
//        );
//    }
//
//    /**
//     * Validate OTP When User ResetPassword
//     *
//     * @param email: User's email
//     * @param OTP: The OTP received by the user in the email
//     * @return ResponseEntity UserResponse
//     */
//    @Override
//    public ResponseEntity<ObjectResponse> validateOTP(String email, Integer OTP, String newPassword, String rePassword) {
//        String message = "Something Went Wrong!!";
//
//        if (newPassword.equals(rePassword)){
//            message = otpService.validateOTP(email, OTP);
//
//            if (message.equals("GET OTP Success!!!")){
//                //Get time forgot password
//                Long currentTimeMillis = new Date().getTime();
//
//                //Hash Password
//                String passwordHash = passwordEncoder.encode(newPassword);
//
//                int update = userRepository.forgotPassword(passwordHash,1, email, currentTimeMillis, email);
//
//                if (update != 0) {
//                    return ResponseEntity.status(HttpStatus.OK).body(
//                            new ObjectResponse("Success", message, "")
//                    );
//                } else {
//                    message = "Unable to update password";
//                }
//            }
//        } else {
//            message = "Passwords do not match";
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                new ObjectResponse("Fail", message, "")
//        );
//    }
//
//    @Override
//    public ResponseEntity<ObjectResponse> getUserByEmail(String email) {
//        List<Object[]> listUser = userRepository.getUserByEmail(email);
//
//        if(listUser.size() == 0) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ObjectResponse("Failed", "Invalid Information", "")
//            );
//        } else {
//
//            Object[] objsUser = listUser.get(0);
//            return ResponseEntity.status(HttpStatus.OK).body(new ObjectResponse("Success", "Get user sucessfully",
//                    new UserDTO((String) objsUser[2], (String) objsUser[0], (String) objsUser[3], (String) objsUser[1]))
//            );
//        }
//    }
//

}

