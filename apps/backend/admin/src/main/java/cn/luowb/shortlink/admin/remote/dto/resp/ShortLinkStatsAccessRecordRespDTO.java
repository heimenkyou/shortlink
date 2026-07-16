package cn.luowb.shortlink.admin.remote.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接监控访问记录响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短链接监控访问记录响应参数")
public class ShortLinkStatsAccessRecordRespDTO {

    /**
     * 访客类型
     */
    @Schema(description = "访客类型")
    private String uvType;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * IP
     */
    @Schema(description = "IP")
    private String ip;

    /**
     * 访问网络
     */
    @Schema(description = "访问网络")
    private String network;

    /**
     * 访问设备
     */
    @Schema(description = "访问设备")
    private String device;

    /**
     * 地区
     */
    @Schema(description = "地区")
    private String locale;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private String user;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "访问时间")
    private Date createTime;
}
