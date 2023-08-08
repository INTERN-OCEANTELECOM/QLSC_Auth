package com.ocena.qlsc.user.controller;

import com.ocena.qlsc.common.annotation.ApiShow;
import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.RoleDto;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/role")
//@CrossOrigin(value = "*")
public class RoleController extends BaseApiImpl<Role, RoleDto> {
    @Autowired
    RoleService roleService;

    @Override
    protected BaseService<Role, RoleDto> getBaseService() {
        return roleService;
    }

    @Override
    @ApiShow
    public ListResponse<RoleDto> getAll() {
        return super.getAll();
    }

    /* User for Swagger*/

    @Override
    @ApiShow
    public DataResponse<RoleDto> add(RoleDto objectDTO) {
        return super.add(objectDTO);
    }
}
