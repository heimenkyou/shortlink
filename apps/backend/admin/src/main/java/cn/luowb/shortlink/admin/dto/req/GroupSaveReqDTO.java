package cn.luowb.shortlink.admin.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短链接分组新增请求参数
 */
@Data
@Schema(description = "短链接分组新增请求参数")
public class GroupSaveReqDTO {
    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String name;
}