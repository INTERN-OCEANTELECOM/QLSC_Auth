package com.ocena.qlsc.user.mapper;

import com.ocena.qlsc.common.model.BaseMapperImpl;
import com.ocena.qlsc.user.dto.RoleDto;
import com.ocena.qlsc.user.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class RoleMapper extends BaseMapperImpl<Role, RoleDto> {
    public RoleMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    protected Class<RoleDto> getDtoClass() {
        return RoleDto.class;
    }
}
