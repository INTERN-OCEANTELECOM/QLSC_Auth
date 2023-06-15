package com.ocena.qlsc.service;

import com.ocena.qlsc.dto.LoginRequest;
import com.ocena.qlsc.dto.ObjectResponse;
import com.ocena.qlsc.configs.Mapper.Mapper;
import com.ocena.qlsc.dto.RegisterRequest;
import com.ocena.qlsc.dto.UserResponse;
import com.ocena.qlsc.model.Role;
import com.ocena.qlsc.model.User;
import com.ocena.qlsc.repository.UserRepository;
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

    @Override
    public boolean registerUser(RegisterRequest registerRequest) {
        // Map registerRequest to User model
        User user = mapper.convertTo(registerRequest, User.class);

        if(userRepository.existsByUsername(registerRequest.getUserName()).size() > 0) {
            // User already exists in the database
            return false;
        } else {
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Set Attribute is missing
            // Set userId with uuid
            UUID uuid = UUID.randomUUID();
            user.setUserId(uuid.toString());

            // Get current Date
            Long currentTimeMillis = new Date().getTime();
            user.setCreated(currentTimeMillis);

            // Trang thai moi
            user.setStatus((short) 0);

            user.delete();

            return userRepository.save(user) != null;
        }
    }

    @Override
    public User update(String id, User user) {
        return null;
    }

    @Override
    public User getUserById(String userId) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    /**
     * {@inheritDoc}
     * Validate Request Login
     * @param loginRequest
     * @param result
     * @return ResponseEntity with type UserResponse
     */
    @Override
    public ResponseEntity<ObjectResponse> validateUser(LoginRequest loginRequest, BindingResult result) {
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
        return validateLogin(loginRequest.getUsername(),loginRequest.getPassword());
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

                if (user[5] instanceof Role) {
                    role = Collections.singletonList((Role) user[5]);
                    System.out.println("Role " + role.get(0).getRoleName());
                } else if (user[5] instanceof List<?>) {
                    role = (List<Role>) user[5];
                }

                User user1 = User.builder()
                        .fullName((String) user[0])
                        .email((String) user[1])
                        .phoneNumber((String) user[2])
                        .userName((String) user[3])
                        .password((String) user[4])
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
     * @param username
     * @param password
     * @return ResponseEntity with type UserResponse
     */
    private ResponseEntity<ObjectResponse> validateLogin(String username, String password) {
        /* Get User by Username*/
        List<Object[]> listUser = userRepository.existsByUsername(username);

        /* Check UserName and Password*/
        if (!listUser.isEmpty()) {
            Object[] userLogin = listUser.get(0);

            if (passwordEncoder.matches(password, String.valueOf(userLogin[1].toString()))){
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ObjectResponse("Success", "Login Success", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ObjectResponse("Fail", "Incorrect password", "")
                );
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ObjectResponse("Fail", "User Not Found", "")
            );
        }
    }
}

