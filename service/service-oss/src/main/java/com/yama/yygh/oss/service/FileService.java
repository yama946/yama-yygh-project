package com.yama.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    //上传文件到阿里云oss,将连接路径返回给前端，进行引用显示
    String upload(MultipartFile file) throws IOException;

}
