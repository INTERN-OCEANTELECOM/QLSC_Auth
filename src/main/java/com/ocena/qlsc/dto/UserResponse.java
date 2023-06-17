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

    private String password;

    private Short status;

    private List<RoleResponse> roles;
}
