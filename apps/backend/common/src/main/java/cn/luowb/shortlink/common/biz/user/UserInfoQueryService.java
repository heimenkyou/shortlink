package cn.luowb.shortlink.common.biz.user;

/**
 * 按用户名加载用户信息。
 */
public interface UserInfoQueryService {

    /**
     * 查询指定用户名的用户信息。
     *
     * @param username 用户名
     * @return 用户信息，查无结果时返回 {@code null}
     */
    UserInfoDTO loadUser(String username);
}
