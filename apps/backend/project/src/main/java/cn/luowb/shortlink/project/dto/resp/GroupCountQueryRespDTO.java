package cn.luowb.shortlink.project.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GroupCountQueryRespDTO {
    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 分组下的短链接数量
     */
    @Schema(description = "分组下的短链接数量")
    private Integer shortLinkCount;
}
