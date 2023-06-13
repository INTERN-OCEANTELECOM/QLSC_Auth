package com.ocena.qlsc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ocena.qlsc.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
public class RegisterRequest {

    /*  Validate username
        length must larger than 6 character
        must have a-z and A-Z character */
    @Size(min = 6)
    private String userName;

    /* Validate password
       length must larger than 8 character
       must have a-z character
       must have A-Z character
       does not contain spaces */
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).*$")
    private String password;

    // must have email format using @Email
    @Email
    private String email;

    private String fullName;

    @Pattern(regexp = "^0(1\\d{9}|9\\d{8})$")
    private String phoneNumber;

    private String creator;

    private List<Role> roles;
}
