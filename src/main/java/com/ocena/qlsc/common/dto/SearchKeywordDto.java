package com.ocena.qlsc.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchKeywordDto {
    private String keyword;

    @Builder.Default
    private String property = "ALL";

    private int pageIndex;

    private int pageSize;
}
