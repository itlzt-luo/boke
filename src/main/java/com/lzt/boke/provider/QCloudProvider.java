package com.lzt.boke.provider;

import com.lzt.boke.exception.CustomizeErrorCode;
import com.lzt.boke.exception.CustomizeException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class QCloudProvider {
    @Value("${qcloud.secretId}")
    private String secretId;

    @Value("${qcloud.secretKey}")
    private String secretKey;

    @Value("${qcloud.bucket}")
    private String bucket;

    @Value("${qcloud.bucketName}")
    private String bucketName;

    @Value("${qcloud.path}")
    private String path;

    public String upload(InputStream fileStream,Long size, String contentType, String fileName) {

        String generatedFileName;
        String[] filePaths = fileName.split("\\.");
        if (filePaths.length > 1) {
            generatedFileName = UUID.randomUUID().toString() + "." + filePaths[filePaths.length - 1];
        } else {
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(bucket);
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        // 指定要上传到的存储桶
        String bucketName = this.bucketName;
        // 指定要上传到 COS 上对象键
        String key = "/images/"+ generatedFileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);//文件的大小
        objectMetadata.setContentType(contentType);//文件的类型

        String url = null;
        try {

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, fileStream, objectMetadata);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            url = this.path + putObjectRequest.getKey();
        } catch(CosServiceException e){
            log.error("upload error ,{}",fileName,e);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (CosClientException e) {
            log.error("upload error ,{}",fileName,e);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }finally {
            // 关闭客户端
            cosClient.shutdown();
            //返回文件的网络访问url
            return url;
        }
    }
 }

