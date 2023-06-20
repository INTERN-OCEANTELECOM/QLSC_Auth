package com.ocena.qlsc.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO {

    @NotBlank(message = "RoleId is required")
    private String id;

    private String roleName;
}
