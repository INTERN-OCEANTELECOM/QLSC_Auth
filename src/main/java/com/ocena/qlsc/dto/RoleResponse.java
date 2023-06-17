package com.ocena.qlsc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {

    @NotBlank(message = "RoleId is required")
    private Integer roleId;

    private String roleName;
}
