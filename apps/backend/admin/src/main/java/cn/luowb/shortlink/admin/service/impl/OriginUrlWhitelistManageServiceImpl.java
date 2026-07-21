package cn.luowb.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.luowb.shortlink.admin.config.OriginUrlWhitelistProperties;
import cn.luowb.shortlink.admin.dto.resp.OriginUrlWhitelistStatusRespDTO;
import cn.luowb.shortlink.admin.service.OriginUrlWhitelistManageService;
import cn.luowb.shortlink.common.convention.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.ORIGIN_URL_WHITELIST_ENABLED_KEY;

/**
 * 原始链接白名单管理服务实现
 */
@Service
@RequiredArgsConstructor
public class OriginUrlWhitelistManageServiceImpl implements OriginUrlWhitelistManageService {

    private final OriginUrlWhitelistProperties originUrlWhitelistProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 查询白名单状态
     *
     * @return 白名单状态
     */
    @Override
    public OriginUrlWhitelistStatusRespDTO getStatus() {
        return OriginUrlWhitelistStatusRespDTO.builder()
                .enabled(resolveEnabled())
                .supportedSites(buildSupportedSites())
                .build();
    }

    /**
     * 校验开发者令牌
     *
     * @param manageToken 开发者令牌
     * @return {void}
     */
    @Override
    public void validateManageToken(String manageToken) {
        if (StrUtil.isBlank(originUrlWhitelistProperties.getManageToken())) {
            throw new ClientException("未配置白名单管理令牌");
        }
        if (!StrUtil.equals(originUrlWhitelistProperties.getManageToken(), manageToken)) {
            throw new ClientException("白名单管理令牌无效");
        }
    }

    /**
     * 更新白名单状态
     *
     * @param enabled 是否启用
     * @return {void}
     */
    @Override
    public void updateStatus(Boolean enabled) {
        if (enabled == null) {
            throw new ClientException("白名单开关不能为空");
        }
        stringRedisTemplate.opsForValue().set(ORIGIN_URL_WHITELIST_ENABLED_KEY.getKey(), enabled.toString());
    }

    /**
     * 优先读取运行时开关，避免重启服务才能生效
     */
    private boolean resolveEnabled() {
        String cacheValue = stringRedisTemplate.opsForValue().get(ORIGIN_URL_WHITELIST_ENABLED_KEY.getKey());
        if (StrUtil.isNotBlank(cacheValue)) {
            return Boolean.parseBoolean(cacheValue);
        }
        return originUrlWhitelistProperties.isEnabled();
    }

    /**
     * 去重后保留配置顺序，保证后台展示稳定
     */
    private List<String> buildSupportedSites() {
        Map<String, String> supportedSiteMap = new LinkedHashMap<>();
        originUrlWhitelistProperties.getDomainNameMap().forEach((pattern, siteName) -> {
            String normalizedPattern = normalizePattern(pattern);
            String canonicalDomain = StrUtil.removePrefix(normalizedPattern, "*.");
            boolean wildcardPattern = normalizedPattern.startsWith("*.");
            if (!wildcardPattern || !supportedSiteMap.containsKey(canonicalDomain)) {
                supportedSiteMap.put(canonicalDomain, siteName);
            }
        });
        Set<String> supportedSites = new LinkedHashSet<>(supportedSiteMap.values());
        if (CollUtil.isEmpty(supportedSites)) {
            supportedSites = new LinkedHashSet<>(originUrlWhitelistProperties.getDomainNameMap().keySet());
        }
        return new ArrayList<>(supportedSites);
    }

    /**
     * 兼容 yml 中对通配符键的方括号写法
     */
    private String normalizePattern(String pattern) {
        return StrUtil.removeSuffix(StrUtil.removePrefix(pattern, "["), "]")
                .toLowerCase(Locale.ROOT);
    }
}
