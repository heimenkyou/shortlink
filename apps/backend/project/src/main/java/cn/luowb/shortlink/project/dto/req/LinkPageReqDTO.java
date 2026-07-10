package cn.luowb.shortlink.project.dto.req;

import cn.luowb.shortlink.common.dto.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LinkPageReqDTO extends BasePageQuery {

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;
}
