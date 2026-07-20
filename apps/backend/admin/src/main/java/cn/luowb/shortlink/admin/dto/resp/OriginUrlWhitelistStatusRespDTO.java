package cn.luowb.shortlink.admin.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 原始链接白名单状态响应
 */
@Data
@Builder
@Schema(description = "原始链接白名单状态响应")
public class OriginUrlWhitelistStatusRespDTO {

    /**
     * 是否启用白名单校验
     */
    @Schema(description = "是否启用白名单校验")
    private Boolean enabled;

    /**
     * 支持的网站列表
     */
    @Schema(description = "支持的网站列表")
    private List<String> supportedSites;
}
