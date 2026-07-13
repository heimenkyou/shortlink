package cn.luowb.shortlink.admin.remote.dto.req;

import cn.luowb.shortlink.common.dto.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "回收站短链接分页查询请求参数")
public class TrashLinkPageReqDTO extends BasePageQuery {

    /**
     * 分组标识
     */
    @Schema(description = "分组标识列表")
    @NotEmpty(message = "分组标识列表不能为空")
    private List<String> gidList;
}
