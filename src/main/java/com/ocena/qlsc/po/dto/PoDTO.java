package com.ocena.qlsc.po.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PoDTO {
    private String id;
    private Integer orderQuantity;

    private Long beginAt;

    private Long endAt;
}
