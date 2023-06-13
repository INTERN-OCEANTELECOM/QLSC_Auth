package com.ocena.qlsc.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {

    private String status;

    private String message;

    Object data;
}
