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

    private static final String DEFAULT_VALUE = "未知";

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
            return DEFAULT_VALUE;
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

    /**
     * 从 HTTP 请求中提取并清洗客户端浏览器名称。
     *
     * @param request HTTP 请求对象
     * @return 规范化后的浏览器大类名称
     */
    public static String extractBrowser(HttpServletRequest request) {
        if (request == null) {
            return DEFAULT_VALUE;
        }

        UserAgent ua = UserAgentUtil.parse(request.getHeader("User-Agent"));
        if (ua == null || ua.getBrowser() == null || ua.getBrowser().isUnknown()) {
            return DEFAULT_VALUE;
        }

        String browserName = ua.getBrowser().getName();
        String lowerBrowser = browserName.toLowerCase();

        // 1. 微信生态（企业微信、微信PC、微信移动端、小程序）
        if (lowerBrowser.contains("micromessenger")
                || lowerBrowser.contains("wxwork")
                || lowerBrowser.contains("windowswechat")
                || lowerBrowser.contains("miniprogram")) {
            return "WeChat";
        }

        // 2. 钉钉生态（钉钉PC、钉钉内置）
        if (lowerBrowser.contains("dingtalk")) {
            return "DingTalk";
        }

        // 3. 微软 Edge 与旧版 IE 家族
        if (lowerBrowser.contains("edge") || lowerBrowser.contains("msedge")) {
            return "Edge";
        }
        if (lowerBrowser.contains("msie") || lowerBrowser.contains("iemobile")) {
            return "IE";
        }

        // 4. 国内及其他主流浏览器标准化命名
        if (lowerBrowser.contains("qqbrowser")) {
            return "QQ Browser";
        }
        if (lowerBrowser.contains("ucbrowser")) {
            return "UC Browser";
        }
        if (lowerBrowser.contains("miuibrowser")) {
            return "Miui Browser";
        }
        if (lowerBrowser.contains("chrome")) {
            return "Chrome";
        }
        if (lowerBrowser.contains("firefox")) {
            return "Firefox";
        }
        if (lowerBrowser.contains("safari")) {
            return "Safari";
        }
        if (lowerBrowser.contains("alipay")) {
            return "Alipay";
        }
        if (lowerBrowser.contains("taobao")) {
            return "Taobao";
        }
        if (lowerBrowser.contains("quark")) {
            return "Quark";
        }
        if (lowerBrowser.contains("baidu")) {
            return "Baidu";
        }
        if (lowerBrowser.contains("opera")) {
            return "Opera";
        }
        return browserName;
    }
}