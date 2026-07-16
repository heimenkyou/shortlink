package cn.luowb.shortlink.project.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接访问设备监控响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短链接访问设备监控响应参数")
public class ShortLinkStatsDeviceRespDTO {

    /**
     * 统计
     */
    @Schema(description = "统计")
    private Integer cnt;

    /**
     * 设备类型
     */
    @Schema(description = "设备类型")
    private String device;

    /**
     * 占比
     */
    @Schema(description = "占比")
    private Double ratio;
}
