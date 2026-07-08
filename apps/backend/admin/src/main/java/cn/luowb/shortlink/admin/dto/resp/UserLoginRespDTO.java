package cn.luowb.shortlink.admin.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录响应参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录响应参数")
public class UserLoginRespDTO {

    @Schema(description = "登录token")
    private String token;
}