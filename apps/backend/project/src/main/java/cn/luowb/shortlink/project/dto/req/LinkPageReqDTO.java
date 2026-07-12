package cn.luowb.shortlink.project.dto.req;

import cn.luowb.shortlink.common.dto.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "短链接分页查询请求参数")
public class LinkPageReqDTO extends BasePageQuery {

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    @NotBlank(message = "分组标识不能为空")
    private String gid;
}
