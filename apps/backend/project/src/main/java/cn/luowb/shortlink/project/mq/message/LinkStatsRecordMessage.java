package cn.luowb.shortlink.project.mq.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 短链接访问统计消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkStatsRecordMessage {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * UV 标识
     */
    private String uvFlag;

    /**
     * 访问 IP
     */
    private String ip;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 网络类型
     */
    private String network;

    /**
     * 访问时间
     */
    private LocalDateTime occurredAt;
}
