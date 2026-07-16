package cn.luowb.shortlink.project.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接访客监控响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短链接访客监控响应参数")
public class ShortLinkStatsUvRespDTO {

    /**
     * 统计
     */
    @Schema(description = "统计")
    private Integer cnt;

    /**
     * 访客类型
     */
    @Schema(description = "访客类型")
    private String uvType;

    /**
     * 占比
     */
    @Schema(description = "占比")
    private Double ratio;
}
