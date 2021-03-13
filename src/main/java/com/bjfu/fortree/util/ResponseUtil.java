package com.bjfu.fortree.util;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.pojo.vo.BaseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * HTTP Response工具类
 * @author warthog
 */
public class ResponseUtil {

    public static void writeResultToResponse(ResultEnum resultEnum, HttpServletResponse response) throws IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        BaseResult<Void> result = new BaseResult<>(resultEnum);
        writer.println(JSONObject.toJSON(result));
    }

}
