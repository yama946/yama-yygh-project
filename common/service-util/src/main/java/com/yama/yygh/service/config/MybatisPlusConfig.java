package com.yama.yygh.service.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置文件，注入mybatis插件，分页插件以及乐观锁插件
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件拦截器，会产生limit语句进行分页
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
