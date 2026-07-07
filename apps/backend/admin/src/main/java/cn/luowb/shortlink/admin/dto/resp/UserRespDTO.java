package cn.luowb.shortlink.admin.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户返回实体
 */
@Data
@Schema(description = "用户响应实体")
public class UserRespDTO {
    /**
     * 主键 ID
     */
    @Schema(description = "主键 ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 手机号(加密)
     */
    @Schema(description = "手机号(加密)")
    private String phone;

    /**
     * 邮箱(加密)
     */
    @Schema(description = "邮箱(加密)")
    private String mail;

    /**
     * 注销时间戳
     */
    @Schema(description = "注销时间戳")
    private Long deletionTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
