package com.common.Config;


import com.common.interceptor.IPAccessInterceptor;
import com.common.interceptor.LoginInterceptor;
import com.common.interceptor.RateLimitInterceptor;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 身份验证忽略的请求接口
     */
    public final static String[] loginIgnore = {"/user/login", "/user/register","/email/emailVerification"};


    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    @Lazy
    private RateLimitInterceptor rateLimitInterceptor;

    @Autowired
    private IPAccessInterceptor ipAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        过滤单个ip速率
        registry.addInterceptor(ipAccessInterceptor).addPathPatterns("/**");
//        过滤总速率
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**");
//        过滤非法请求
        registry.addInterceptor(loginInterceptor).excludePathPatterns(loginIgnore);
    }

    @Value("${rateLimiter.count}")
    private int rateCount;

    @Value("${rateLimiter.duration}")
    private String rateDuration;

    // 明确指定 RateLimiterRegistry 的配置
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(rateCount)
                .limitRefreshPeriod(Duration.parse(rateDuration))
                .timeoutDuration(Duration.ZERO)
                .build();

        return RateLimiterRegistry.of(config);
    }

    // 创建 RateLimiter 实例
    @Bean
    @Lazy
    public RateLimiter rateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        return rateLimiterRegistry.rateLimiter("global");
    }


}
