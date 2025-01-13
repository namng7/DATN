package com.namng7.datn_v1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .simpleDateFormat("dd-MM-yyyy HH:mm:ss")
                .timeZone("Asia/Ho_Chi_Minh")
                .build();
    }
}
