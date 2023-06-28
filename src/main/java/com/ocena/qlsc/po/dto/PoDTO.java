package com.ocena.qlsc.po.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PoDTO {
    @NotBlank(message = "poNumber must not be blank")
    private String poNumber;

//    @NotNull(message = "quantity must not be null")
//    @Min(value = 1, message = "quantity must be greater than 0")
    private Integer quantity;

//    @NotNull(message = "beginAt must not be null")
//    @Min(value = 1, message = "beginAt must be greater than 0")
    private Long beginAt;

//    @NotNull(message = "endAt must not be null")
//    @Min(value = 1, message = "endAt must be greater than 0")
    private Long endAt;


    public PoDTO(String poNumber) {
        this.poNumber = poNumber;
    }
}
