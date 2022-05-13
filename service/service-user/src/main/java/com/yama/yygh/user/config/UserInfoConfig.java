package com.yama.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 以配置文件的形式，将mapper代理类放到容器中
 */
@Configuration
@MapperScan(basePackages = "com.yama.yygh.user.mapper")
//MapperScan注解会将接口认为是mybatis的mapper进而生成代理，如果扫描的包service也会被代理成mapper
//变成操作数据库的代理类，而不是spring生成的代理类操作服务层，会导致service注入使用失败
public class UserInfoConfig {

}
