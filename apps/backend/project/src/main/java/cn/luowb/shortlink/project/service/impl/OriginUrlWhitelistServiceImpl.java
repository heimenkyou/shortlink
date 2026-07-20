package cn.luowb.shortlink.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.luowb.shortlink.common.convention.exception.ClientException;
import cn.luowb.shortlink.project.config.OriginUrlWhitelistProperties;
import cn.luowb.shortlink.project.service.OriginUrlWhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.ORIGIN_URL_WHITELIST_ENABLED_KEY;

/**
 * 原始链接白名单校验服务实现
 */
@Service
@RequiredArgsConstructor
public class OriginUrlWhitelistServiceImpl implements OriginUrlWhitelistService {

    private final OriginUrlWhitelistProperties originUrlWhitelistProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 校验原始链接是否命中白名单
     *
     * @param originUrl 原始链接
     * @return {void}
     */
    @Override
    public void validate(String originUrl) {
        if (!isWhitelistEnabled()) {
            return;
        }
        Map<String, String> domainNameMap = originUrlWhitelistProperties.getDomainNameMap();
        if (domainNameMap.isEmpty()) {
            return;
        }
        String host = parseHost(originUrl);
        boolean matched = domainNameMap.keySet().stream().anyMatch(each -> matches(host, each));
        if (matched) {
            return;
        }
        throw new ClientException("当前仅支持以下网站：" + StrUtil.join("、", buildSupportedSiteNames(domainNameMap)));
    }

    /**
     * 优先读取运行时开关，未覆盖时回退到本地配置
     */
    private boolean isWhitelistEnabled() {
        String cacheValue = stringRedisTemplate.opsForValue().get(ORIGIN_URL_WHITELIST_ENABLED_KEY.getKey());
        if (StrUtil.isNotBlank(cacheValue)) {
            return Boolean.parseBoolean(cacheValue);
        }
        return originUrlWhitelistProperties.isEnabled();
    }

    /**
     * 解析 URL 域名，避免对白名单做错误匹配
     */
    private String parseHost(String originUrl) {
        try {
            URI uri = URI.create(originUrl);
            String host = uri.getHost();
            if (StrUtil.isBlank(host)) {
                throw new ClientException("原始链接格式错误，请输入完整的网站链接");
            }
            return host.toLowerCase(Locale.ROOT);
        } catch (IllegalArgumentException ex) {
            throw new ClientException("原始链接格式错误，请输入完整的网站链接");
        }
    }

    /**
     * 通配符只允许匹配子域名，避免根域名误放行
     */
    private boolean matches(String host, String pattern) {
        String normalizedPattern = normalizePattern(pattern);
        if (normalizedPattern.startsWith("*.")) {
            String suffix = normalizedPattern.substring(2);
            return host.endsWith("." + suffix);
        }
        return StrUtil.equals(host, normalizedPattern);
    }

    /**
     * 去重后保留配置顺序，保证提示内容稳定
     */
    private Set<String> buildSupportedSiteNames(Map<String, String> domainNameMap) {
        Map<String, String> supportedSiteMap = new LinkedHashMap<>();
        domainNameMap.forEach((pattern, siteName) -> {
            String normalizedPattern = normalizePattern(pattern);
            String canonicalDomain = StrUtil.removePrefix(normalizedPattern, "*.");
            boolean wildcardPattern = normalizedPattern.startsWith("*.");
            if (!wildcardPattern || !supportedSiteMap.containsKey(canonicalDomain)) {
                supportedSiteMap.put(canonicalDomain, siteName);
            }
        });
        Set<String> supportedSiteNames = new LinkedHashSet<>(supportedSiteMap.values());
        if (CollUtil.isNotEmpty(supportedSiteNames)) {
            return supportedSiteNames;
        }
        return new LinkedHashSet<>(domainNameMap.keySet());
    }

    /**
     * 兼容 yml 中对通配符键的方括号写法
     */
    private String normalizePattern(String pattern) {
        return StrUtil.removeSuffix(StrUtil.removePrefix(pattern, "["), "]")
                .toLowerCase(Locale.ROOT);
    }
}
