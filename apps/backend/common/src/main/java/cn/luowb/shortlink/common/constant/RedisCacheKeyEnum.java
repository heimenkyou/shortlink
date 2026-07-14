package cn.luowb.shortlink.common.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RedisCacheKeyEnum {
    LOCK_USER_REGISTER_KEY("short-link:lock_user-register:%s"),
    USER_SESSION_KEY("short-link:user:session:%s"),
    SESSION_USER_KEY("short-link:session:%s"),

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
    LINK_ACCESS_STATS_UV_KEY("short-link:stats:uv:%s"),
    /**
     * 短链接访问统计独立IP数 Key
     */
    LINK_ACCESS_STATS_UIP_KEY("short-link:stats:uip:%s");

    private final String template;

    public String getKey(Object... params) {
        return String.format(this.template, params);
    }
}