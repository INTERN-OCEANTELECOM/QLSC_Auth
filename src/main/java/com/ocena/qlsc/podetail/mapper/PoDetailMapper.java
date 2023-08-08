package com.ocena.qlsc.podetail.mapper;

import com.ocena.qlsc.common.model.BaseMapperImpl;
import com.ocena.qlsc.podetail.dto.PoDetailDto;
import com.ocena.qlsc.podetail.model.PoDetail;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PoDetailMapper extends BaseMapperImpl<PoDetail, PoDetailDto> {
    public PoDetailMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected Class<PoDetail> getEntityClass() {
        return PoDetail.class;
    }

    @Override
    protected Class<PoDetailDto> getDtoClass() {
        return PoDetailDto.class;
    }
}
