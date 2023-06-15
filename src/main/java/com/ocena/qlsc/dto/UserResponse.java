package com.ocena.qlsc.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String fullName;

    private String email;

    private String phoneNumber;

    private String userName;

    private String password;

    private List<RoleResponse> roles;
}
