package cn.luowb.shortlink.admin.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短链接分组修改请求参数
 */
@Data
@Schema(description = "短链接分组修改请求参数")
public class GroupUpdateReqDTO {
    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String name;
}