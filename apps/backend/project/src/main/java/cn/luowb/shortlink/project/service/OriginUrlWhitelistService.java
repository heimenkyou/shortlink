package cn.luowb.shortlink.project.service;

/**
 * 原始链接白名单校验服务
 */
public interface OriginUrlWhitelistService {

    /**
     * 校验原始链接是否命中白名单
     *
     * @param originUrl 原始链接
     */
    void validate(String originUrl);
}
