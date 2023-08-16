package com.ocena.qlsc.common.config;

import com.ocena.qlsc.podetail.dto.PoDetailRequest;
import com.ocena.qlsc.podetail.model.PoDetail;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
@EnableCaching
@OpenAPIDefinition
public class AppConfig {
    @Autowired
    private FilterConfig filter;

    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper mapper = new ModelMapper();


        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setPropertyCondition(context -> {
                    if (context.getSource() == null
                            && (context.getDestinationType().equals(Long.class))) {
                        return true; // skip map
                    }
                    if(context.getSource() == null) {
                        return false;
                    }
                    return true;
                });
        return mapper;
    }

    @Bean
    public FilterRegistrationBean<FilterConfig> filterRegistrationBean() {
        FilterRegistrationBean<FilterConfig> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}

