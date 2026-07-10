package cn.luowb.shortlink.project.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短链接创建响应实体
 */
@Data
@Schema(description = "短链接创建响应实体")
public class LinkCreateRespDTO {
    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 原始链接
     */
    @Schema(description = "原始链接")
    private String originUrl;

    /**
     * 完整短链接
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;
}