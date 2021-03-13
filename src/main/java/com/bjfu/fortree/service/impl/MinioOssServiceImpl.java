package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.OssException;
import com.bjfu.fortree.service.OssService;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * 基于开源的minio的oss实现类
 * @author warthog
 */
@Service
@Slf4j
public class MinioOssServiceImpl implements OssService {

    /**
     * 默认上传文件URL过期时间(秒) 一天
     */
    public static final Integer DEFAULT_PUT_OBJECT_EXPIRES = 60 * 60 * 24;
    /**
     * 默认下载文件URL过期时间(秒) 7天
     */
    public static final Integer DEFAULT_GET_OBJECT_EXPIRES = 60 * 60 * 24 * 7;

    @Autowired
    private MinioClient minioClient;

    @Override
    public void putObject(String bucketName, String objectName, InputStream stream) {
        try{
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(stream, stream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception exception) {
            log.error("文件上传失败：", exception);
            throw new OssException(ResultEnum.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception exception) {
            log.error("文件下载失败：", exception);
            throw new OssException(ResultEnum.FILE_DOWNLOAD_FAILED);
        }
    }

    @Override
    public String preSignedPutObject(String bucketName, String objectName) {
        return preSignedPutObject(bucketName, objectName, DEFAULT_PUT_OBJECT_EXPIRES);
    }

    @Override
    public String preSignedPutObject(String bucketName, String objectName, Integer expires) {
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expires)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch(Exception exception) {
            log.error("获取文件上传链接失败：", exception);
            throw new OssException(ResultEnum.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public String preSignedGetObject(String bucketName, String objectName) {
        return preSignedGetObject(bucketName, objectName, DEFAULT_GET_OBJECT_EXPIRES);
    }

    @Override
    public String preSignedGetObject(String bucketName, String objectName, Integer expires) {
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expires)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch(Exception exception) {
            log.error("获取文件下载链接失败：", exception);
            throw new OssException(ResultEnum.FILE_DOWNLOAD_FAILED);
        }
    }
}
