package com.ocena.qlsc.user.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.user.mapper.RoleMapper;
import com.ocena.qlsc.user.dto.RoleDTO;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService extends BaseServiceImpl<Role, RoleDTO> implements IRoleService{

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RoleRepository roleRepository;

    @Override
    protected BaseRepository<Role> getBaseRepository() {
        return roleRepository;
    }

    @Override
    protected BaseMapper<Role, RoleDTO> getBaseMapper() {
        return roleMapper;
    }

    @Override
    protected Page<Role> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<Role> getListSearchResults(String keyword) {
        return null;
    }

    /**
     * Retrieves a list of all roles in the system.
     * Each role includes the roleId and roleName.
     * @return A ResponseEntity containing a list of RoleResponse objects.
     */
    @Override
    public ListResponse<List<RoleDTO>> getAllRoles() {
        // Retrieve all roles from the UserRepository and convert them to RoleResponse objects
        List<RoleDTO> listRoles = roleRepository.getAllRoles()
                .stream()
                .map(objs -> {
                    return new RoleDTO(objs[0].toString(), objs[1].toString());
                }).collect(Collectors.toList());

        return ResponseMapper.toListResponseSuccess(listRoles);
    }
}
