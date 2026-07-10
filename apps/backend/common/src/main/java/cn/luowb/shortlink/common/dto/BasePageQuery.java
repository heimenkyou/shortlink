package cn.luowb.shortlink.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "基础分页请求参数")
public class BasePageQuery {
    @Schema(description = "当前页", example = "1")
    private long current = 1;

    @Schema(description = "每页条数", example = "10")
    private long size = 10;
}