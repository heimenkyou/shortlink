package cn.luowb.shortlink.admin.service;

import cn.luowb.shortlink.admin.dto.resp.OriginUrlWhitelistStatusRespDTO;

/**
 * 原始链接白名单管理服务
 */
public interface OriginUrlWhitelistManageService {

    /**
     * 查询白名单状态
     *
     * @return 白名单状态
     */
    OriginUrlWhitelistStatusRespDTO getStatus();

    /**
     * 校验开发者令牌
     *
     * @param manageToken 开发者令牌
     * @return {void}
     */
    void validateManageToken(String manageToken);

    /**
     * 更新白名单状态
     *
     * @param enabled 是否启用
     * @return {void}
     */
    void updateStatus(Boolean enabled);
}
