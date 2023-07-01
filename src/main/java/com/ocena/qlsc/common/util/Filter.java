package com.ocena.qlsc.common.util;


import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.user.repository.RoleRepository;
import com.ocena.qlsc.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.util.List;

import com.ocena.qlsc.user.model.Role;

@Component
public class Filter extends GenericFilterBean {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        List<Role> roles = getUserRole();
        boolean isRemove = validateUser();

        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        System.out.println("Menthod là " + method);

        if(roles.isEmpty() || isRemove){
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        //User

        System.out.println("path là " + path);
        for (Role role : roles) {
            if (path.startsWith("/po-detail") && validateRequest(path,method)){
                if (!"admin".equals(role.getRoleName())) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                    return;
                }
            } else if (path.startsWith("/user")) {
                if (!"user".equals(role.getRoleName())) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private List<Role> getUserRole() {
        String email = SystemUtil.getCurrentEmail();
        return roleRepository.getRoleByEmail(email);
    }

    private Boolean validateUser(){
        String email = SystemUtil.getCurrentEmail();
        return userRepository.existsByEmailAndRemoved(email, true);
    }

    private Boolean validateRequest(String path, String menthod){
        if (path.contains(ApiResources.SEARCH_BY_KEYWORD) || menthod.equals("GET")) {
            return false;
        }
        return true;
    }

}