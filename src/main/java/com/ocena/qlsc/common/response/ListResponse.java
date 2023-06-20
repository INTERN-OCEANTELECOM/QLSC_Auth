package com.ocena.qlsc.common.response;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse<T> {
    private Long timestamp;
    private int statusCode;
    private String statusMessage;
    private long totalRecords;
    private int totalPages;
    private List<T> data;
}
