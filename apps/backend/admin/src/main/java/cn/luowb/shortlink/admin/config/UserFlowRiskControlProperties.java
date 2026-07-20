package cn.luowb.shortlink.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 用户请求频控配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.flow-risk-control")
public class UserFlowRiskControlProperties {

    /**
     * 频控时间窗口，单位秒
     */
    private int windowSeconds;

    /**
     * 时间窗口内允许的最大请求次数
     */
    private int maxRequests;
}
