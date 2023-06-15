package com.ocena.qlsc.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {

    private String fullName;

    private String email;

    private String phoneNumber;

    private String userName;

    private String password;

    private List<RoleResponse> roles;
}
