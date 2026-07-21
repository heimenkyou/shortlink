package cn.luowb.shortlink.project.common.porperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短链接访问统计 RocketMQ 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.mq")
public class LinkStatsRocketMQProperties {

    /**
     * 消费者分组
     */
    private String consumerGroup;

    /**
     * 访问统计主题
     */
    private String linkStatsTopic;
}
