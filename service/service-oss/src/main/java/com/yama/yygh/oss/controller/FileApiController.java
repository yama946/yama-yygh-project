package com.yama.yygh.oss.controller;

import com.yama.yygh.common.result.Result;
import com.yama.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(tags = "阿里云oss文件存储")
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Autowired
    private FileService fileService;

    /**
     *     上传文件到阿里云oss
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("oss文件上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) throws IOException {
        //获取上传文件
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}

