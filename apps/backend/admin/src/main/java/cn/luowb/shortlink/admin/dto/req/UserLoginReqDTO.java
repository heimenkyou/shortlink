package cn.luowb.shortlink.admin.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录请求参数
 */
@Data
@Schema(description = "用户登录请求参数")
public class UserLoginReqDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;
}