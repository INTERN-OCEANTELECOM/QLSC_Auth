package com.ocena.qlsc.user.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.user.configs.session.SessionTimeOut;
import com.ocena.qlsc.user.mapper.RoleMapper;
import com.ocena.qlsc.user.mapper.UserMapper;
import com.ocena.qlsc.user.dto.LoginRequest;
import com.ocena.qlsc.user.dto.RoleDTO;
import com.ocena.qlsc.user.dto.UserDTO;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.model.User;
import com.ocena.qlsc.user.repository.UserRepository;
import com.ocena.qlsc.user.configs.mail.OTPService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    RoleMapper roleMapper;
    @Autowired
    private LocalValidatorFactoryBean validator;
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

    /**
     * Validates user registration data and returns a ResponseEntity object
     * containing a UserResponse object.
     * @param dto : User (DTO) receive from request
     * @return The ResponseEntity object contains the register result information
     */
    @Override
    public DataResponse<User> validateRegister(UserDTO dto) {
        // The BindingResult object that holds the result of the data validation process.
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
    @Override
    public ListResponse<UserDTO> getAllUser() {
        // Get all user data from the database
        List<UserDTO> listUser = userRepository.getAllUser().stream()
                .map(user -> {
                    List<Role> roles = new ArrayList<>();
                    if (user[4] instanceof Role) {
                        roles = Collections.singletonList((Role) user[4]);
                    } else if (user[4] instanceof List<?>) {
                        roles = (List<Role>) user[4];
                    }

                    List<RoleDTO> roleDTOS = roles.stream()
                            .map(role -> roleMapper.entityToDto(role))
                            .collect(Collectors.toList());

                    return UserDTO.builder()
                            .fullName((String) user[0])
                            .email((String) user[1])
                            .phoneNumber((String) user[2])
                            .status((Short) user[3])
                            .roles(roleDTOS)
                            .build();

                })
                .collect(Collectors.toList());

        return ResponseMapper.toListResponseSuccess(listUser);
    }

    /**
     * Authenticates the user by checking if the provided email and password match an existing user in the database.
     * @param email
     * @param password
     * @return A UserResponse object containing the authenticated user's information,
     * or an empty UserResponse object if authentication fails.
     */
    private UserDTO isAuthenticate(String email, String password) {
        // Check if the user exists in the database based on the email
        User user = userRepository.findByEmail(email);
        UserDTO userResponse = new UserDTO();

        // If the user exists in the database
        if(user != null) {
            if (user.getStatus() != 2 && passwordEncoder.matches(password, user.getPassword())) {
                userResponse.setEmail(user.getEmail());
                userResponse.setStatus(user.getStatus());
                List<RoleDTO> roles = user.getRoles().stream()
                        .map(role -> roleMapper.entityToDto(role))
                        .collect(Collectors.toList());
                userResponse.setRoles(roles);
            }
        }

        return userResponse;
    }

    /**
     * A function that handles limiting the number of failed login attempts
     * and handling login result
     * @param email
     * @param session HttpSession to store session information
     * @param result BidingResult object to check for errors when validating input data
     * @return The ResponseEntity object contains the login result information
     */
    public DataResponse<User> handleLoginAttempts(String email, HttpSession session, Errors result) {
        Long lockedTime;

        // Increase the number of false logins
        Integer loginAttempts = (Integer) session.getAttribute("loginAttempts");
        if(loginAttempts == null) {
            loginAttempts = 0;
        }
        loginAttempts++;

        System.out.println("Attempts: " + loginAttempts);

        session.setAttribute("loginAttempts", loginAttempts);
        // Delete session 30 seconds after session was created
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.schedule(() -> {
//            session.removeAttribute("loginAttempts");
//        }, 60, TimeUnit.SECONDS);

        if(loginAttempts >= SessionTimeOut.loginAttempts) {
            // Set time out is 60s
            lockedTime = System.currentTimeMillis() / 1000 + SessionTimeOut.lockTime + 10;
            session.setAttribute("lockedTimeLogin", lockedTime);

            // Reset false login attempts to 0
            session.setAttribute("loginAttempts", 0);

            return ResponseMapper.toDataResponse(lockedTime, StatusCode.DATA_NOT_FOUND,
                    StatusMessage.LOCK_ACCESS);
        }

        if((result.hasErrors())) {
            // Get list of error messages from BindingResult
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseMapper.toDataResponse(errorMessages.get(0), StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);

        }

        return ResponseMapper.toDataResponse("", StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }

    /**
     * Validates the login credentials provided by the user.
     * @return A ResponseEntity containing the validation result and response object.
     */
    @Override
    public DataResponse<User> validateLogin(LoginRequest loginRequest, HttpServletRequest request) {
        HttpSession session = request.getSession();

        // The BindingResult object to check for input validation errors.
        Errors result = new BeanPropertyBindingResult(loginRequest, "loginRequest");
        validator.validate(loginRequest, result);

        // Check if the account is temporarily locked
        Long lockedTime = (Long) session.getAttribute("lockedTimeLogin");
        if(lockedTime != null)  {
            return ResponseMapper.toDataResponse(lockedTime, StatusCode.DATA_NOT_FOUND,
                    StatusMessage.LOCK_ACCESS);
        }

        // Authenticate the email and password
        if(isAuthenticate(loginRequest.getEmail(), loginRequest.getPassword()).getEmail() != null) {
            if(session.getAttribute("loginAttempts") != null) {
                session.setAttribute("loginAttempts", 0);
            }
            return ResponseMapper.toDataResponseSuccess(isAuthenticate(loginRequest.getEmail(),
                    loginRequest.getPassword()));
        }
        else {
            // Handle login attempts for wrong login
            return handleLoginAttempts(loginRequest.getEmail(), session, result);
        }
    }

    /**
     * Sent Email OTP
     * @param email: User's email
     * @param request: Request to be blocked when sending OTP.
     * @return ResponseEntity UserResponse
     */
    @Override
    public DataResponse<User> sentOTP(String email, HttpServletRequest request) {
        HttpSession session = request.getSession();

        // Check if locked sent OTP
        Long lockedTimeOTP = (Long) session.getAttribute("lockedTimeOTP");

        if(lockedTimeOTP != null)  {
            if(System.currentTimeMillis() / 1000 < lockedTimeOTP) {

                return ResponseMapper.toDataResponse(lockedTimeOTP, StatusCode.LOCK_ACCESS, "Please wait for "+
                        (lockedTimeOTP - (System.currentTimeMillis() / 1000))+" seconds and try again");
            }
            session.setAttribute("lockedTimeOTP", 0L);
        }

        // GenerateOTP and sent mail OTP
        String message = otpService.generateOtp(email);

        if (message.equals("OTP Has Been Sent!!!")){
            // Set time out is 60s
            lockedTimeOTP = System.currentTimeMillis() / 1000 + 60;
            session.setAttribute("lockedTimeOTP", lockedTimeOTP);

            return ResponseMapper.toDataResponseSuccess(message);
        }

        return ResponseMapper.toDataResponse(message, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }

    /**
     * Validate OTP When User ResetPassword
     *
     * @param email: User's email
     * @param OTP: The OTP received by the user in the email
     * @return ResponseEntity UserResponse
     */
    @Override
    public DataResponse<User> validateOTP(String email, Integer OTP, String newPassword, String rePassword) {
        String message = "Something Went Wrong!!";

        if (newPassword.equals(rePassword)){
            message = otpService.validateOTP(email, OTP);

            if (message.equals("GET OTP Success!!!")){
                //Get time forgot password
                Long currentTimeMillis = new Date().getTime();

                //Hash Password
                String passwordHash = passwordEncoder.encode(newPassword);

                int update = userRepository.forgotPassword(passwordHash,1, email, currentTimeMillis, email);

                if (update != 0) {
                    return ResponseMapper.toDataResponseSuccess(message);
                } else {
                    message = "Unable to update password";
                }
            }
        } else {
            message = "Passwords do not match";
        }
        return ResponseMapper.toDataResponse(message, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
    }
    @Override
    public DataResponse<User> deleteUser(String emailUser, String emailModifier) {
        List<Role> listRoles = userRepository.getRoleByEmail(emailModifier);

        boolean isAdmin = listRoles.stream().anyMatch(role -> role.getId().equals("1"));

        if (isAdmin && !emailModifier.equals(emailUser)){

            User user = userRepository.findByEmail(emailUser);
            user.setStatus((short) 2);
            user.setRemoved(true);

            if (userRepository.save(user) != null) {
                return ResponseMapper.toDataResponseSuccess(StatusMessage.REQUEST_SUCCESS);
            }
        }

        return ResponseMapper.toDataResponse("", StatusCode.NOT_IMPLEMENTED, StatusMessage.NOT_IMPLEMENTED);
    }

    @Override
    public DataResponse<User> getUserByEmail(String email) {
        List<Object[]> listUser = userRepository.getUserByEmail(email);

        if(listUser.size() == 0) {
            return ResponseMapper.toDataResponse("", StatusCode.NOT_IMPLEMENTED, StatusMessage.NOT_IMPLEMENTED);
        } else {
            Object[] objsUser = listUser.get(0);

            UserDTO userDTO = new UserDTO((String) objsUser[2], (String) objsUser[0], (String) objsUser[3], (String) objsUser[1]);

            return ResponseMapper.toDataResponseSuccess(userDTO);
        }
    }

    @Override
    public DataResponse<User> updateUser(String emailUser, UserDTO userDTO) {
        Errors result = new BeanPropertyBindingResult(userDTO, "userDTO");
        validator.validate(userDTO, result);
        if((result.hasErrors())) {
            // Get list of error messages from BindingResult
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseMapper.toDataResponse(errorMessages.get(0).toString(), StatusCode.DATA_NOT_MAP,
                    StatusMessage.DATA_NOT_MAP);
        }
        try {
//            List<Role> listRoles = userRepository.getRoleByEmail(emailModifier);
            User user = userRepository.findByEmail(emailUser);

            if (user != null) {
                User userRequest = userMapper.dtoToEntity(userDTO);

                user.setEmail(userRequest.getEmail());
                user.setPhoneNumber(userRequest.getPhoneNumber());
                user.setFullName(userRequest.getFullName());
                user.setRoles(userRequest.getRoles());
            }
            userRepository.save(user);
            return ResponseMapper.toDataResponseSuccess("");
        } catch (Exception ex) {
            System.out.println("Error" + ex);

            return ResponseMapper.toDataResponse(null, StatusCode.NOT_IMPLEMENTED, StatusMessage.NOT_IMPLEMENTED);
        }
    }
}

