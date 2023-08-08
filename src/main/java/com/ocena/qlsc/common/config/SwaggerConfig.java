package com.ocena.qlsc.common.config;

import com.ocena.qlsc.common.annotation.ApiShow;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getMethodAnnotation(ApiShow.class) == null) {
                // Ẩn hoàn toàn các phương thức không có chú thích @ApiShow
                return null;
            }
            return operation;
        };
    }
}
