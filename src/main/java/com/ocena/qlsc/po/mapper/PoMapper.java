package com.ocena.qlsc.po.mapper;

import com.ocena.qlsc.common.model.BaseMapperImpl;
import com.ocena.qlsc.po.dto.PoDto;
import com.ocena.qlsc.po.model.Po;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PoMapper extends BaseMapperImpl<Po, PoDto> {
    public PoMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected Class<Po> getEntityClass() {
        return Po.class;
    }

    @Override
    protected Class<PoDto> getDtoClass() {
        return PoDto.class;
    }
}
