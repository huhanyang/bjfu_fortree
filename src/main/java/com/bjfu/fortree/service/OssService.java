package com.bjfu.fortree.service;

import java.io.InputStream;

/**
 * oss文件相关操作
 *
 * @author warthog
 */
public interface OssService {

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param stream     输入流
     */
    void putObject(String bucketName, String objectName, InputStream stream);

    /**
     * 下载文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 输入流
     */
    InputStream getObject(String bucketName, String objectName);

    /**
     * 生成一个上传文件用的URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 上传文件用的url
     */
    String preSignedPutObject(String bucketName, String objectName);

    /**
     * 生成一个上传文件用的URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires    url过期时间
     * @return 上传文件用的url
     */
    String preSignedPutObject(String bucketName, String objectName, Integer expires);

    /**
     * 生成一个下载文件用的URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 下载文件用的URL
     */
    String preSignedGetObject(String bucketName, String objectName);

    /**
     * 生成一个下载文件用的URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires    url过期时间
     * @return 下载文件用的URL
     */
    String preSignedGetObject(String bucketName, String objectName, Integer expires);

}
