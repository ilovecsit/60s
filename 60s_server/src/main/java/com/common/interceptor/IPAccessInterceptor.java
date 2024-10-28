package com.common.interceptor;

import com.common.pojo.Record.IPAccessRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于限制单个ip的访问速率
 * */
@Component
public class IPAccessInterceptor implements HandlerInterceptor {

    private Map<String, IPAccessRecord> ipAccessRecords = new ConcurrentHashMap<>();
    @Value("${rateLimiter.ipMaxVisitRate}")
    private int ipMaxVisitRate;




    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = getRemoteIp(request);
        IPAccessRecord record = ipAccessRecords.computeIfAbsent(ip, k -> new IPAccessRecord());
        record.recordAccess();
        if (record.getRate() != null && record.getRate() > ipMaxVisitRate) {
            response.setStatus(429);
            return false;
        }
        return true;
    }

    private String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}