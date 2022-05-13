package com.yama.yygh.oss.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置参数
 */
@Getter
@Component
public class ConstantOssPropertiesUtil {
    @Value("${EndPoint}")
    private String endPoint;
    @Value("${AccessKeyId}")
    private String accessKeyId;
    @Value("${AccessKeySecret}")
    private String accessKeySecret;
    @Value("${BucketName}")
    private String bucketName;
}
