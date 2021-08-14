package com.bjfu.fortree.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * jwt工具类
 */
@Slf4j
public class JwtUtil {

    private static final String JWT_ISSUER = "auth0";
    private static final Algorithm HMAC256_ALGORITHM = Algorithm.HMAC256("secret");

    public static String generateToken(Map<String, String> map) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date());
        cd.add(Calendar.DATE, 1);
        return generateToken(cd.getTime(), map);
    }

    public static String generateToken(Date expiresAt, Map<String, String> map) {
        try {
            JWTCreator.Builder builder = JWT.create()
                    .withIssuer(JWT_ISSUER)
                    .withExpiresAt(expiresAt);
            map.forEach(builder::withClaim);
            return builder.sign(HMAC256_ALGORITHM);
        } catch (JWTCreationException exception) {
            log.error("token生成异常", exception);
            throw new BizException(ResultEnum.TOKEN_GENERATE_FAILED);
        }
    }

    public static Map<String, Claim> verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(HMAC256_ALGORITHM)
                    .withIssuer(JWT_ISSUER)
                    .build();
            return verifier.verify(token).getClaims();
        } catch (JWTVerificationException exception) {
            log.error("token认证失败", exception);
            throw new BizException(ResultEnum.TOKEN_WRONG);
        }
    }
}

