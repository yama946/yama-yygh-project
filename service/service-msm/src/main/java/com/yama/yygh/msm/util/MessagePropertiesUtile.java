package com.yama.yygh.msm.util;

import com.yama.yygh.common.exception.YyghException;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 获取个人数据的工具类
 */
@Component
//@PropertySource("classpath:messageconfig.properties")
@Getter
public class MessagePropertiesUtile {

    @Value("${ApiHost}")
    private  String apiHost;

    @Value("${AppCode}")
    private  String appCode;

    @Value("${AppKey}")
    private  String appKey;

    @Value("${AppSecret}")
    private  String appSecret;



    /*public static Map<String,String> getInfoMap() {
        InputStream fileInput=null;
        try {
            Map<String,String> infoMap = new HashMap<>();
            Properties prop = new Properties();
            fileInput = MessagePropertiesUtile.class.getResource("/messageconfig.properties").openStream();
            prop.load(fileInput);
            Set<String> propertyNames = prop.stringPropertyNames();

            for (String name:propertyNames){
                String value = prop.getProperty(name);
                infoMap.put(name,value);
            }
            return infoMap;
        } catch (IOException e) {
            throw new YyghException("个人信息配置文件读取失败");
        }finally {
            try {
                fileInput.close();
            } catch (IOException e) {
                throw new YyghException("个人信息配置文件输入流关闭失败");
            }
        }
    }*/
}
