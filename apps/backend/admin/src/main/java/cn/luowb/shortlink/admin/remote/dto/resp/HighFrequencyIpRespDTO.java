package cn.luowb.shortlink.admin.remote.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 高频访问 IP 响应参数
 */
@Data
@Schema(description = "高频访问 IP 响应参数")
public class HighFrequencyIpRespDTO {

    /**
     * IP 地址
     */
    @Schema(description = "IP 地址")
    private String ip;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    private Long accessCount;
}
