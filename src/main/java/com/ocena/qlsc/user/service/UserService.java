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
import com.ocena.qlsc.common.constants.RoleUser;
import com.ocena.qlsc.common.constants.GlobalConstants;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.*;
import java.util.function.Function;
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
    protected Function<String, Optional<User>> getFindByFunction() {
        return userRepository::findByEmail;
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

    public boolean validateCreate(UserDTO dto) {
        // Get all roles from the database
        List<Object[]> listRoles = userRepository.getAllRoles();
        for(RoleDTO role : dto.getRoles()) {
            // Check if each roleId exists in the database
            if(!listRoles.stream().anyMatch(objs -> objs[0].equals(role.getId()))) {
                return false;
            }
        }

        if(userRepository.existsByEmail(dto.getEmail()).size() > 0) {
            return false;
        }
        // Encode the user's password
//        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        return true;
    }

    /**
     * Validates user registration data and returns a ResponseEntity object
     * containing a UserResponse object.
     * @param dto : User (DTO) receive from request
     * @return The ResponseEntity object contains the register result information
     */
    @Override
    public DataResponse<User> validateRegister(UserDTO dto) {
        List<String> result = validationRequest(dto);

        if((result != null)) {
            return ResponseMapper.toDataResponse(result, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
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
        Optional<User> isExistUser = userRepository.findByEmail(email);
        UserDTO userResponse = new UserDTO();

        // If the user exists in the database
        if(isExistUser.isPresent()) {
            User user = isExistUser.get();
            if (user.getStatus() != 2 && passwordEncoder.matches(password, user.getPassword())) {
                userResponse.setEmail(user.getEmail());
                userResponse.setStatus(user.getStatus());
                List<RoleDTO> roles = user.getRoles().stream()
                        .map(role -> roleMapper.entityToDto(role))
                        .collect(Collectors.toList());
                userResponse.setRemoved(user.getRemoved());
                userResponse.setRoles(roles);
            }
        }

        return userResponse;
    }

    /**
     * A function that handles limiting the number of failed login attempts
     * and handling login result
     * @param session HttpSession to store session information
     * @param loginRequest
     * @return The ResponseEntity object contains the login result information
     */
    public DataResponse<User> handleLoginAttempts(HttpSession session, LoginRequest loginRequest) {
        Long lockedTime;

        // Increase the number of false logins
        Integer loginAttempts = (Integer) session.getAttribute("loginAttempts");
        if(loginAttempts == null) {
            loginAttempts = 0;
        }
        loginAttempts++;

        System.out.println("Attempts: " + loginAttempts);

        session.setAttribute("loginAttempts", loginAttempts);

        if(loginAttempts >= GlobalConstants.loginAttempts) {
            // Set time out is 60s
            lockedTime = System.currentTimeMillis() / 1000 + GlobalConstants.lockTime;
            session.setAttribute("lockedTimeLogin", lockedTime);

            // Reset false login attempts to 0
            session.setAttribute("loginAttempts", 0);

            return ResponseMapper.toDataResponse(lockedTime, StatusCode.DATA_NOT_FOUND,
                    StatusMessage.LOCK_ACCESS);
        }
        // Validation Login Request
        List<String> result = validationRequest(loginRequest);

        if((result != null)) {
            return ResponseMapper.toDataResponse(result, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
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

        // Check if the account is temporarily locked
        Long lockedTime = (Long) session.getAttribute("lockedTimeLogin");

        if (lockedTime != null) {
            return ResponseMapper.toDataResponse(lockedTime, StatusCode.DATA_NOT_FOUND,
                    StatusMessage.LOCK_ACCESS);
        }
        // Authenticate the email and password
        if (isAuthenticate(loginRequest.getEmail(), loginRequest.getPassword()).getEmail() != null) {
            if (session.getAttribute("loginAttempts") != null) {
                session.setAttribute("loginAttempts", 0);
            }
            return ResponseMapper.toDataResponseSuccess(isAuthenticate(loginRequest.getEmail(),
                    loginRequest.getPassword()));
        } else {
            // Handle login attempts for wrong login
            return handleLoginAttempts(session, loginRequest);
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
                        (lockedTimeOTP - (System.currentTimeMillis() / 1000)) + " seconds and try again");
            }
            session.setAttribute("lockedTimeOTP", 0L);
        }
        // GenerateOTP and sent mail OTP
        String message = otpService.generateOtp(email);

        if (message.equals("OTP Has Been Sent!!!")){
            // Set time out is 60s
            lockedTimeOTP = System.currentTimeMillis() / 1000 + GlobalConstants.lockTime;
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
    public DataResponse<User> validateOTP(String email, Integer OTP, String newPassword) {
        String message = "An error occurred while validating OTP";

        Optional<User> isExistUser = userRepository.findByEmail(email);
        if (isExistUser.isPresent()) {
            User user = isExistUser.get();
            if (otpService.validateOTP(email, OTP)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setStatus((short) 1);
                if (userRepository.save(user) != null) {
                    return ResponseMapper.toDataResponseSuccess(StatusMessage.REQUEST_SUCCESS);
                }
            }
        } else {
            message = "Email is not correct";
        }

        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, message);
    }

    public boolean validateDeleteUser(String emailUser, String emailModifier) {
        List<Role> listRoles = userRepository.getRoleByEmail(emailModifier);

        boolean isAdmin = listRoles.stream().anyMatch(role -> role.getRoleName().equals(RoleUser.ADMIN.toString()));
        System.out.println(isAdmin);

        if (isAdmin && !emailModifier.equals(emailUser)){
            return true;
        }

        return false;
    }

    @Override
    public Boolean validateUpdateUser(String emailUser, UserDTO userDTO) {
        // Validate Request
        List<String> result = validationRequest(userDTO);

        if((result != null)) {
            return null;
        }


        List<User> listUser = userRepository.findAll();

        User user = listUser.stream()
                .filter(users -> users.getEmail().equals(emailUser))
                .findFirst()
                .orElse(null);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String email = request.getHeader("email");
        boolean isUpdatedAdmin = listUser.stream()
                .filter(users -> users.getEmail().equals(email))
                .findFirst()
                .map(User::getRoles)
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(role -> role.getRoleName().equals(RoleUser.ADMIN.toString()));

        if (user != null) {
            if (isUpdatedAdmin) {
                return true;
            }
            return false;
        }

        return null;
    }

    @Override
    public DataResponse<User> resetPassword(String email, String oldPassword, String newPassword) {
        Optional<User> isExistUser = userRepository.findByEmail(email);

        if(isExistUser.isPresent() && passwordEncoder.matches(oldPassword, isExistUser.get().getPassword())) {
            User user = isExistUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setStatus((short) 1);
            if(userRepository.save(user) != null) {
                return ResponseMapper.toDataResponseSuccess("");
            }
        }

        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
    }
}

