package cn.luowb.shortlink.project.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkPageRespDTO {
    /**
     * 主键 ID
     */
    @Schema(description = "主键 ID")
    private Long id;

    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domain;

    /**
     * 短链接
     */
    @Schema(description = "短链接")
    private String shortUri;

    /**
     * 完整短链接
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;

    /**
     * 原始链接
     */
    @Schema(description = "原始链接")
    private String originUrl;

    /**
     * 点击量
     */
    @Schema(description = "点击量")
    private Integer clickNum;

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 网站图标
     */
    @Schema(description = "网站图标")
    private String favicon;

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

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 历史PV
     */
    @Schema(description = "历史PV")
    private Integer totalPv;

    /**
     * 今日PV
     */
    @Schema(description = "今日PV")
    private Integer todayPv;

    /**
     * 历史UV
     */
    @Schema(description = "历史UV")
    private Integer totalUv;

    /**
     * 今日UV
     */
    @Schema(description = "今日UV")
    private Integer todayUv;

    /**
     * 历史UIP
     */
    @Schema(description = "历史UIP")
    private Integer totalUip;

    /**
     * 今日UIP
     */
    @Schema(description = "今日UIP")
    private Integer todayUip;
}
