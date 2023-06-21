package com.ocena.qlsc.user.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user.dto.RoleDTO;
import com.ocena.qlsc.user.dto.UserDTO;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.model.User;
import com.ocena.qlsc.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/role")
@CrossOrigin(origins = "*")
public class RoleController extends BaseApiImpl<Role, RoleDTO> {
    @Autowired
    RoleService roleService;

    @Override
    protected BaseService<Role, RoleDTO> getBaseService() {
        return roleService;
    }

    @GetMapping("/get-roles")
    public ListResponse<List<RoleDTO>> getRoles() {
        return roleService.getAllRoles();
    }
}
