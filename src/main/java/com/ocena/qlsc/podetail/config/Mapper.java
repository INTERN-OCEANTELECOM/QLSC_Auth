package com.ocena.qlsc.podetail.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Mapper {
    private final ModelMapper modelMapper;
    public <T, U> U convertTo(T source, Class<U> targetClass) {
        return modelMapper.map(source, targetClass);
    }
}
