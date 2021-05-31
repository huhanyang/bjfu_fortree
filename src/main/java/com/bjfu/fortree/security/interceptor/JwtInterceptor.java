package com.bjfu.fortree.security.interceptor;

import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.pojo.dto.user.UserDTO;
import com.bjfu.fortree.security.annotation.RequireAdmin;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.security.annotation.RequireUser;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.util.JwtUtil;
import com.bjfu.fortree.util.ResponseUtil;
import com.bjfu.fortree.util.UserInfoContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    /**
     * 进入controller前检查权限
     * @param request http请求
     * @param response http响应
     * @param handler 处理器
     * @return 是否可以通过拦截器
     * @throws Exception 拦截器出现异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isHandlerMethod = handler.getClass().isAssignableFrom(HandlerMethod.class);
        if(isHandlerMethod) {
            HandlerMethod handlerMethod= ((HandlerMethod)handler);
            // 获取接口上的访问权限控制注解
            boolean requireLogin = (handlerMethod.getMethodAnnotation(RequireLogin.class) != null);
            boolean requireUser = handlerMethod.getMethodAnnotation(RequireUser.class) != null;
            boolean requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class) != null;
            requireLogin = requireLogin || requireUser || requireAdmin;
            // 尝试获取用户登录信息
            UserDTO userInfo = Optional.ofNullable(request.getHeader("Authorization"))
                    .map(token -> token.substring(7)) // 前缀"Bearer "清除
                    .map(JwtUtil::verifyToken)
                    .map(claimMap -> claimMap.get("userAccount").asString())
                    .map(account -> userService.getInfo(account))
                    .orElse(null);
            // 需要登录则检查是否登录以及是否被封号
            if(requireLogin) {
                if(userInfo == null) {
                    ResponseUtil.writeResultToResponse(ResultEnum.NEED_TO_LOGIN, response);
                    return false;
                }
                if(UserStateEnum.BANNED.equals(userInfo.getState())){
                    ResponseUtil.writeResultToResponse(ResultEnum.ACCOUNT_BANNED, response);
                    return false;
                }
                UserInfoContextUtil.setUserInfo(userInfo);
            }
            // 判断是否为用户
            if(requireUser) {
                if(!userInfo.getType().equals(UserTypeEnum.USER)) {
                    ResponseUtil.writeResultToResponse(ResultEnum.REQUIRE_USER, response);
                    return false;
                }
            }
            // 判断是否为管理员
            if(requireAdmin) {
                if(!userInfo.getType().equals(UserTypeEnum.ADMIN)) {
                    ResponseUtil.writeResultToResponse(ResultEnum.REQUIRE_ADMIN, response);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 清空上下文中的用户信息
        UserInfoContextUtil.clear();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
