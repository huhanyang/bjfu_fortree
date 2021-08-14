package com.bjfu.fortree.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加密工具类
 */
public class EncryptionUtil {

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    /**
     * md5的盐
     */
    private static final String MD5_SALT = "md5_md5_md5";

    public static String base64Encode(String source) {
        return base64Encoder.encodeToString(source.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Decode(String source) {
        return Arrays.toString(base64Decoder.decode(source.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * md5加密(不可逆)
     *
     * @param source 源字符串
     * @return 加盐并进行md5加密后的字符串
     */
    public static String md5Encode(String source) {
        String saltedSource = source + MD5_SALT;
        return DigestUtils.md5DigestAsHex(saltedSource.getBytes(StandardCharsets.UTF_8));
    }

}

