package cn.luowb.shortlink.admin.remote.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接高频访问IP监控响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短链接高频访问IP监控响应参数")
public class ShortLinkStatsTopIpRespDTO {

    /**
     * 统计
     */
    @Schema(description = "统计")
    private Integer cnt;

    /**
     * IP
     */
    @Schema(description = "IP")
    private String ip;
}
