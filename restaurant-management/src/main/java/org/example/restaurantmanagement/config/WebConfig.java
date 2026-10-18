package org.example.restaurantmanagement.config;

import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(jwtInterceptor)

                .addPathPatterns("/api/**")

                .excludePathPatterns(
                        // 管理员登录
                        "/api/login",

                        // 顾客登录
                        "/api/customer/login",

                        // 顾客注册
                        "/api/customer/register",

                        // 支付宝异步通知（支付宝服务器调用，无登录态）
                        "/api/pay/alipay/notify"
                );
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(
                        "file:"
                                + System.getProperty("user.dir")
                                + File.separator
                                + "upload"
                                + File.separator
                );
    }
}