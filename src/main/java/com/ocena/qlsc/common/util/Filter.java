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

        if (!validateRequest(path, method , roles)){
            chain.doFilter(request, response);
            return;
        }

        if(roles.isEmpty() || isRemove){
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        for (Role role : roles) {
            if (path.equals("/user/delete")
                    || path.equals("/user/add")
                    || path.equals("/user/get-all")
                    || path.equals("/role/get-all")){
                if (!"ROLE_ADMIN".equals(role.getRoleName())) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                    return;
                }
            } else if (path.startsWith("/user")
                    || path.startsWith("/po-detail")
                    || path.startsWith("/po")
                    || path.startsWith("/product") ) {
                if ("ROLE_USER".equals(role.getRoleName())) {
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

    private Boolean validateRequest(String path, String method, List<Role> roles){
        if (path.contains("/forgot-password/sent-otp")
                || path.contains("/forgot-password/verify")
                || path.equals("/user/login")){
            return false;
        }

        if ((!roles.isEmpty()) && !validateUser()){
            if (method.equals("GET")
                || path.contains(ApiResources.SEARCH_BY_KEYWORD)
                || path.contains("/user/reset-password")){
                    if (path.equals("/user/get-all") || path.equals("/role/get-all")) {
                        return true;
                    }
                    return false;
                }
        }
        return true;
    }
}