package cn.luowb.shortlink.project.common.porperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 原始链接白名单配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security.whitelist")
public class OriginUrlWhitelistProperties {

    /**
     * 是否启用白名单校验
     */
    private boolean enabled;

    /**
     * 域名白名单，value 用于给前端展示支持的网站
     */
    private Map<String, String> domainNameMap = new LinkedHashMap<>();
}
