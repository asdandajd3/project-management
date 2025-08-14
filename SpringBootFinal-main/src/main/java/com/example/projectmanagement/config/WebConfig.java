package com.example.projectmanagement.config;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối của thư mục lưu ảnh
        Path uploadDir = Paths.get("uploads/avatar");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Mapping URL "/uploads/avatar/**" đến thư mục hệ thống
        registry.addResourceHandler("/uploads/avatar/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
    @Bean
    public FilterRegistrationBean<Filter> characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*"); // Áp dụng cho mọi URL
        registrationBean.setOrder(1); // Ưu tiên cao

        return registrationBean;
    }
}