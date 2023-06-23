package com.ocena.qlsc.user.configs;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class UserConfig {
    @Bean
    public LocalValidatorFactoryBean validatorFactory() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public CustomSessionListener customSessionListener() {
//        return new CustomSessionListener();
//    }
//
//    @Bean
//    public ServletListenerRegistrationBean<CustomSessionListener> customSessionListenerRegistration() {
//        ServletListenerRegistrationBean<CustomSessionListener> registrationBean =
//                new ServletListenerRegistrationBean<>();
//        registrationBean.setListener(customSessionListener());
//        return registrationBean;
//    }
}
