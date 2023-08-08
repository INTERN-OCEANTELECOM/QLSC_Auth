package com.ocena.qlsc.user.mapper;

import com.ocena.qlsc.common.model.BaseMapperImpl;
import com.ocena.qlsc.user.dto.UserDto;
import com.ocena.qlsc.user.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserMapper extends BaseMapperImpl<User, UserDto> {
    public UserMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected Class<UserDto> getDtoClass() {
        return UserDto.class;
    }
}