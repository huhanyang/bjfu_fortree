package com.bjfu.fortree.security.interceptor;

import com.bjfu.fortree.pojo.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.security.annotation.RequireAdmin;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.util.ResponseUtil;
import com.bjfu.fortree.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 接口访问权限控制拦截器
 * @author warthog
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        boolean isHandlerMethod = handler.getClass().isAssignableFrom(HandlerMethod.class);
        if(isHandlerMethod) {
            HandlerMethod handlerMethod= ((HandlerMethod)handler);
            boolean requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class) != null;
            boolean requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class) != null;
            UserWithAuthoritiesDTO userInfo = SessionUtil.getUserInfo(session);
            if(requireLogin || requireAdmin) {
                if(userInfo == null) {
                    ResponseUtil.writeResultToResponse(ResultEnum.NEED_TO_LOGIN, response);
                    return false;
                }
                if(userRepository.existsByAccountAndState(userInfo.getAccount(), UserStateEnum.BANNED)){
                    ResponseUtil.writeResultToResponse(ResultEnum.ACCOUNT_BANNED, response);
                    return false;
                }
            }
            if(requireAdmin) {
                boolean isAdmin = userInfo.getType().equals(UserTypeEnum.ADMIN);
                if(!isAdmin) {
                    ResponseUtil.writeResultToResponse(ResultEnum.REQUIRE_ADMIN, response);
                    return false;
                }
            }
        }
        return true;
    }
}
