package cn.luowb.shortlink.admin.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短链接分组排序参数
 */
@Data
@Schema(description = "短链接分组排序参数")
public class GroupSortReqDTO {
    /**
     * 分组ID
     */
    @Schema(description = "分组ID")
    private String gid;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

}
