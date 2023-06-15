package com.ocena.qlsc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoleResponse {
    private Integer roleId;

    private String roleName;
}
