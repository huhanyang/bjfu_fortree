package com.bjfu.fortree.util;

import com.bjfu.fortree.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.enums.SessionKeyEnum;

import javax.servlet.http.HttpSession;

/**
 * HTTP Session工具类
 * @author warthog
 */
public class SessionUtil {

    private static final int SESSION_MAX_INACTIVE_INTERVAL = 7 * 3600;

    public static void initSession(HttpSession session, UserWithAuthoritiesDTO userWithAuthoritiesDTO, boolean remember) {
        session.setAttribute(SessionKeyEnum.USER_INFO.name(), userWithAuthoritiesDTO);
        if(remember) {
            session.setMaxInactiveInterval(SESSION_MAX_INACTIVE_INTERVAL);
        }
    }

    public static void deleteSession(HttpSession session) {
        session.invalidate();
    }

    public static boolean existSession(HttpSession session) {
        return session.getAttribute(SessionKeyEnum.USER_INFO.name()) != null;
    }

    public static UserWithAuthoritiesDTO getUserInfo(HttpSession session) {
        return (UserWithAuthoritiesDTO) session.getAttribute(SessionKeyEnum.USER_INFO.name());
    }

}
