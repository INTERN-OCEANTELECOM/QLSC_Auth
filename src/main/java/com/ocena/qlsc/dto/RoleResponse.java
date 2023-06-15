package com.ocena.qlsc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {

    private Integer roleId;

    private String roleName;
}
