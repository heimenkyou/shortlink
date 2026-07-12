package cn.luowb.shortlink.admin.remote.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "将链接移动到回收站请求参数")
@Data
public class TrashSaveReqDTO {
    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
