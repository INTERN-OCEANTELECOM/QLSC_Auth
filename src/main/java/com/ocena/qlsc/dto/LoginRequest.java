package com.ocena.qlsc.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class LoginRequest {

    /* Validate username
       not required
       length must larger than 8 character
    */
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    /* Validate password
       length must larger than 8 character
       must have a-z character
       must have A-Z character
       does not contain spaces */
    @NotBlank(message = "Password is required")
    @Size(min = 8,max = 15, message = "Password must have between 6 and 15 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and no whitespace")
    private String password;
}
