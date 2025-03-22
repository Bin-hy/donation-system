package com.example.config;

import com.example.interceptor.JwtTokenAdminInterceptor;
import com.example.interceptor.JwtTokenUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");

        // 用户拦截器
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/verifyCode", // 获取验证码
                        "/user/news/recommend",
                        "/user/projects/recommend",
                        "/user/register",
                        "/user/news/detail",
                        "/user/info-disclosure/list",
                        "/user/carousel/list", // 排除轮播图接口
                        "/websocket/**",
                        "/files/upload" // 排除文件上传接口
                );

        // 管理员拦截器
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/info",
                        "/websocket/**",
                        "/files/upload" // 排除文件上传接口
                );
    }
}
