package com.bjfu.fortree.enums;

import lombok.Getter;

/**
 * 接口返回信息的枚举类
 *
 * @author warthog
 */
@Getter
public enum ResultEnum {

    //1xx
    SUCCESS(101, "成功"),

    //2xx 接口相关错误
    PARAM_WRONG(201, "参数错误"),
    NEED_TO_LOGIN(202, "需要登录"),
    REQUIRE_USER(203, "只有普通用户才可操作"),
    REQUIRE_ADMIN(204, "只有管理员才可操作"),
    FREQUENT_VISITS(204, "访问过于频繁"),

    //3xx 登录注册相关
    ACCOUNT_NOT_EXIST_OR_PASSWORD_WRONG(301, "账号不存在或密码错误"),
    ACCOUNT_EXIST(302, "账号已存在"),
    ACCOUNT_BANNED(303, "账号已被封禁"),
    TOKEN_GENERATE_FAILED(304, "Token生成出错"),
    TOKEN_WRONG(305, "Token错误"),
    PASSWORD_WRONG(306, "密码错误"),
    ACCOUNT_NOT_UNACTIVE(307, "账号状态非未激活状态"),
    ACCOUNT_UNACTIVE(307, "账号未激活"),

    //4xx 参数错误
    USER_NOT_EXIST(401, "用户不存在"),
    WOODLAND_NOT_EXIST(402, "林地不存在"),
    RECORD_NOT_EXIST(403, "记录不存在"),
    APPLYJOB_NOT_EXIST(404, "申请不存在"),
    TREE_IS_NOT_IN_RECORD(405, "树木不属于此记录"),

    //5xx 系统相关错误
    UNKNOWN_ERROR(501, "未知错误"),
    JWT_USER_INFO_ERROR(502, "JWT中存储的信息错误"),
    USER_INFO_CONTEXT_WRONG(503, "用户上下文出错"),
    FILE_EXPORT_FAILED(503, "文件导出出错"),
    FILE_UPLOAD_FAILED(504, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(505, "文件下载失败"),
    OSS_CLIENT_INIT_FAILED(506, "oss客户端初始化失败"),

    //6xx 业务错误
    NOT_APPLY_USER(601, "非申请人"),
    APPLYJOB_STATE_CHANGE_NOT_ALLOWED(602, "不允许的申请状态变更"),
    PERMISSION_DENIED(603, "获取信息权限不足"),
    FILE_NOT_EXIST_OR_EXPIRES(604, "文件过期或不存在"),

    ;

    private final int code;
    private final String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
