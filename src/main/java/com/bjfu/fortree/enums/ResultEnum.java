package com.bjfu.fortree.enums;

import lombok.Getter;

/**
 * 接口返回信息的枚举类
 * @author warthog
 */
@Getter
public enum ResultEnum {

    //1xx
    SUCCESS(101, "成功"),

    //2xx 接口相关错误
    PARAM_WRONG(201, "参数错误"),
    NEED_TO_LOGIN(202, "需要登录"),
    REQUIRE_ADMIN(203, "只有管理员才可操作"),
    FREQUENT_VISITS(204, "访问过于频繁"),

    //3xx 登录注册相关
    ACCOUNT_NOT_EXIST_OR_PASSWORD_WRONG(301, "账号不存在或密码错误"),
    ACCOUNT_EXIST(302, "账号已存在"),

    //4xx 参数错误
    SESSION_USER_NOT_EXIST(401, "用户不存在"),
    WOODLAND_NOT_EXIST(402, "林地不存在"),
    RECORD_NOT_EXIST(403, "记录不存在"),
    APPLYJOB_NOT_EXIST(404, "申请不存在"),
    TREE_IS_NOT_IN_RECORD(405, "树木不属于此记录"),
    APPLYJOB_STATE_CHANGE_NOT_ALLOWED(406, "不允许的申请状态变更"),

    //5xx 系统相关错误
    UNKNOWN_ERROR(501, "未知错误"),
    USER_SESSION_WRONG(502, "session中用户账号信息错误");

    private final int code;
    private final String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
