package cn.luowb.shortlink.admin.common.constant;

public enum RedisCacheKeyEnum {
    LOCK_USER_REGISTER_KEY("short-link:lock_user-register:%s");

    private final String template;

    RedisCacheKeyEnum(String template) {
        this.template = template;
    }

    public String getKey(Object... params) {
        return String.format(this.template, params);
    }
}
