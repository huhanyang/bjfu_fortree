package com.bjfu.fortree.config;

import com.bjfu.fortree.security.interceptor.RateLimiterInterceptor;
import com.bjfu.fortree.security.interceptor.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * spring mvc配置类
 * @author warthog
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimiterInterceptor());
        registry.addInterceptor(securityInterceptor);
    }

}
