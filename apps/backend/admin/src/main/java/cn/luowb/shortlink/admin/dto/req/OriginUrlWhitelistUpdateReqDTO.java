package cn.luowb.shortlink.admin.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 原始链接白名单开关更新请求
 */
@Data
@Schema(description = "原始链接白名单开关更新请求")
public class OriginUrlWhitelistUpdateReqDTO {

    /**
     * 是否启用白名单校验
     */
    @Schema(description = "是否启用白名单校验")
    private Boolean enabled;
}
