package com.ocena.qlsc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String fullName;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must be in correct format")
    private String phoneNumber;

    private String password;

    private Short status;

    private List<RoleResponse> roles;
}
