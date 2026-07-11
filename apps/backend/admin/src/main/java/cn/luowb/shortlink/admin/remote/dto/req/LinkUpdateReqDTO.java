package cn.luowb.shortlink.admin.remote.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短链接修改请求参数
 */
@Data
@Schema(description = "短链接修改请求参数")
public class LinkUpdateReqDTO {
    /**
     * 旧分组标识(用来定位）
     */
    @Schema(description = "旧分组标识")
    private String oldGid;

    /**
     * 新分组标识（修改后的值）
     */
    @Schema(description = "新分组标识")
    private String newGid;

    /**
     * 完整短链接（用来定位）
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;

    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domain;

    /**
     * 原始链接
     */
    @Schema(description = "原始链接")
    private String originUrl;


    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    @Schema(description = "有效期类型 0：永久有效 1：用户自定义")
    private Integer validDateType;

    /**
     * 有效期
     */
    @Schema(description = "有效期")
    private LocalDateTime validDate;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String describe;
}