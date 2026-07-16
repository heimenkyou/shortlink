package cn.luowb.shortlink.admin.remote.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接地区监控响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短链接地区监控响应参数")
public class ShortLinkStatsLocaleCNRespDTO {

    /**
     * 统计
     */
    @Schema(description = "统计")
    private Integer cnt;

    /**
     * 地区
     */
    @Schema(description = "地区")
    private String locale;

    /**
     * 占比
     */
    @Schema(description = "占比")
    private Double ratio;
}
