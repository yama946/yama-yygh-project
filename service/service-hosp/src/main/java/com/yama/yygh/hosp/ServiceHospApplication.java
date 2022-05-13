package com.yama.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 1、@Mapper注解：
 * 作用：在接口类上添加了@Mapper，在编译之后会生成相应的接口实现类，并被springboot自动扫描注入容器
 * 添加位置：接口类上面
 *
 * 2、@MapperScan
 * 作用：指定要变成实现类的接口所在的包，然后包下面的所有接口在编译之后都会生成相应的实现类，并注入容器
 * 添加位置：是在Springboot启动类上面添加，
 * 对于mybatis的代理接口，两者使用其一即可
 *
 *3、@ComponentScan注解作用：（扫描指定注解的类注册到IOC容器中）
 * @ComponentScan用于类或接口上主要是指定扫描路径，spring会把指定路径下带有指定注解的类注册到IOC容器中。
 * 会被自动装配的注解包括@Controller、@Service、@Component、@Repository等等。
 * 其作用等同于<context:component-scan base-package="com.maple.learn" />配置。
 * 3.1、@ComponentScan使用
 * 常用属性如下：
 * basePackages、value：指定扫描路径，如果为空则以@ComponentScan注解的类所在的包为基本的扫描路径
 * basePackageClasses：指定具体扫描的类
 * includeFilters：指定满足Filter条件的类
 * excludeFilters：指定排除Filter条件的类
 * 3.2、@ComponentScan的常见的配置如下：
 * @ComponentScan(value="com.maple.learn",
 *    excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class)},
 *    includeFilters = {@ComponentScan.Filter(type=FilterType.ANNOTATION,classes={Controller.class})})
 * public class SampleClass{}
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.yama.yygh")//用来扫描其他模块中的bean加入容器
@EnableDiscoveryClient//开启注册中心客户端用于被注册中心发现
@EnableFeignClients(basePackages = "com.yama.yygh")//开启远程调用服务,不适用
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class,args);
    }
}
