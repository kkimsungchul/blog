package com.sungchul.blog.config;

import com.sungchul.blog.interceptor.VisitorCounterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final VisitorCounterInterceptor visitorCounterInterceptor;

    @Autowired
    public WebMvcConfig(VisitorCounterInterceptor visitorCounterInterceptor) {
        this.visitorCounterInterceptor = visitorCounterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitorCounterInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico");
    }
}