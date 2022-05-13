package com.yama.yygh.oss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.yama.yygh.oss.service.FileService;
import com.yama.yygh.oss.util.ConstantOssPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private ConstantOssPropertiesUtil constantOssPropertiesUtil;

    /**
     * 上传文件到oss对象中，并返回路径，供前端进行引用
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) throws IOException {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint ="https://"+constantOssPropertiesUtil.getEndPoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = constantOssPropertiesUtil.getAccessKeyId();
        String accessKeySecret = constantOssPropertiesUtil.getAccessKeySecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = constantOssPropertiesUtil.getBucketName();
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String fileName = file.getOriginalFilename();
        //使用UUID生成一个随机数进行命令
        String fileRandom= UUID.randomUUID().toString().replaceAll("-","");
        fileName = fileRandom+fileName;
        //上传文件到bucket空间中如果key值中包含/会认为是文件分割符形成文件分级，我们使用日期来进行分级
        String fileDate= new DateTime().toString("yyyy/MM/dd");
        fileName = fileDate + "/" + fileName;
        //指定上传文件的流
        InputStream fileInputStream= file.getInputStream();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        if (!ossClient.doesBucketExist(bucketName)){
            //创建bucket空间
            ossClient.createBucket(bucketName);
        }

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, fileInputStream);
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
             ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
             metadata.setObjectAcl(CannedAccessControlList.PublicRead);
             putObjectRequest.setMetadata(metadata);
            // 上传文件。
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        //当oss对象存储是公共读的，访问连接时：https://safdsf.oss-cn-beijing.aliyuncs.com/wife.jpg
        //如果仓库时私有的，请求需要添加key，secret等参数
        String fileUrl="https://"+bucketName+"."+constantOssPropertiesUtil.getEndPoint()+"/"+fileName;
        return fileUrl;
    }
}
