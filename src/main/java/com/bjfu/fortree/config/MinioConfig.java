package com.bjfu.fortree.config;

import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.OssException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * minio配置类
 * @author warthog
 */
@Configuration
@Slf4j
public class MinioConfig {

    public static final String EXCEL_BUCKET_NAME = "export-excel";

    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.port}")
    private Integer minioPort;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 初始化或创建存储桶
     * @param minioClient minio客户端
     */
    private void initBuckets(MinioClient minioClient) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(EXCEL_BUCKET_NAME).build();
        if(!minioClient.bucketExists(bucketExistsArgs)) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(EXCEL_BUCKET_NAME)
                    .build();
            minioClient.makeBucket(makeBucketArgs);
        }
    }

    @Bean
    MinioClient minioClient() {
        MinioClient minioClient;
        try {
            minioClient = MinioClient.builder()
                    .endpoint(minioUrl, minioPort, false)
                    .credentials(accessKey, secretKey)
                    .build();
            initBuckets(minioClient);
        } catch (Exception e) {
            log.error("oss client init failed!" + e.getMessage());
            throw new OssException(ResultEnum.OSS_CLIENT_INIT_FAILED);
        }
        return minioClient;
    }
}
