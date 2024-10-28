package com.common.interceptor;

import com.common.util.JwtUtil;
import com.common.util.LoginToken;
import com.common.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 判断用户的token是否有效
 * */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");


        if(LoginToken.validateToken(token)){
            /**
             * 将用户的token对应的userId到ThreadLocal中，以便后续备用*/
            ThreadLocalUtil.set(JwtUtil.parseUserLoginToken(token).get("userId"));
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
       /**
        * 请求处理完毕，清楚当前线程的数据
        * */
        ThreadLocalUtil.remove();
    }
}
