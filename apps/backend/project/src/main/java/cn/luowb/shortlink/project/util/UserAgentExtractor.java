package cn.luowb.shortlink.project.util;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户代理（User-Agent）信息提取工具类。
 * 用途：从 HTTP 请求中提取客户端操作系统信息，用于多维度数据统计。
 */
@Slf4j
public class UserAgentExtractor {

    private static final String DEFAULT_OS = "未知";

    /**
     * 从 HttpServletRequest 中解析出客户端操作系统名称。
     * 解析失败时，返回 "未知" 占位符。
     *
     * @param request HTTP 请求对象
     * @return 过滤清洗后的操作系统名称
     */
    public static String extractOs(HttpServletRequest request) {
        UserAgent ua = UserAgentUtil.parse(request.getHeader("User-Agent"));
        if (ua == null || ua.getOs() == null || ua.getOs().isUnknown()) {
            return DEFAULT_OS;
        }
        String osName = ua.getOs().getName();
        String lowerOs = osName.toLowerCase();
        if (lowerOs.contains("windows")) {
            return "Windows";
        }
        if (lowerOs.contains("osx") || lowerOs.contains("mac")) {
            return "Mac OS";
        }
        if (lowerOs.contains("android")) {
            return "Android";
        }
        if (lowerOs.contains("iphone") || lowerOs.contains("ipad")) {
            return "iOS";
        }
        if (lowerOs.contains("linux")) {
            return "Linux";
        }
        return osName;
    }
}