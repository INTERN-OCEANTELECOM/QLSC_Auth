package com.ocena.qlsc.user.Mapper;

import com.ocena.qlsc.common.model.BaseMapperImpl;
import com.ocena.qlsc.user.dto.RoleDTO;
import com.ocena.qlsc.user.dto.UserDTO;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class RoleMapper extends BaseMapperImpl<Role, RoleDTO> {
    public RoleMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    protected Class<RoleDTO> getDtoClass() {
        return RoleDTO.class;
    }
}
