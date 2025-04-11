//package com.example.yz.server.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**") // 匹配所有/api开头的路径
//                .allowedOrigins("http://localhost:5173") // 允许的来源
//                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的HTTP方法
//                .allowedHeaders("*") // 允许的请求头
//                .allowCredentials(true); // 允许携带凭证（如cookies）
//    }
//}