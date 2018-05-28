package com.bfei.icrane.common.util;

import org.apache.commons.lang3.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by moying on 2018/5/28.
 */
public class IPUtils {
    /**
     * 获取请求地址IP
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (org.apache.commons.lang3.StringUtils.isNoneBlank(ip)) {
            ip = org.apache.commons.lang3.StringUtils.substringBefore(ip, ",");
        }

        return ip;
    }
}
