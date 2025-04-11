package com.example.yz.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {
    @Bean
    public FileStorageProperties fileStorageProperties() {
        return new FileStorageProperties();
    }
}
