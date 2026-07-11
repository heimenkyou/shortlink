package cn.luowb.shortlink.admin.remote.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短链接创建请求参数
 */
@Data
@Schema(description = "短链接创建请求参数")
public class LinkCreateReqDTO {
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
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 创建类型 0：接口 1：控制台
     */
    @Schema(description = "创建类型 0：接口 1：控制台")
    private Integer createdType;

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