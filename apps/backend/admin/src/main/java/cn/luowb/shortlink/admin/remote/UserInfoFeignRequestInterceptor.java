package cn.luowb.shortlink.admin.remote;

import cn.luowb.shortlink.common.biz.user.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

/**
 * 向下游服务透传当前用户信息。
 */
@Component
public class UserInfoFeignRequestInterceptor implements RequestInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String REAL_NAME_HEADER = "X-Real-Name";

    /**
     * 将当前用户上下文写入 Feign 请求头。
     *
     * @param template Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate template) {
        Long userId = UserContext.getUserId();
        String username = UserContext.getUsername();
        if (userId == null || !StringUtils.hasText(username)) {
            return;
        }
        template.header(USER_ID_HEADER, userId.toString());
        template.header(USERNAME_HEADER, UriUtils.encode(username, StandardCharsets.UTF_8));
        String realName = UserContext.getRealName();
        if (StringUtils.hasText(realName)) {
            template.header(REAL_NAME_HEADER, UriUtils.encode(realName, StandardCharsets.UTF_8));
        }
    }
}
