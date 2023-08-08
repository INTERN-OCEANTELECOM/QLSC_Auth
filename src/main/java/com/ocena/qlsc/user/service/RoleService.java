package com.ocena.qlsc.user.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.user.mapper.RoleMapper;
import com.ocena.qlsc.user.dto.RoleDto;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.repository.RoleRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class RoleService extends BaseServiceImpl<Role, RoleDto> implements IRoleService{

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RoleRepository roleRepository;

    @Override
    protected BaseRepository<Role> getBaseRepository() {
        return roleRepository;
    }

    @Override
    protected BaseMapper<Role, RoleDto> getBaseMapper() {
        return roleMapper;
    }

    @Override
    protected Function<String, Optional<Role>> getFindByFunction() {
        return null;
    }

    @Override
    protected Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    @Override
    protected Page<RoleDTO> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<Role> getListSearchResults(String keyword) {
        return null;
    }
}
