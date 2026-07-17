package cn.luowb.shortlink.admin.remote.dto.req;

import cn.luowb.shortlink.common.dto.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "短链接分页查询请求参数")
public class LinkPageReqDTO extends BasePageQuery {

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 排序标识
     */
    @Schema(description = "排序标识")
    private String orderTag;
}
