package com.ocena.qlsc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ObjectResponse {

    private String status;

    private String message;

    private Object data;
}
