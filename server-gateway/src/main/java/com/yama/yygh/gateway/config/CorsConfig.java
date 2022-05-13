package com.yama.yygh.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 目前我们已经在网关做了跨域处理，那么service服务就不需要再做跨域处理了
 * 将之前在controller类上添加过@CrossOrigin标签的去掉，防止程序异常
 */
@Configuration
public class CorsConfig {
    /**
     * 以过滤器的方式实现跨域
     * 向容器中注入一个符合要求的CorsWebFilter实现跨域
     * @return
     */
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //设置允许的http头信息
        config.addAllowedMethod("*");
        //允许的域
        config.addAllowedOrigin("*");
        //请求方式
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        //配置映射路径
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}

