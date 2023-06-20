package com.ocena.qlsc.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String fullName;

    // must have email format using @Email
    @Email
    @NotBlank(message = "Email is required")
    private String email;

    private String phoneNumber;

    /* Validate password
       length must larger than 8 character
       must have a-z character
       must have A-Z character
       does not contain spaces */
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^\\S*$", message = "Password must no whitespace")
    @NotBlank(message = "Password is required")
    private String password;
    private Short status;

    private List<RoleDTO> roles;

    public UserDTO(String fullName, String email, String phoneNumber, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
