package cn.luowb.shortlink.common.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RedisCacheKeyEnum {
    LOCK_USER_REGISTER_KEY("short-link:lock_user-register:%s"),
    USER_SESSION_KEY("short-link:user:session:%s"),
    SESSION_USER_KEY("short-link:session:%s"),
    /**
     * 用户信息缓存 Key
     */
    USER_INFO_KEY("short-link:user:info:%s"),
    /**
     * 短链接分组创建锁前缀 Key
     */
    GROUP_CREATE_LOCK_KEY("short-link:lock_group:create:%s"),

    /**
     * 短链接跳转前缀 Key
     */
    GOTO_SHORT_LINK_KEY("short-link:goto_%s"),
    /**
     * 短链接跳转锁前缀 Key
     */
    LOCK_GOTO_SHORT_LINK_KEY("short-link:lock_goto_%s"),

    /**
     * 短链接访问统计独立访问数 Key
     */
    LINK_ACCESS_STATS_UV_KEY("short-link:stats:uv:%s:%s"),
    /**
     * 短链接访问统计独立IP数 Key
     */
    LINK_ACCESS_STATS_UIP_KEY("short-link:stats:uip:%s:%s"),
    /**
     * 消息队列消费幂等状态 Key
     */
    MESSAGE_QUEUE_IDEMPOTENT_KEY("short-link:mq:idempotent:%s"),
    /**
     * 原始链接白名单启用状态 Key
     */
    ORIGIN_URL_WHITELIST_ENABLED_KEY("short-link:config:origin-url-whitelist:enabled"),
    /**
     * 用户请求频控 Key
     */
    USER_FLOW_RISK_CONTROL_KEY("short-link:user-flow-risk-control:%s");

    private final String template;

    public String getKey(Object... params) {
        return String.format(this.template, params);
    }
}
